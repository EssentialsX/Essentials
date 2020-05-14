package net.ess3.providers;

import org.apache.commons.lang.WordUtils;

public enum ProviderType {
    POTION_META,
    SPAWN_EGG,
    MOB_SPAWNER;

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name().replace('_', ' '));
    }
}
