package net.ess3.nms.flattened;

import com.google.gson.*;
import net.ess3.nms.ItemDbProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.stream.Collectors;

public class FlatItemDbProvider extends ItemDbProvider {
    private static Gson gson = new Gson();

    private final transient Map<String, ItemData> primaryNames = new HashMap<>();
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
        ItemData itemData = new ItemData(null, item.getType(), null);

        for (Map.Entry<String, ItemData> entry : primaryNames.entrySet()) {
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
                ItemData data = gson.fromJson(element, ItemData.class);
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
}
