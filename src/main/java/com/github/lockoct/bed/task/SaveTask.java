package com.github.lockoct.bed.task;

import com.github.lockoct.Main;
import com.github.lockoct.bed.listener.MarkListener;
import com.github.lockoct.entity.BedArea;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Dao;

public class SaveTask extends BukkitRunnable {

    private final BedArea ba;
    private final Player player;

    public SaveTask(Player player, BedArea ba) {
        this.player = player;
        this.ba = ba;
    }

    @Override
    public void run() {
        Dao dao = DatabaseUtil.getDao();
        if (dao == null) {
            return;
        }

        BedArea baTmp = dao.insert(ba);
        if (baTmp != null) {
            player.sendMessage(ChatColor.GREEN + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.saveCmd.saveSuccessful"));
        } else {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.saveCmd.saveFailed"));
        }

        MarkListener.clearMarkData(player);
    }
}
