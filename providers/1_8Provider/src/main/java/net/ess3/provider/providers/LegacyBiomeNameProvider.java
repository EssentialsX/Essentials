package net.ess3.provider.providers;

import net.ess3.provider.BiomeNameProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.block.Block;

import java.util.Locale;

@ProviderData(description = "Legacy Item Unbreakable Provider")
public class LegacyBiomeNameProvider implements BiomeNameProvider {
    @Override
    public String getBiomeName(final Block block) {
        return block.getBiome().name().toLowerCase(Locale.ENGLISH);
    }
}
