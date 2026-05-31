package net.ess3.provider.providers;

import net.ess3.provider.PatternTypeProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;

/**
 * Modern PatternType provider for Minecraft 1.20.5+.
 *
 * In 1.20.5 PatternType was changed from an enum to a Keyed interface backed
 * by Registry.BANNER_PATTERN. This provider uses the Registry API to look up
 * pattern types by their identifier.
 */
@ProviderData(description = "Modern (Registry-based) PatternType Provider", weight = 1)
public class ModernPatternTypeProvider implements PatternTypeProvider {

    @Override
    public PatternType getPatternTypeByIdentifier(final String identifier) {
        if (identifier == null) {
            return null;
        }
        return Registry.BANNER_PATTERN.get(NamespacedKey.minecraft(identifier.toLowerCase()));
    }

    @ProviderTest
    public static boolean test() {
        try {
            // Registry.BANNER_PATTERN was added in 1.20.5 when PatternType
            // was converted from an enum to a Registry-backed Keyed type.
            Registry.class.getField("BANNER_PATTERN");
            return true;
        } catch (final Throwable ignored) {
            return false;
        }
    }
}
