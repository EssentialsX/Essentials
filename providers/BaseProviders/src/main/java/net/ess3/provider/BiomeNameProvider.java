package net.ess3.provider;

import org.bukkit.block.Block;

public interface BiomeNameProvider extends Provider {
    String getBiomeName(Block block);
}
