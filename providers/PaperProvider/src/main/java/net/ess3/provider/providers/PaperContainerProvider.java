package net.ess3.provider.providers;

import net.ess3.provider.ContainerProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

@ProviderData(description = "Paper Container Provider")
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

    @ProviderTest
    public static boolean test() {
        try {
            HumanEntity.class.getDeclaredMethod("openCartographyTable", Location.class, boolean.class);
            return true;
        } catch (final NoSuchMethodException ignored) {
            return false;
        }
    }
}
