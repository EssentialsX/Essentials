package net.ess3.provider;

import net.essentialsx.providers.NullableProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

@NullableProvider
public interface ContainerProvider extends Provider {

    InventoryView openAnvil(Player player);

    InventoryView openCartographyTable(Player player);

    InventoryView openGrindstone(Player player);

    InventoryView openLoom(Player player);

    InventoryView openSmithingTable(Player player);

    InventoryView openStonecutter(Player player);

}
