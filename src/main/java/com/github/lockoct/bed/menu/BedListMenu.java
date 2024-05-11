package com.github.lockoct.bed.menu;

import com.github.lockoct.Main;
import com.github.lockoct.bed.listener.BedManageMenuListener;
import com.github.lockoct.entity.BedArea;
import com.github.lockoct.menu.PageableMenu;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BedListMenu extends PageableMenu {
    private List<BedArea> areas;

    public BedListMenu(String title, Player player) {
        super(title, new HashMap<>(), player, Main.plugin);
    }

    public BedListMenu(int currentPage, String title, Player player) {
        super(currentPage, title, new HashMap<>(), player, Main.plugin);
    }

    @Override
    protected void setPageContent(int page) {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Pager pager = dao.createPager(page, PAGE_SIZE);
                    Cnd cond = Cnd.where("deleted", "=", 0).and("create_user", "=", getPlayer().getUniqueId().toString());
                    areas = dao.query(BedArea.class, cond.orderBy("create_time", "desc"), pager);
                    pager.setRecordCount(dao.count(BedArea.class, cond));
                    setTotalPage(pager.getPageCount());
                    setTotal(pager.getRecordCount());
                    // 设置分页
                    setPageElement();
                    // 填充物品
                    for (int i = 0; i < PAGE_SIZE; i++) {
                        Inventory inv = getInventory();
                        if (i < areas.size()) {
                            ItemStack is = new ItemStack(Material.GRASS_BLOCK);
                            ItemMeta im = is.getItemMeta();
                            assert im != null;
                            im.setDisplayName(areas.get(i).getName());
                            is.setItemMeta(im);
                            inv.setItem(i, is);
                        } else {
                            // 填充空位
                            inv.setItem(i, null);
                        }
                    }
                }
            }.runTaskAsynchronously(Main.plugin);
        }
    }

    // 翻页按钮、分页信息
    @Override
    protected void setPageElement() {
        super.setPageElement();

        Inventory inv = getInventory();

        // 获取分页信息元素
        ItemStack is = inv.getItem(49);
        assert is != null;
        ItemMeta im = is.getItemMeta();
        assert im != null;
        // 分页附加信息
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(I18nUtil.getText(Main.plugin, getPlayer(), "bedListMenu.pageStatisticsInfo", getTotal()));
        im.setLore(loreList);
        is.setItemMeta(im);
        inv.setItem(49, is);
    }

    public void toManageMenu(int index) {
        if (index < PAGE_SIZE) {
            HashMap<String, Object> context = getMenuContext();
            // 床信息
            context.put("bedInfo", areas.get(index));
            // 列表菜单当前页码
            context.put("fromPage", getCurrentPage());

            BedManageMenu menu = new BedManageMenu(I18nUtil.getText(Main.plugin, getPlayer(), "bedManageMenu.title"), getMenuContext(), getPlayer());
            close();
            menu.open(new BedManageMenuListener(menu));
        }
    }
}
