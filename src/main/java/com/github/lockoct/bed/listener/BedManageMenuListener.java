package com.github.lockoct.bed.listener;

import com.github.lockoct.bed.menu.BedManageMenu;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.listener.BaseMenuListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BedManageMenuListener extends BaseMenuListener {
    public BedManageMenuListener(BaseMenu menu) {
        super(menu);
    }

    @Override
    @EventHandler
    public boolean onClick(InventoryClickEvent e) {
        if (super.onClick(e)) {
            ItemStack is = e.getCurrentItem();
            BedManageMenu menu = (BedManageMenu) getMenu();
            if (is != null) {
                String sign = menu.getOperationItemPos().get(e.getRawSlot());
                sign = sign == null ? "" : sign;
                switch (sign) {
                    case "exit" -> menu.close();
                    case "back" -> menu.back();
                    case "delete" -> menu.delete();
                    case "enable" -> menu.enable();
                }
            }
            return true;
        }
        return false;
    }
}
