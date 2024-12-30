package net.ess3.provider;

import net.essentialsx.providers.NullableProvider;
import org.bukkit.Material;

@NullableProvider
public interface MaterialTagProvider extends Provider {
    boolean tagExists(String tagName);

    boolean isTagged(String tagName, Material material);
}
