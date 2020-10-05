package net.ess3.provider.providers;

import net.ess3.provider.ContainerProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class PaperContainerProvider implements ContainerProvider {

    @Override
    public InventoryView openAnvil(Player player) {
        return player.openAnvil(null, true);
    }

    @Override
    public InventoryView openCartographyTable(Player player) {
        return player.openCartographyTable(null, true);
    }

    @Override
    public InventoryView openGrindstone(Player player) {
        return player.openGrindstone(null, true);
    }

    @Override
    public InventoryView openLoom(Player player) {
        return player.openLoom(null, true);
    }

    @Override
    public InventoryView openSmithingTable(Player player) {
        return player.openSmithingTable(null, true);
    }

    @Override
    public InventoryView openStonecutter(Player player) {
        return player.openStonecutter(null, true);
    }

    @Override
    public String getDescription() {
        return "Paper Container Opening Provider";
    }

}
