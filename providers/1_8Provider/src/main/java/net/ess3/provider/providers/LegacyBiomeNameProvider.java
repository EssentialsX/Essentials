package net.ess3.provider.providers;

import net.ess3.provider.BiomeNameProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.block.Block;

import java.util.Locale;

@ProviderData(description = "Legacy Biome Name Provider")
public class LegacyBiomeNameProvider implements BiomeNameProvider {
    @Override
    public String getBiomeName(final Block block) {
        // For some reason, compiling against modern versions causes this call to break, possibly related to OldEnum?
        // Compiling this against versions that still have proper enums allow this to work
        return block.getBiome().name().toLowerCase(Locale.ENGLISH);
    }
}
