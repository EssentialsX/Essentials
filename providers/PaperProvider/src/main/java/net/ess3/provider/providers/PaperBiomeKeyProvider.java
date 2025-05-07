package net.ess3.provider.providers;

import net.ess3.provider.BiomeKeyProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.RegionAccessor;
import org.bukkit.UnsafeValues;
import org.bukkit.block.Block;

@ProviderData(description = "Paper Biome Key Provider")
public class PaperBiomeKeyProvider implements BiomeKeyProvider {
    @Override
    public NamespacedKey getBiomeKey(final Block block) {
        //noinspection deprecation
        return Bukkit.getUnsafe().getBiomeKey(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    @ProviderTest
    public static boolean test() {
        try {
            //noinspection deprecation
            UnsafeValues.class.getDeclaredMethod("getBiomeKey", RegionAccessor.class, int.class, int.class, int.class);
            return true;
        } catch (final Throwable ignored) {
            return false;
        }
    }
}
