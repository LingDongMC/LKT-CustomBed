package com.github.lockoct.bed.command.mark.sub;

import com.github.lockoct.Main;
import com.github.lockoct.bed.listener.MarkListener;
import com.github.lockoct.bed.task.SaveTask;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.entity.BedArea;
import com.github.lockoct.entity.MarkData;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SaveSubCommandHandler extends BaseCommandHandler {
    private static SaveSubCommandHandler instance;

    public static SaveSubCommandHandler getInstance() {
        if (instance == null) {
            instance = new SaveSubCommandHandler();
        }
        return instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        // 输出帮助
        if (args.length != 2) {
            doHelp(Main.plugin, player, "cmd.markCmd.saveCmd.helpMsg");
            return;
        }

        MarkData data = MarkListener.getMarkModePlayers().get(player);
        if (data == null) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.notInMarkMode"));
            return;
        }

        // 将区域信息转换为java bean
        Location point1 = data.getMarkPoint1();
        Location point2 = data.getMarkPoint2();
        String playerId = player.getUniqueId().toString();

        // 检查合法性
        if (point1 == null || point2 == null) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.saveCmd.markPointSelectNotComplete"));
            return;
        }

        // 检查当前标记点是否在主世界
        World point1World = point1.getWorld();
        World point2World = point2.getWorld();
        if (point1World == null || point2World == null) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.cannotGetMarkPointWorld"));
            return;
        }

        if (!point1World.equals(point2World) || point1World.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.markPointsNotInMainWorld"));
            return;
        }

        // 对标记点坐标排序，统一转换成平面的左上、右下
        int topLeftZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
        int topLeftX = Math.max(point1.getBlockX(), point2.getBlockX());
        int bottomRightZ = Math.max(point1.getBlockZ(), point2.getBlockZ());
        int bottomRightX = Math.min(point1.getBlockX(), point2.getBlockX());

        // 检查当前选择区域是否存在其他人的床区域
        Dao dao = DatabaseUtil.getDao();
        if (dao == null) {
            return;
        }
        List<BedArea> coincide = dao.query(BedArea.class, Cnd
            .where("z1", "<=", bottomRightZ).and("z2", ">=", topLeftZ)
            .and("x1", ">=", bottomRightX).and("x2", "<=", topLeftX)
            .and("deleted", "=", 0)
        );
        if (coincide.size() > 0) {
            player.sendMessage(ChatColor.YELLOW + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.existOtherBed.start"));
            coincide.forEach(e -> {
                String playerName = Optional.ofNullable(Bukkit.getPlayer(UUID.fromString(e.getCreateUser()))).map(Player::getName).orElse("null");
                String sb = ChatColor.YELLOW +
                    I18nUtil.getText(Main.plugin, player, "cmd.markCmd.existOtherBed.bedName", e.getName()) +
                    I18nUtil.getText(Main.plugin, player, "cmd.markCmd.existOtherBed.bedCoordinate", e.getX1(), e.getY1(), e.getZ1(), e.getX2(), e.getY2(), e.getZ2()) +
                    I18nUtil.getText(Main.plugin, player, "cmd.markCmd.existOtherBed.owner", playerName);
                player.sendMessage(sb);
            });
            return;
        }

        // 提交床区域信息
        BedArea ba = new BedArea();
        ba.setName(args[1]);
        World world = point1.getWorld();
        assert world != null;
        ba.setWorld(world.getName());

        ba.setZ1(topLeftZ);
        ba.setX1(topLeftX);
        ba.setY1(point1.getBlockY());

        ba.setZ2(bottomRightZ);
        ba.setX2(bottomRightX);
        ba.setY2(point2.getBlockY());

        ba.setDeleted(false);
        ba.setEnabled(true);
        ba.setCreateUser(playerId);
        ba.setUpdateUser(playerId);

        // 防止重复建立保存线程任务
        if (data.getSaveTaskId() > 0) {
            return;
        }
        SaveTask task = new SaveTask(player, ba);
        int taskId = task.runTaskAsynchronously(Main.plugin).getTaskId();
        data.setSaveTaskId(taskId);
    }
}
