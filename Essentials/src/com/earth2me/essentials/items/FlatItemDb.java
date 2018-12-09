package com.earth2me.essentials.items;

import com.earth2me.essentials.ManagedFile;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.PotionData;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;


public class FlatItemDb extends AbstractItemDb {
    protected static final Logger LOGGER = Logger.getLogger("Essentials");
    private final transient IEssentials ess;

    private static Gson gson = new Gson();

    // Maps primary name to ItemData
    private final transient Map<String, ItemData> items = new HashMap<>();

    // Maps alias to primary name
    private final transient Map<String, String> itemAliases = new HashMap<>();

    // Every known alias
    private final transient Set<String> allAliases = new HashSet<>();

    private transient ManagedFile file = null;

    public FlatItemDb(final IEssentials ess) {
        this.ess = ess;
    }

    @Override
    public void reloadConfig() {
        if (file == null) {
            file = new ManagedFile("items.json", ess);
        }

        this.rebuild();
        LOGGER.info(String.format("Loaded %s items from items.json.", listNames().size()));
    }

    private void rebuild() {
        this.reset();

        String json = file.getLines().stream()
                .filter(line -> !line.startsWith("#"))
                .collect(Collectors.joining());
        
        this.loadJSON(String.join("\n", json));
    }

    private void reset() {
        items.clear();
        itemAliases.clear();
        allAliases.clear();
    }

    public void loadJSON(String source) {
        JsonObject map = (new JsonParser()).parse(source).getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();
            boolean valid = false;

            if (element.isJsonObject()) {
                ItemData data = gson.fromJson(element, ItemData.class);
                items.put(key, data);
                valid = true;
            } else {
                try {
                    String target = element.getAsString();
                    itemAliases.put(key, target);
                    valid = true;
                } catch (Exception ignored) {}
            }

            if (valid) {
                allAliases.add(key);
            } else {
                LOGGER.warning(String.format("Failed to add item: \"%s\": %s", key, element.toString()));
            }
        }
    }

    @Override
    public ItemStack get(final String id) throws Exception {
        final String[] split = id.split(":");

        ItemData data = getByName(split[0]);

        if (data == null) {
            throw new Exception(tl("unknownItemName", id));
        }

        PotionData potionData = data.getPotionData();
        Material material = data.getMaterial();

        ItemStack stack = new ItemStack(material);
        stack.setAmount(material.getMaxStackSize());

        ItemMeta meta = stack.getItemMeta();

        if (potionData != null && meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setBasePotionData(potionData);
        }

        // For some reason, Damageable doesn't extend ItemMeta but CB implements them in the same
        // class. As to why, your guess is as good as mine.
        if (split.length > 1 && meta instanceof Damageable) {
            Damageable damageMeta = (Damageable) meta;
            damageMeta.setDamage(Integer.parseInt(split[1]));
        }

        stack.setItemMeta(meta);

        return stack;
    }

    private ItemData getByName(String name) {
        name = name.toLowerCase();
        if (items.containsKey(name)) {
            return items.get(name);
        } else if (itemAliases.containsKey(name)) {
            return items.get(itemAliases.get(name));
        }

        return null;
    }

    @Override
    public List<String> nameList(ItemStack item) {
        List<String> names = new ArrayList<>();
        String primaryName = name(item);
        names.add(primaryName);

        for (Map.Entry<String, String> entry : itemAliases.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(primaryName)) {
                names.add(entry.getKey());
            }
        }

        return names;
    }

    @Override
    public String name(ItemStack item) {
        Material type = item.getType();
        PotionData potion = null;

        if ((type.name().contains("POTION") || type.name().equals("TIPPED_ARROW")) && item.getItemMeta() instanceof PotionMeta) {
            potion = ((PotionMeta) item.getItemMeta()).getBasePotionData();
        }

        ItemData data = new ItemData(type, potion);

        for (Map.Entry<String, ItemData> entry : items.entrySet()) {
            if (entry.getValue().equals(data)) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    @Deprecated
    public Material getFromLegacyId(int id) {
        throw new UnsupportedOperationException("Legacy IDs aren't supported on this version of EssentialsX.");
    }

    @Override
    @Deprecated
    public int getLegacyId(Material material) throws Exception {
        throw new UnsupportedOperationException("Legacy IDs aren't supported on this version of EssentialsX.");
    }

    @Override
    public Collection<String> listNames() {
        return Collections.unmodifiableSet(allAliases);
    }

    public static class ItemData {
        private String itemName;
        private Material material;
        private PotionData potionData;

        public ItemData(Material material, PotionData potionData) {
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
            ItemData that = (ItemData) o;
            return this.material == that.getMaterial() && potionDataEquals(that);
        }

        public String getItemName() {
            return itemName;
        }

        public Material getMaterial() {
            return material;
        }

        public PotionData getPotionData() {
            return this.potionData;
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
