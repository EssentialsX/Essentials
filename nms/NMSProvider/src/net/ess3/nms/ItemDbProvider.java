package net.ess3.nms;

import net.ess3.providers.Provider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ItemDbProvider implements Provider {

    private SpawnerProvider spawnerProvider;
    private SpawnEggProvider spawnEggProvider;
    private PotionMetaProvider potionMetaProvider;

    /**
     * Populate the item database using the given lines of data.
     *
     * @param lines The lines of data from which the database should be populated
     */
    public abstract void addFrom(List<String> lines);

    /**
     * Reset the database and rebuild it from the given lines of data.
     *
     * @param lines The lines of the file from which the database should be built
     */
    public abstract void rebuild(List<String> lines);

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

    /**
     * Resolves a material name to the corresponding ItemData.
     *
     * @param name The item name to look up
     * @return The corresponding ItemData for the given name
     */
    public abstract ItemData resolve(String name);

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
     * Creates a stack of a given item by its name.
     *
     * @param name The material name to look up
     * @return An ItemStack of size 1 of the given item
     */
    public abstract ItemStack getStack(String name) throws Exception;

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
     * Get all registered primary names for materials.
     * This does not include any additional aliases.
     *
     * @return A collection of primary names
     */
    public abstract Collection<String> listNames();

    @Override
    public boolean tryProvider() {
        try {
            getStack("cobblestone");
            getStack("dstone");
            getStack("steelbar", 5);
            getStack("splbreathlevel2pot");
            getStack("skeletonhorsespawnegg", 12);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the current spawner provider.
     *
     * @return The current spawner provider
     */
    protected SpawnerProvider getSpawnerProvider() {
        return spawnerProvider;
    }

    /**
     * Set the current spawner provider.
     */
    public void setSpawnerProvider(SpawnerProvider spawnerProvider) {
        this.spawnerProvider = spawnerProvider;
    }

    /**
     * Get the current spawn egg provider.
     *
     * @return The current spawn egg provider
     */
    protected SpawnEggProvider getSpawnEggProvider() {
        return spawnEggProvider;
    }

    /**
     * Set the current spawn egg provider.
     */
    public void setSpawnEggProvider(SpawnEggProvider spawnEggProvider) {
        this.spawnEggProvider = spawnEggProvider;
    }

    /**
     * Get the current potion provider.
     *
     * @return The current potion provider
     */
    protected PotionMetaProvider getPotionMetaProvider() {
        return potionMetaProvider;
    }

    /**
     * Set the current potion provider.
     */
    public void setPotionMetaProvider(PotionMetaProvider potionMetaProvider) {
        this.potionMetaProvider = potionMetaProvider;
    }

    public abstract static class ItemData {
        protected String itemName;
        protected Material material;
        protected int legacyId;
        protected short itemData;
        protected String nbt;
        protected PotionData potionData;

        public String getItemName() {
            return itemName;
        }

        public Material getMaterial() {
            return material;
        }

        public int getItemNo() {
            return legacyId;
        }

        public short getItemData() {
            return itemData;
        }

        public String getNbt() {
            return nbt;
        }

        public PotionData getPotionData() {
            return this.potionData;
        }
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
