package net.ess3.nms;

import net.ess3.providers.Provider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ItemDbProvider implements Provider {

    private SpawnerProvider spawnerProvider;
    private SpawnEggProvider spawnEggProvider;
    private PotionMetaProvider potionMetaProvider;

    /**
     * Resolves a material name to its corresponding Material
     *
     * @param name The material name to look up
     * @return The corresponding Material for the given name
     */
    public abstract Material resolve(String name);

    /**
     * Whether the provider supports legacy ID values or not.
     *
     * @return True if the provider supports legacy IDs, otherwise false
     */
    public abstract boolean supportsLegacyIds();

    /**
     * Get the legacy ID for the material.
     *
     * @param material The material to look up
     * @return The ID corresponding to the material, or null if not supported
     */
    public abstract int getLegacyId(Material material) throws Exception;

    /**
     * Get the material for the legacy ID.
     *
     * @param id The ID to look up
     * @return The material corresponding to the ID, or -1 if not supported
     */
    public abstract Material getFromLegacyId(int id);

    /**
     * Get the primary name for the item in the given stack.
     *
     * @param item The ItemStack to check
     * @return The primary name for the item
     */
    public abstract String getPrimaryName(ItemStack item);

    /**
     * Get all names for the item in the given stack.
     *
     * @param item The ItemStack to check
     * @return The names for the item
     */
    public abstract List<String> getNames(ItemStack item);

    /**
     * Rebuild the item database, using the given lines of a file.
     *
     * @param lines The lines of the file from which the database should be built.
     */
    public abstract void rebuild(List<String> lines);

    /**
     * Creates a stack of a given item by its name.
     *
     * @param name The material name to look up
     * @return An ItemStack of size 1 of the given item
     */
    public ItemStack getStack(String name) throws Exception {
        return new ItemStack(resolve(name));
    }

    /**
     * Creates a stack with the given amount of a given item by its name.
     *
     * @param name The material name to look up
     * @param amount The amount of items in the returned ItemStack
     * @return An ItemStack with the given amount of the given item
     */
    public ItemStack getStack(String name, int amount) throws Exception {
        ItemStack is = getStack(name);
        is.setAmount(amount);
        return is;
    }

    /**
     * Read a resource from the jar.
     * Used to build the database before data from a ManagedFile is available.
     *
     * @param name The name of the resource to load.
     * @return The lines of the resource.
     */
    protected List<String> loadResource(final String name) {
        try (InputStreamReader isr = new InputStreamReader(ItemDbProvider.class.getResourceAsStream(name))) {
            BufferedReader br = new BufferedReader(isr);
            return br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean tryProvider() {
        try {
            getStack("cstone");
            getStack("diorite");
            getStack("steelbar", 5);
            getStack("aoepot");
            getStack("skeletonegg", 12);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected SpawnerProvider getSpawnerProvider() {
        return spawnerProvider;
    }

    public void setSpawnerProvider(SpawnerProvider spawnerProvider) {
        this.spawnerProvider = spawnerProvider;
    }

    protected SpawnEggProvider getSpawnEggProvider() {
        return spawnEggProvider;
    }

    public void setSpawnEggProvider(SpawnEggProvider spawnEggProvider) {
        this.spawnEggProvider = spawnEggProvider;
    }

    protected PotionMetaProvider getPotionMetaProvider() {
        return potionMetaProvider;
    }

    public void setPotionMetaProvider(PotionMetaProvider potionMetaProvider) {
        this.potionMetaProvider = potionMetaProvider;
    }

    public static class ItemData {
        final private String itemName;
        final private Material material;
        private int legacyId;
        private short itemData;
        final private String nbt;
        private PotionData potionData;

        public ItemData(String itemName, Material material, String nbt) {
            this.itemName = itemName;
            this.material = material;
            this.nbt = nbt;
        }

        public ItemData(String itemName, Material material, String nbt, PotionData potionData) {
            this.itemName = itemName;
            this.material = material;
            this.nbt = nbt;
            this.potionData = potionData;
        }

        @Deprecated
        public ItemData(String itemName, Material material, final int legacyId, final short itemData, String nbt) {
            this.itemName = itemName;
            this.material = material;
            this.legacyId = legacyId;
            this.itemData = itemData;
            this.nbt = nbt;
        }

        public String getItemName() {
            return itemName;
        }

        public Material getMaterial() {
            return material;
        }

        @Deprecated
        public int getItemNo() {
            return legacyId;
        }

        public short getItemData() {
            return itemData;
        }

        public String getNbt() {
            return nbt;
        }

        @Override
        public int hashCode() {
            return (31 * material.hashCode()) ^ itemData;
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
            return this.material == pairo.getMaterial() && this.itemData == pairo.getItemData() && this.nbt.equals(pairo.getNbt());
        }
    }

    public static class PotionData {
        private PotionType bukkitType;
        private String vanillaType;
        private boolean isStrong;
        private boolean isLong;
    }

    protected static class LengthCompare implements java.util.Comparator<String> {

        public static final LengthCompare INSTANCE = new LengthCompare();

        public LengthCompare() {
            super();
        }

        @Override
        public int compare(String s1, String s2) {
            return s1.length() - s2.length();
        }
    }
}
