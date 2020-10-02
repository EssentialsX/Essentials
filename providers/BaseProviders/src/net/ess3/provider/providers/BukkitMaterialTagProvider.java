package net.ess3.provider.providers;

import net.ess3.provider.MaterialTagProvider;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.lang.reflect.Field;

public class BukkitMaterialTagProvider implements MaterialTagProvider {
    @Override
    public boolean tagExists(String tagName) {
        if (tagName == null) {
            return false;
        }
        try {
            Tag.class.getDeclaredField(tagName.toUpperCase());
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    @Override
    public boolean isTagged(String tagName, Material material) {
        if (tagName == null) {
            return false;
        }
        try {
            Field tagField = Tag.class.getDeclaredField(tagName.toUpperCase());
            @SuppressWarnings("unchecked")
            Tag<Material> tagSet = (Tag<Material>) tagField.get(null);
            return tagSet.isTagged(material);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            return false;
        }
    }
}
