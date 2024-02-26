package net.ess3.provider;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;

public interface BiomeKeyProvider extends Provider {
    NamespacedKey getBiomeKey(Block block);
}
