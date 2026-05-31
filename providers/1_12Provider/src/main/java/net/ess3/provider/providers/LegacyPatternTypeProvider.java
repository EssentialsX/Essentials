package net.ess3.provider.providers;

import net.ess3.provider.PatternTypeProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.block.banner.PatternType;

/**
 * Legacy PatternType provider for Minecraft 1.19.4 and older.
 *
 * In these versions PatternType is an enum and getByIdentifier() is a static
 * method on the enum itself. Calling it via the modern API path causes an
 * IncompatibleClassChangeError because the compiled bytecode expects a
 * Methodref constant but finds an InterfaceMethodref (or vice-versa).
 *
 * This provider avoids that by iterating over PatternType.values() and
 * comparing identifiers manually, which works on all versions where
 * PatternType is still an enum.
 */
@ProviderData(description = "Legacy (enum-based) PatternType Provider", weight = 0)
public class LegacyPatternTypeProvider implements PatternTypeProvider {

    @Override
    public PatternType getPatternTypeByIdentifier(final String identifier) {
        if (identifier == null) {
            return null;
        }
        for (final PatternType type : PatternType.values()) {
            //noinspection removal
            if (type.getIdentifier().equalsIgnoreCase(identifier)) {
                return type;
            }
        }
        return null;
    }

    @ProviderTest
    public static boolean test() {
        try {
            // PatternType is an enum in 1.19.4 and older.
            // In 1.20.5+ it became a Keyed interface backed by Registry,
            // so values() no longer exists and this will throw.
            PatternType.class.getMethod("values");
            return true;
        } catch (final Throwable ignored) {
            return false;
        }
    }
}
