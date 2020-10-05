package com.earth2me.essentials.items;

import com.earth2me.essentials.ManagedFile;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;


public class FlatItemDb extends AbstractItemDb {
    protected static final Logger LOGGER = Logger.getLogger("Essentials");
    private static final Gson gson = new Gson();

    // Maps primary name to ItemData
    private final transient Map<String, ItemData> items = new HashMap<>();

    // Maps alias to primary name
    private final transient Map<String, String> itemAliases = new HashMap<>();

    // Every known alias
    private final transient Set<String> allAliases = new HashSet<>();

    private transient ManagedFile file = null;

    public FlatItemDb(final IEssentials ess) {
        super(ess);
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

        ready = true;
    }

    private void reset() {
        ready = false;
        items.clear();
        itemAliases.clear();
        allAliases.clear();
    }

    private void loadJSON(String source) {
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
    public ItemStack get(String id, boolean useResolvers) throws Exception {
        if (useResolvers) {
            ItemStack resolved = tryResolvers(id);
            if (resolved != null) {
                return resolved;
            }
        }

        id = id.toLowerCase();
        final String[] split = id.split(":");

        ItemData data = getByName(split[0]);

        if (data == null) {
            throw new Exception(tl("unknownItemName", id));
        }

        Material material = data.getMaterial();

        if (!material.isItem()) throw new Exception(tl("unableToSpawnItem", id));

        ItemStack stack = new ItemStack(material);
        stack.setAmount(material.getMaxStackSize());

        PotionData potionData = data.getPotionData();
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

        // The spawner provider will update the meta again, so we need to call it after
        // setItemMeta to prevent a race condition
        EntityType entity = data.getEntity();
        if (entity != null && material.toString().contains("SPAWNER")) {
            ess.getSpawnerProvider().setEntityType(stack, entity);
        }

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
        ItemData data = lookup(item);

        for (Map.Entry<String, ItemData> entry : items.entrySet()) {
            if (entry.getValue().equals(data)) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    @Deprecated
    public int getLegacyId(Material material) {
        throw new UnsupportedOperationException("Legacy IDs aren't supported on this version.");
    }

    private ItemData lookup(ItemStack item) {
        Material type = item.getType();

        if (MaterialUtil.isPotion(type) && item.getItemMeta() instanceof PotionMeta) {
            PotionData potion = ((PotionMeta) item.getItemMeta()).getBasePotionData();
            return new ItemData(type, potion);
        } else if (type.toString().contains("SPAWNER")) {
            EntityType entity = ess.getSpawnerProvider().getEntityType(item);
            return new ItemData(type, entity);
        } else {
            return new ItemData(type);
        }
    }

    @Override
    public Collection<String> listNames() {
        Set<String> names = new HashSet<>(allAliases);
        names.addAll(getResolverNames());
        return names;
    }

    public static class ItemData {
        private Material material;
        private String[] fallbacks = null;
        private PotionData potionData = null;
        private EntityType entity = null;

        ItemData(Material material) {
            this.material = material;
        }

        ItemData(Material material, PotionData potionData) {
            this.material = material;
            this.potionData = potionData;
        }

        ItemData(Material material, EntityType entity) {
            this.material = material;
            this.entity = entity;
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
            return this.getMaterial() == that.getMaterial() && potionDataEquals(that) && entityEquals(that);
        }

        public Material getMaterial() {
            if (material == null && fallbacks != null) {
                material = EnumUtil.getMaterial(fallbacks);
                fallbacks = null; // If fallback fails, don't keep trying to look up fallbacks
            }

            return material;
        }

        public PotionData getPotionData() {
            return this.potionData;
        }

        public EntityType getEntity() {
            return this.entity;
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

        private boolean entityEquals(ItemData o) {
            if (this.entity == null && o.getEntity() == null) { // neither have an entity
                return true;
            } else if (this.entity != null && o.getEntity() != null) { // both have an entity; check if it's the same one
                return this.entity.equals(o.getEntity());
            } else { // one has an entity but the other doesn't, so they can't be equal
                return false;
            }
        }
    }
}
