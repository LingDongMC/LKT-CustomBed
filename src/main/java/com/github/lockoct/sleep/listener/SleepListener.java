package com.github.lockoct.sleep.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import com.github.lockoct.Main;
import com.github.lockoct.entity.BedArea;
import com.github.lockoct.sleep.task.AllSkipNightTask;
import com.github.lockoct.sleep.task.SingleSkipNightTask;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;

import java.util.*;

public class SleepListener implements Listener {
    private final static ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    private final static HashMap<Player, Location> sleepingPlayer = new HashMap<>();
    private static SingleSkipNightTask singleSkipNightTask;
    private static AllSkipNightTask allSkipNightTask;

    static {
        // 监听玩家点击起床按钮
        // https://wiki.vg/Protocol#Player_Command
        manager.addPacketListener(new PacketAdapter(Main.plugin, ListenerPriority.NORMAL, PacketType.Play.Client.ENTITY_ACTION) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                PacketContainer playerCommandPacket = e.getPacket();
                // 获取玩家
                Player player = e.getPlayer();
                // 避免干扰玩家在普通床的起床
                if (sleepingPlayer.get(player) == null) {
                    return;
                }
                // 获取action
                EnumWrappers.PlayerAction action = playerCommandPacket.getPlayerActions().read(0);
                // 判断操作
                if (action.equals(EnumWrappers.PlayerAction.STOP_SLEEPING)) {
                    wakeUp(player);
                }
            }
        });
    }

    public static HashMap<Player, Location> getSleepingPlayer() {
        return sleepingPlayer;
    }

    public static void setSingleSkipNightTask(SingleSkipNightTask singleSkipNightTask) {
        SleepListener.singleSkipNightTask = singleSkipNightTask;
    }

    public static void setAllSkipNightTask(AllSkipNightTask allSkipNightTask) {
        SleepListener.allSkipNightTask = allSkipNightTask;
    }

    // 玩家点击自定义床方块触发
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && EquipmentSlot.HAND.equals(e.getHand())) {
            Block customBedBlock = e.getClickedBlock();
            assert customBedBlock != null;
            Player player = e.getPlayer();

            // 避免干扰普通床的使用
            if (customBedBlock.getBlockData() instanceof Bed) {
                return;
            }

            World world = player.getWorld();
            Location customBedBlockLocation = customBedBlock.getLocation();
            int x = customBedBlockLocation.getBlockX();
            int y = customBedBlockLocation.getBlockY();
            int z = customBedBlockLocation.getBlockZ();

            // 查询当前点击的是否是自定义床
            Dao dao = DatabaseUtil.getDao();
            int res = dao.count(BedArea.class, Cnd
                .where("deleted", "=", 0).and("enabled", "=", 1)
                .and("create_user", "=", player.getUniqueId().toString())
                .and("z1", "<=", z).and("z2", ">=", z)
                .and("x1", ">=", x).and("x2", "<=", x)
                .and("y1", "=", y)
            );
            if (res == 0) {
                return;
            }

            // 设置出生点
            Location newSpawnPoint = customBedBlockLocation.add(0.5, 1, 0.5);
            Optional.ofNullable(player.getBedSpawnLocation())
                .ifPresentOrElse(
                    location -> {
                        if (
                            Math.abs(location.getBlockX() - newSpawnPoint.getBlockX()) >= 1 ||
                                Math.abs(location.getBlockY() - newSpawnPoint.getBlockY()) >= 1 ||
                                Math.abs(location.getBlockZ() - newSpawnPoint.getBlockZ()) >= 1
                        ) {
                            player.setBedSpawnLocation(newSpawnPoint, true);
                            player.sendMessage(I18nUtil.getText(Main.plugin, player, "sleep.setSpawnPoint"));
                        }
                    },
                    () -> {
                        player.setBedSpawnLocation(newSpawnPoint, true);
                        player.sendMessage(I18nUtil.getText(Main.plugin, player, "sleep.setSpawnPoint"));
                    }
                );

            // 检查是否可以睡觉
            if (!this.canSleep(world, player)) {
                return;
            }
            sleep(player, customBedBlock.getLocation());
        }
    }

    // 点击普通床睡觉
    @EventHandler
    public void onEnterBed(PlayerBedEnterEvent e) {
        if (e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            sleep(e.getPlayer(), null);
        }
    }

    // 普通床起床
    @EventHandler
    public void onLeaveBed(PlayerBedLeaveEvent e) {
        wakeUp(e.getPlayer());
    }

    private boolean canSleep(World world, Player player) {
        // 检查时间、天气
        if (world.getTime() < 13000 && !world.isThundering()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(I18nUtil.getText(Main.plugin, player, "sleep.notPossibleNow")));
            return false;
        }

        // 检查附近是否有怪物
        List<Entity> nearbyEntities = player.getNearbyEntities(8, 5, 8);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof Monster) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(I18nUtil.getText(Main.plugin, player, "sleep.notSafe")));
                return false;
            }
        }
        return true;
    }

    public static void sleep(Player player, Location bedLocation) {
        World world = player.getWorld();
        if (bedLocation != null) {
            // 自定义床睡觉

            // 要让玩家进入睡眠状态，需要让客户端知道有一个床方块
            // 因此需要构建BlockChange数据包，改变某个方块为床方块（假方块）
            // https://wiki.vg/Protocol#Block_Update
            // 假方块位置为当前床方块向下一格
            Location fakeBedLocation = bedLocation.clone().add(0, -1, 0);

            PacketContainer blockPacket = manager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            // 设置假方块位置
            blockPacket.getBlockPositionModifier().write(0, new BlockPosition(fakeBedLocation.toVector()));
            // 设置方块类型
            blockPacket.getBlockData().write(0, WrappedBlockData.createData(Material.RED_BED));

            // 构建Entity Metadata数据包
            // https://wiki.vg/Protocol#Set_Entity_Metadata
            PacketContainer entityMetadataPacket = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            // 设置entityId
            entityMetadataPacket.getIntegers().write(0, player.getEntityId());
            // 设置metaData
            // https://wiki.vg/Entity_metadata#Living_Entity
            // LivingEntity继承自Entity，其中的第14个属性为玩家睡觉位置，设置这个才能触发睡觉
            WrappedDataValue sleepPosValue = new WrappedDataValue(14, WrappedDataWatcher.Registry.getBlockPositionSerializer(true), null);
            sleepPosValue.setValue(Optional.of(new BlockPosition(fakeBedLocation.toVector())));
            entityMetadataPacket.getDataValueCollectionModifier().write(0, List.of(
                // Entity中的第6个属性为玩家姿势，设置这个其他玩家才能看到该玩家躺下
                new WrappedDataValue(6, WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass()), EnumWrappers.EntityPose.SLEEPING),
                sleepPosValue
            ));

            // 发送数据包给当前世界所有玩家
            for (Player p : world.getPlayers()) {
                manager.sendServerPacket(p, blockPacket);
                manager.sendServerPacket(p, entityMetadataPacket);
            }

            // 由于假方块隐藏在地下，触发睡觉后，玩家也会进入地下
            // 为避免玩家醒来后在地下，需要把玩家tp回床方块向上一格
            player.teleport(bedLocation.clone().add(0, 1, 0));
            // 添加玩家到睡眠队列中
            sleepingPlayer.put(player, fakeBedLocation);
        } else {
            // 普通床睡觉
            sleepingPlayer.put(player, null);
        }
        // 尝试度过夜晚
        trySkipNight(world, player);
    }

    public static void wakeUp(Player player) {
        Location fakeBedLocation = sleepingPlayer.get(player);

        if (fakeBedLocation != null) {
            // 起床数据包（与进入睡眠操作相反）
            PacketContainer entityMetadataPacket = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            // 设置entityId
            entityMetadataPacket.getIntegers().write(0, player.getEntityId());
            // 设置metaData
            entityMetadataPacket.getDataValueCollectionModifier().write(0, List.of(
                // 设置玩家姿势为站立
                new WrappedDataValue(6, WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass()), EnumWrappers.EntityPose.STANDING),
                new WrappedDataValue(14, WrappedDataWatcher.Registry.getBlockPositionSerializer(true), Optional.empty())
            ));

            // 构建起床动画数据包
            // https://wiki.vg/Protocol#Entity_Animation
            PacketContainer animationPacket = manager.createPacket(PacketType.Play.Server.ANIMATION);
            animationPacket.getIntegers().write(0, player.getEntityId());
            animationPacket.getIntegers().write(1, 2);

            // 发送数据包给当前世界所有玩家
            for (Player p : player.getWorld().getPlayers()) {
                manager.sendServerPacket(p, entityMetadataPacket);
                manager.sendServerPacket(p, animationPacket);
            }
            // 还原假方块
            Block fakeBed = fakeBedLocation.getBlock();
            fakeBed.getState().update();
        }

        // 将玩家从睡眠队列中去除
        sleepingPlayer.remove(player);
        // 在全体模式下，终止正在等待度过夜晚的任务
        interruptSkipNight(player);
    }

    public static void trySkipNight(World world, Player player) {
        if (sleepingPlayer.isEmpty()) {
            return;
        }

        // 判断当前睡觉模式
        // 只要不是单独模式，就必须所有玩家睡觉才能度过夜晚
        if (Main.sleepMode.equals("single")) {
            if (singleSkipNightTask == null && player != null) {
                singleSkipNightTask = new SingleSkipNightTask(world, player);
                singleSkipNightTask.runTaskTimer(Main.plugin, 0, 5L);
            }
            return;
        }

        // 当主世界所有玩家都睡觉时，开始倒计时，倒计时结束后度过夜晚
        if (comparePlayerList(world.getPlayers().stream().filter(e -> !e.isDead() && e.isOnline()).toList(), sleepingPlayer.keySet())) {
            // 开始倒计时
            if (allSkipNightTask == null) {
                allSkipNightTask = new AllSkipNightTask(world);
                allSkipNightTask.runTaskTimerAsynchronously(Main.plugin, 0, 5L);
            }
        }
    }

    public static void skipNight(World world) {
        // 设置为白天
        // 设置白天后，使用原版床睡觉的玩家会自己起床
        world.setTime(0);
        // 判断天气，如果是雷暴天气则改为晴朗
        if (world.isThundering()) {
            world.setThundering(false);
            world.setStorm(false);
        }
        // 唤醒通过自定义床睡觉的所有人
        ArrayList<Player> sleepList = new ArrayList<>(getSleepingPlayer().keySet());
        sleepList.forEach(SleepListener::wakeUp);
    }

    public static void interruptSkipNight(Player player) {
        if (singleSkipNightTask != null && player != null) {
            singleSkipNightTask.interrupt(player);
            singleSkipNightTask = null;
        }

        if (allSkipNightTask != null) {
            allSkipNightTask.cancel();
            allSkipNightTask = null;
        }
    }

    private static boolean comparePlayerList(Collection<Player> c1, Collection<Player> c2) {
        HashSet<Player> arr1 = new HashSet<>(c1);
        HashSet<Player> arr2 = new HashSet<>(c2);
        return arr1.equals(arr2);
    }
}
