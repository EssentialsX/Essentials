package net.ess3.nms.flattened;

import com.google.gson.*;
import net.ess3.nms.ItemDbProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.stream.Collectors;

public class FlatItemDbProvider extends ItemDbProvider {
    private static Gson gson = new Gson();

    private final transient Map<String, ItemData> items = new HashMap<>();
    private final transient Map<ItemData, Set<String>> aliases = new HashMap<>();

    @Override
    public Material resolve(String name) {
        return null;
    }

    @Override
    public boolean supportsLegacyIds() {
        return false;
    }

    @Override
    public int getLegacyId(Material material) {
        return -1;
    }

    @Override
    public Material getFromLegacyId(int id) {
        return null;
    }

    @Override
    public String getPrimaryName(ItemStack item) {
        return null;
    }

    @Override
    public List<String> getNames(ItemStack item) {
        return null;
    }

    @Override
    public void rebuild(List<String> lines) {
        String json = String.join("\n", lines);
        JsonObject map = (new JsonParser()).parse(json).getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();

            if (element.isJsonObject()) {
                ItemData data = gson.fromJson(element, ItemData.class);
                items.put(key, data);
                addAlias(data, key);
            } else {
                try {
                    // TODO: finalise this - how do we handle aliases loading before actual materials?
                    // Temporary Map<String, List<String>> that we copy over from once json parsed?
                    String target = element.getAsString();
                    ItemData data = items.get(target);
                    addAlias(data, target);
                } catch (Exception e) {
                    // TODO: log invalid entry
                }
            }
        }
    }

    private void addAlias(ItemData data, String alias) {
        Set<String> aliasList;

        if (aliases.containsKey(data)) {
            aliasList = aliases.get(data);
        } else {
            aliasList = new HashSet<>();
            aliases.put(data, aliasList);
        }

        aliasList.add(alias);
    }

    @Override
    public boolean tryProvider() {
        // Build the database initially so that we can actually test the provider
        this.rebuild(this.loadResource("/items.json"));
        return super.tryProvider();
    }

    @Override
    public String getHumanName() {
        return "Post-1.13 item database provider";
    }

    private class MaterialData {
        private Material material;
        private PotionType potionEnum;
        private String potionModifier;
    }
}
