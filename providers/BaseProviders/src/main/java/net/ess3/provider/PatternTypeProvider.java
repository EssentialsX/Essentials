package net.ess3.provider;

import org.bukkit.block.banner.PatternType;

public interface PatternTypeProvider extends Provider {
    /**
     * Returns the PatternType matching the given identifier string, or null if not found.
     *
     * @param identifier the pattern identifier (e.g. "stripe_top")
     * @return the matching PatternType, or null
     */
    PatternType getPatternTypeByIdentifier(String identifier);
}
