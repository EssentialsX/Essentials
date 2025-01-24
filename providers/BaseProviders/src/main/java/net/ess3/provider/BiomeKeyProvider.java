package net.ess3.provider;

import net.essentialsx.providers.NullableProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;

@NullableProvider
public interface BiomeKeyProvider extends Provider {
    NamespacedKey getBiomeKey(Block block);
}
