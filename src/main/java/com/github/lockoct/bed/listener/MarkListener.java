package com.github.lockoct.bed.listener;

import com.github.lockoct.Main;
import com.github.lockoct.entity.MarkData;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;

public class MarkListener implements Listener {
    private static HashMap<Player, MarkData> markModePlayers = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        // 查找进入标记状态玩家
        Player player = e.getPlayer();
        MarkData data = getMarkModePlayers().get(player);
        if (data == null) {
            return;
        }
        Block block = e.getClickedBlock();
        Action action = e.getAction();
        // 玩家点击的必须是方块，且不能是空气，不能是流体
        if (block != null && !block.isEmpty() && !block.isLiquid()) {
            // 检查玩家动作，必须是左键点击方块或右键点击方块，且右键点击方块时必须是主手
            int positionNum = 1;
            if (!action.equals(Action.LEFT_CLICK_BLOCK)) {
                if (action.equals(Action.RIGHT_CLICK_BLOCK) && EquipmentSlot.HAND.equals(e.getHand())) {
                    positionNum = 2;
                    data.setMarkPoint2(block.getLocation());
                } else {
                    return;
                }
            } else {
                data.setMarkPoint1(block.getLocation());
            }

            player.sendMessage(ChatColor.LIGHT_PURPLE + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.selectPoint", positionNum, block.getX(), block.getY(), block.getZ()));

            // 选中两个标记点后计算范围大小，查找范围中的方块数量
            Location point1 = data.getMarkPoint1();
            Location point2 = data.getMarkPoint2();

            if (point1 == null || point2 == null) {
                return;
            }

            // 检查两个标记点是否在同一平面
            if (point1.getBlockY() != point2.getBlockY()) {
                player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.yAxisInconsistency"));
                this.clearMarkPoint(player);
                return;
            }

            // 检查床区域面积是否超过900
            int area = (Math.abs(point1.getBlockX() - point2.getBlockX()) + 1) * (Math.abs(point1.getBlockZ() - point2.getBlockZ()) + 1);
            if (area > 900) {
                player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaTooLarge"));
                this.clearMarkPoint(player);
                return;
            }

            player.sendMessage(ChatColor.LIGHT_PURPLE + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaStatisticsMsg", area));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        clearMarkData(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        clearMarkData(e.getPlayer());
    }

    // 选区不满足条件时要清除选区
    private void clearMarkPoint(Player player) {
        MarkData data = markModePlayers.get(player);
        data.setMarkPoint1(null);
        data.setMarkPoint2(null);
    }

    // 清除玩家标记数据
    public static boolean clearMarkData(Player player) {
        MarkData data = markModePlayers.get(player);
        if (data != null) {
            markModePlayers.remove(player);
            return true;
        } else {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.notInMarkMode"));
            return false;
        }
    }

    public static HashMap<Player, MarkData> getMarkModePlayers() {
        if (markModePlayers == null) {
            markModePlayers = new HashMap<>();
        }
        return markModePlayers;
    }
}
