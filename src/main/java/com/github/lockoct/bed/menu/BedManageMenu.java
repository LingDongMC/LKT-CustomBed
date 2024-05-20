package com.github.lockoct.bed.menu;

import com.github.lockoct.Main;
import com.github.lockoct.bed.listener.BedListMenuListener;
import com.github.lockoct.entity.BedArea;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Dao;

import java.util.HashMap;

public class BedManageMenu extends BaseMenu {
    private boolean enabled;
    private final int STATUS_BTN_POS = 33;

    public BedManageMenu(String title, HashMap<String, Object> menuContext, Player player) {
        super(54, title, menuContext, player, Main.plugin);

        // 取出上下文信息
        BedArea bedInfo = (BedArea) menuContext.get("bedInfo");

        // 设置告示信息
        int INFO_POS = 13;
        ItemStack is = setOptItem(Material.OAK_SIGN, bedInfo.getName(), INFO_POS, null);
        getInventory().setItem(INFO_POS, is);

        // 删除按钮
        setOptItem(Material.BARRIER, I18nUtil.getText(Main.plugin, player, "bedManageMenu.btn.delete"), 29, "delete");

        // 启用/禁用按钮
        enabled = bedInfo.isEnabled();
        String enableStr = I18nUtil.getText(Main.plugin, player, enabled ? "bedManageMenu.btn.enable" : "bedManageMenu.btn.disable");
        Material enableItemMaterial = enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
        setOptItem(enableItemMaterial, enableStr, STATUS_BTN_POS, "enable");

        // 返回、退出按钮
        int BACK_BTN_POS = 48;
        int EXIT_BTN_POS = 50;
        setOptItem(Material.ARROW, I18nUtil.getCommonText(player, "menu.back"), BACK_BTN_POS, "back");
        setOptItem(Material.DARK_OAK_DOOR, I18nUtil.getCommonText(player, "menu.exit"), EXIT_BTN_POS, "exit");

        // 背景
        setBackGround(Material.BLUE_STAINED_GLASS_PANE);
    }

    public void enable() {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    BedArea area = (BedArea) getMenuContext().get("bedInfo");
                    enabled = !enabled;
                    area.setEnabled(enabled);
                    int res = dao.update(area);
                    if (res > 0) {
                        String enableStr = I18nUtil.getText(Main.plugin, getPlayer(), enabled ? "bedManageMenu.btn.enable" : "bedManageMenu.btn.disable");
                        Material enableItemMaterial = enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
                        ItemStack is = getInventory().getItem(STATUS_BTN_POS);
                        assert is != null;
                        is.setType(enableItemMaterial);
                        ItemMeta im = is.getItemMeta();
                        assert im != null;
                        im.setDisplayName(enableStr);
                        is.setItemMeta(im);
                    }
                }
            }.runTaskAsynchronously(Main.plugin);
        }
    }

    public void delete() {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    BedArea area = (BedArea) getMenuContext().get("bedInfo");
                    area.setDeleted(true);
                    int res = dao.update(area);
                    if (res > 0) {
                        getPlayer().sendMessage(ChatColor.GREEN + I18nUtil.getText(Main.plugin, getPlayer(), "bedManageMenu.deleteSuccessful", area.getName()));
                    }
                }
            }.runTaskAsynchronously(Main.plugin);
            close();
        }
    }

    public void back() {
        BedListMenu menu = new BedListMenu((int) getMenuContext().get("fromPage"), I18nUtil.getText(Main.plugin, getPlayer(), "bedListMenu.title"), getPlayer());
        close();
        menu.open(new BedListMenuListener(menu));
    }
}
