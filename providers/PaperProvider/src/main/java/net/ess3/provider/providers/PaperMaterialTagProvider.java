package net.ess3.provider.providers;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import net.ess3.provider.MaterialTagProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@ProviderData(description = "Paper Material Tag Provider", weight = 1)
public class PaperMaterialTagProvider implements MaterialTagProvider {
    private final Map<String, Tag<Material>> bukkitTagMap = new HashMap<>();
    private final Map<String, MaterialSetTag> paperTagMap = new HashMap<>();

    @Override
    public boolean tagExists(String tagName) {
        if (tagName == null) {
            return false;
        }
        return getBukkitTag(tagName) != null || getPaperTag(tagName) != null;
    }

    @Override
    public boolean isTagged(String tagName, Material material) {
        if (tagName == null) {
            return false;
        }

        if (getBukkitTag(tagName) != null) {
            return getBukkitTag(tagName).isTagged(material);
        }

        if (getPaperTag(tagName) != null) {
            return getPaperTag(tagName).isTagged(material);
        }

        return false;
    }

    private MaterialSetTag getPaperTag(String tagName) {
        if (tagName == null) {
            return null;
        }

        tagName = tagName.toUpperCase();
        if (!paperTagMap.containsKey(tagName)) {
            try {
                final Field field = MaterialTags.class.getDeclaredField(tagName.toUpperCase());
                paperTagMap.put(tagName, (MaterialSetTag) field.get(null));
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                paperTagMap.put(tagName, null);
            }
        }
        return paperTagMap.get(tagName);
    }

    private Tag<Material> getBukkitTag(String tagName) {
        if (tagName == null) {
            return null;
        }

        tagName = tagName.toUpperCase();
        if (!bukkitTagMap.containsKey(tagName)) {
            try {
                final Field field = Tag.class.getDeclaredField(tagName.toUpperCase());
                //noinspection unchecked
                bukkitTagMap.put(tagName, (Tag<Material>) field.get(null));
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                bukkitTagMap.put(tagName, null);
            }
        }
        return bukkitTagMap.get(tagName);
    }

    @ProviderTest
    public static boolean test() {
        try {
            Class.forName("org.bukkit.Tag");
            Class.forName("com.destroystokyo.paper.MaterialTags");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
