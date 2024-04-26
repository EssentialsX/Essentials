package com.earth2me.essentials.items;

import com.earth2me.essentials.ManagedFile;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ess3.api.IEssentials;
import net.ess3.api.TranslatableException;
import net.ess3.provider.PotionMetaProvider;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FlatItemDb extends AbstractItemDb {
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
        ess.getLogger().info(String.format("Loaded %s items from items.json.", listNames().size()));
    }

    private void rebuild() {
        this.reset();

        final String json = file.getLines().stream()
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

    private void loadJSON(final String source) {
        final JsonObject map = new JsonParser().parse(source).getAsJsonObject();

        for (final Map.Entry<String, JsonElement> entry : map.entrySet()) {
            final String key = entry.getKey();
            final JsonElement element = entry.getValue();
            boolean valid = false;

            if (element.isJsonObject()) {
                final ItemData data = gson.fromJson(element, ItemData.class);
                items.put(key, data);
                valid = true;
            } else {
                try {
                    final String target = element.getAsString();
                    itemAliases.put(key, target);
                    valid = true;
                } catch (final Exception ignored) {
                }
            }

            if (valid) {
                allAliases.add(key);
            } else {
                ess.getLogger().warning(String.format("Failed to add item: \"%s\": %s", key, element.toString()));
            }
        }
    }

    @Override
    public ItemStack get(String id, final boolean useResolvers) throws Exception {
        if (useResolvers) {
            final ItemStack resolved = tryResolverDeserialize(id);
            if (resolved != null) {
                return resolved;
            }
        }

        id = id.toLowerCase();
        final String[] split = id.split("(?<!^minecraft):");

        final ItemData data = getByName(split[0]);

        if (data == null) {
            throw new TranslatableException("unknownItemName", id);
        }

        final Material material = data.getMaterial();

        if (!material.isItem()) throw new TranslatableException("unableToSpawnItem", id);

        final ItemStack stack = new ItemStack(material);
        stack.setAmount(material.getMaxStackSize());

        final PotionMetaProvider.AbstractPotionData potionData = data.getPotionData();
        final ItemMeta meta = stack.getItemMeta();

        if (potionData != null && meta instanceof PotionMeta) {
            final PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setBasePotionType(potionData);
        }

        // For some reason, Damageable doesn't extend ItemMeta but CB implements them in the same
        // class. As to why, your guess is as good as mine.
        if (split.length > 1 && meta instanceof Damageable) {
            final Damageable damageMeta = (Damageable) meta;
            damageMeta.setDamage(Integer.parseInt(split[1]));
        }

        stack.setItemMeta(meta);

        // The spawner provider will update the meta again, so we need to call it after
        // setItemMeta to prevent a race condition
        final EntityType entity = data.getEntity();
        if (entity != null && material.toString().contains("SPAWNER")) {
            ess.getSpawnerItemProvider().setEntityType(stack, entity);
            ess.getPersistentDataProvider().set(stack, "convert", "true");
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
    public List<String> nameList(final ItemStack item) {
        final List<String> names = new ArrayList<>();
        final String primaryName = name(item);
        names.add(primaryName);

        for (final Map.Entry<String, String> entry : itemAliases.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(primaryName)) {
                names.add(entry.getKey());
            }
        }

        return names;
    }

    @Override
    public String name(final ItemStack item) {
        final ItemData data = lookup(item);

        for (final Map.Entry<String, ItemData> entry : items.entrySet()) {
            if (entry.getValue().equals(data)) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    @Deprecated
    public int getLegacyId(final Material material) {
        throw new UnsupportedOperationException("Legacy IDs aren't supported on this version.");
    }

    private ItemData lookup(final ItemStack item) {
        final Material type = item.getType();

        if (MaterialUtil.isPotion(type) && item.getItemMeta() instanceof PotionMeta) {
            final PotionMetaProvider.AbstractPotionData potion = ess.getPotionMetaProvider().getPotionData(item);
            return new ItemData(type, potion);
        } else if (type.toString().contains("SPAWNER")) {
            final EntityType entity = ess.getSpawnerItemProvider().getEntityType(item);
            return new ItemData(type, entity);
        } else {
            return new ItemData(type);
        }
    }

    @Override
    public Collection<String> listNames() {
        final Set<String> names = new HashSet<>(allAliases);
        names.addAll(getResolverNames());
        return names;
    }

    public static class ItemData {
        private Material material;
        private String[] fallbacks = null;
        private PotionMetaProvider.AbstractPotionData potionData = null;
        private EntityType entity = null;

        ItemData(final Material material) {
            this.material = material;
        }

        ItemData(final Material material, final PotionMetaProvider.AbstractPotionData potionData) {
            this.material = material;
            this.potionData = potionData;
        }

        ItemData(final Material material, final EntityType entity) {
            this.material = material;
            this.entity = entity;
        }

        @Override
        public int hashCode() {
            return (31 * material.hashCode()) ^ potionData.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ItemData)) {
                return false;
            }
            final ItemData that = (ItemData) o;
            return this.getMaterial() == that.getMaterial() && potionDataEquals(that) && entityEquals(that);
        }

        public Material getMaterial() {
            if (material == null && fallbacks != null) {
                material = EnumUtil.getMaterial(fallbacks);
                fallbacks = null; // If fallback fails, don't keep trying to look up fallbacks
            }

            return material;
        }

        public PotionMetaProvider.AbstractPotionData getPotionData() {
            return this.potionData;
        }

        public EntityType getEntity() {
            return this.entity;
        }

        private boolean potionDataEquals(final ItemData o) {
            if (this.potionData == null && o.getPotionData() == null) {
                return true;
            } else if (this.potionData != null && o.getPotionData() != null) {
                return this.potionData.equals(o.getPotionData());
            } else {
                return false;
            }
        }

        private boolean entityEquals(final ItemData o) {
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
