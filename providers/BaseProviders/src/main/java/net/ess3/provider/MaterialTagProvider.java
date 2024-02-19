package net.ess3.provider;

import org.bukkit.Material;

public interface MaterialTagProvider extends Provider {
    boolean tagExists(String tagName);

    boolean isTagged(String tagName, Material material);
}
