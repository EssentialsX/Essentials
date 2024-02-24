package net.ess3.provider.providers;

import net.ess3.provider.BiomeKeyProvider;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;

@SuppressWarnings("deprecation")
public class PaperBiomeKeyProvider implements BiomeKeyProvider {
    @Override
    public NamespacedKey getBiomeKey(final Block block) {
        return Bukkit.getUnsafe().getBiomeKey(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    @Override
    public String getDescription() {
        return "Paper Biome Key Provider";
    }
}
