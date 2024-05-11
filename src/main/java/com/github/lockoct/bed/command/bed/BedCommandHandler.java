package com.github.lockoct.bed.command.bed;

import com.github.lockoct.Main;
import com.github.lockoct.bed.listener.BedListMenuListener;
import com.github.lockoct.bed.menu.BedListMenu;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BedCommandHandler extends BaseCommandHandler {
    private static BedCommandHandler instance;

    public static BedCommandHandler getInstance() {
        if (instance == null) {
            instance = new BedCommandHandler();
        }
        return instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        // 输出帮助
        if (args.length > 0) {
            doHelp(Main.plugin, player, "cmd.bedCmd.helpMsg");
            return;
        }

        // 打开菜单
        BedListMenu menu = new BedListMenu(I18nUtil.getText(Main.plugin, player, "bedListMenu.title"), player);
        menu.open(new BedListMenuListener(menu));
    }
}
