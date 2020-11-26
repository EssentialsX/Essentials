package net.ess3.provider.providers;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import net.ess3.provider.MaterialTagProvider;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.lang.reflect.Field;

public class PaperMaterialTagProvider implements MaterialTagProvider {
    @Override
    public boolean tagExists(String tagName) {
        if (tagName == null) {
            return false;
        }
        try {
            MaterialTags.class.getDeclaredField(tagName.toUpperCase());
            return true;
        } catch (NoSuchFieldException e) {
            try {
                Tag.class.getDeclaredField(tagName.toUpperCase());
                return true;
            } catch (NoSuchFieldException ex) {
                return false;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isTagged(String tagName, Material material) {
        if (tagName == null) {
            return false;
        }
        try {
            final Field tagField = Tag.class.getDeclaredField(tagName.toUpperCase());
            final MaterialSetTag tagSet = (MaterialSetTag) tagField.get(null);
            return tagSet.isTagged(material);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            try {
                final Field tagField = MaterialTags.class.getDeclaredField(tagName.toUpperCase());
                final Tag<Material> tagSet = (Tag<Material>) tagField.get(null);
                return tagSet.isTagged(material);
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException ex) {
                return false;
            }
        }
    }
}
