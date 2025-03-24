package net.ess3.provider;

import net.essentialsx.providers.NullableProvider;
import org.bukkit.inventory.ItemStack;

@NullableProvider
public interface SerializationProvider extends Provider {
    byte[] serializeItem(ItemStack stack);

    ItemStack deserializeItem(byte[] bytes);
}
