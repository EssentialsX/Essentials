package net.ess3.provider.providers;

import net.ess3.provider.MaterialTagProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@ProviderData(description = "Bukkit Material Tag Provider")
public class BukkitMaterialTagProvider implements MaterialTagProvider {
    private final Map<String, Tag<Material>> stringToTagMap = new HashMap<>();

    @Override
    public boolean tagExists(String tagName) {
        if (tagName == null) {
            return false;
        }
        return getTag(tagName) != null;
    }

    @Override
    public boolean isTagged(String tagName, Material material) {
        if (tagName == null || material == null) {
            return false;
        }

        final Tag<Material> tag = getTag(tagName);
        return tag != null && tag.isTagged(material);
    }

    private Tag<Material> getTag(String tagName) {
        if (tagName == null) {
            return null;
        }

        tagName = tagName.toUpperCase();
        if (!stringToTagMap.containsKey(tagName)) {
            try {
                final Field field = Tag.class.getDeclaredField(tagName.toUpperCase());
                //noinspection unchecked
                stringToTagMap.put(tagName, (Tag<Material>) field.get(null));
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                stringToTagMap.put(tagName, null);
            }
        }
        return stringToTagMap.get(tagName);
    }

    @ProviderTest
    public static boolean test() {
        try {
            Class.forName("org.bukkit.Tag");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
