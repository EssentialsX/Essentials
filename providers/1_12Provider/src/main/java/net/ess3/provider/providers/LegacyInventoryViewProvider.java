package net.ess3.provider.providers;

import net.ess3.provider.InventoryViewProvider;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class LegacyInventoryViewProvider implements InventoryViewProvider {
    @Override
    public Inventory getTopInventory(InventoryView view) {
        return view.getTopInventory();
    }

    @Override
    public Inventory getBottomInventory(InventoryView view) {
        return view.getBottomInventory();
    }

    @Override
    public void setItem(InventoryView view, int slot, ItemStack item) {
        view.setItem(slot, item);
    }

    @Override
    public void close(InventoryView view) {
        view.close();
    }

    @Override
    public String getDescription() {
        return "Legacy InventoryView Abstract Class ABI Provider";
    }
}
