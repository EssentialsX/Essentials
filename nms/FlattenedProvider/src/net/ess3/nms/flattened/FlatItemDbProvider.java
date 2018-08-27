package net.ess3.nms.flattened;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ess3.nms.ItemDbProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.util.*;
import java.util.stream.Collectors;

public class FlatItemDbProvider extends ItemDbProvider {
    private static Gson gson = new Gson();

    private final transient Map<String, FlatItemData> primaryNames = new HashMap<>();
    private final transient Map<String, List<String>> names = new HashMap<>();

    @Override
    public Material resolve(String name) {
        return Objects.requireNonNull(getByName(name)).getMaterial();
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
        ItemData itemData = new FlatItemData(null, item.getType(), null);

        for (Map.Entry<String, FlatItemData> entry : primaryNames.entrySet()) {
            if (entry.getValue().equals(itemData)) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    public List<String> getNames(ItemStack item) {
        String primaryName = getPrimaryName(item);

        for (Map.Entry<String, List<String>> entry : names.entrySet()) {
            if (entry.getKey().equals(primaryName)) {
                return Collections.unmodifiableList(entry.getValue());
            }
        }
        return null;
    }

    @Override
    public ItemStack getStack(String name) throws Exception {
        ItemData data = Objects.requireNonNull(getByName(name));
        PotionData potionData = data.getPotionData();
        Material material = data.getMaterial();

        ItemStack stack = new ItemStack(material);

        if (potionData != null) {
            PotionMeta meta = (PotionMeta) stack.getItemMeta();
            meta.setBasePotionData(potionData);
            stack.setItemMeta(meta);
        }

        return stack;
    }

    @Override
    public Collection<String> listNames() {
        return Collections.unmodifiableSet(primaryNames.keySet());
    }

    private ItemData getByName(String name) {
        if (primaryNames.containsKey(name.toLowerCase())) {
            return primaryNames.get(name);
        } else {
            for (Map.Entry<String, List<String>> entry : names.entrySet()) {
                if (entry.getValue().contains(name.toLowerCase())) {
                    return primaryNames.get(entry.getKey());
                }
            }
        }

        return null;
    }

    private void resetDb() {
        primaryNames.clear();
        names.clear();
    }

    @Override
    public void addFrom(List<String> lines) {
        String json = lines.stream()
                .filter(line -> !line.startsWith("#"))
                .collect(Collectors.joining("\n"));

        JsonObject map = (new JsonParser()).parse(json).getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();

            if (element.isJsonObject()) {
                FlatItemData data = gson.fromJson(element, FlatItemData.class);
                primaryNames.put(key, data);
            } else {
                try {
                    String target = element.getAsString();
                    addAlias(target, key);
                } catch (Exception e) {
                    // TODO: log invalid entry
                }
            }
        }
    }

    @Override
    public void rebuild(List<String> lines) {
        resetDb();
        addFrom(lines);
    }

    private void addAlias(String primaryName, String alias) {
        List<String> aliasList;

        if (names.containsKey(primaryName)) {
            aliasList = names.get(primaryName);
        } else {
            aliasList = new ArrayList<>();
            names.put(primaryName, aliasList);
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

    public static class FlatItemData extends ItemData {
        private FlatItemData(String itemName, Material material, PotionData potionData) {
            this.itemName = itemName;
            this.material = material;
            this.potionData = potionData;
        }

        @Override
        public int hashCode() {
            return (31 * material.hashCode()) ^ potionData.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ItemData)) {
                return false;
            }
            ItemData pairo = (ItemData) o;
            return this.material == pairo.getMaterial() && potionDataEquals(pairo);
        }

        private boolean potionDataEquals(ItemData o) {
            if (this.potionData == null && o.getPotionData() == null) {
                return true;
            } else if (this.potionData != null && o.getPotionData() != null) {
                return this.potionData.equals(o.getPotionData());
            } else {
                return false;
            }
        }
    }
}
