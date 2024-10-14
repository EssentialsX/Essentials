package com.earth2me.essentials.items;

import com.earth2me.essentials.ManagedFile;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.TranslatableException;
import net.ess3.provider.PersistentDataProvider;
import net.ess3.provider.PotionMetaProvider;
import net.ess3.provider.SpawnEggProvider;
import net.ess3.provider.SpawnerItemProvider;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyItemDb extends AbstractItemDb {
    private final transient Map<String, Integer> items = new HashMap<>();
    private final transient Map<ItemData, List<String>> names = new HashMap<>();
    private final transient Map<ItemData, String> primaryName = new HashMap<>();
    private final transient Map<Integer, ItemData> legacyIds = new HashMap<>();
    private final transient Map<String, Short> durabilities = new HashMap<>();
    private final transient Map<String, String> nbtData = new HashMap<>();
    private final transient ManagedFile file;
    private final transient Pattern splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");
    private final transient Pattern csvSplitPattern = Pattern.compile("(\"([^\"]*)\"|[^,]*)(,|$)");

    public LegacyItemDb(final IEssentials ess) {
        super(ess);
        file = new ManagedFile("items.csv", ess);
    }

    @Override
    public void reloadConfig() {
        final List<String> lines = file.getLines();

        if (lines.isEmpty()) {
            return;
        }

        ready = false;
        durabilities.clear();
        items.clear();
        names.clear();
        primaryName.clear();

        for (final String line : lines) {
            if (line.length() > 0 && line.charAt(0) == '#') {
                continue;
            }

            String itemName = null;
            int numeric = -1;
            short data = 0;
            String nbt = null;

            int col = 0;
            final Matcher matcher = csvSplitPattern.matcher(line);
            while (matcher.find()) {
                String match = matcher.group(1);
                if (StringUtil.stripToNull(match) == null) {
                    continue;
                }
                match = StringUtil.strip(match.trim(), "\"");
                switch (col) {
                    case 0:
                        itemName = match.toLowerCase(Locale.ENGLISH);
                        break;
                    case 1:
                        numeric = Integer.parseInt(match);
                        break;
                    case 2:
                        data = Short.parseShort(match);
                        break;
                    case 3:
                        nbt = StringUtil.stripToNull(match);
                        break;
                    default:
                        continue;
                }
                col++;
            }
            // Invalid row
            if (itemName == null || numeric < 0) {
                continue;
            }

            durabilities.put(itemName, data);
            items.put(itemName, numeric);
            if (nbt != null) {
                nbtData.put(itemName, nbt);
            }

            final ItemData itemData = new ItemData(numeric, data);
            if (names.containsKey(itemData)) {
                final List<String> nameList = names.get(itemData);
                nameList.add(itemName);
            } else {
                final List<String> nameList = new ArrayList<>();
                nameList.add(itemName);
                names.put(itemData, nameList);
                primaryName.put(itemData, itemName);
            }

            legacyIds.put(numeric, itemData);
        }

        for (final List<String> nameList : names.values()) {
            nameList.sort(LengthCompare.INSTANCE);
        }

        ess.getLogger().info(String.format("Loaded %s items from items.csv.", listNames().size()));

        ready = true;
    }

    @Override
    public ItemStack get(final String id, final boolean useResolvers) throws Exception {
        if (useResolvers) {
            final ItemStack resolved = tryResolverDeserialize(id);
            if (resolved != null) {
                return resolved;
            }
        }

        int itemid = 0;
        String itemname;
        short metaData = 0;
        final Matcher parts = splitPattern.matcher(id);
        if (parts.matches()) {
            itemname = parts.group(2);
            metaData = Short.parseShort(parts.group(3));
        } else {
            itemname = id;
        }

        if (NumberUtil.isInt(itemname)) {
            itemid = Integer.parseInt(itemname);
        } else if (NumberUtil.isInt(id)) {
            itemid = Integer.parseInt(id);
        } else {
            itemname = itemname.toLowerCase(Locale.ENGLISH);
        }

        if (itemid < 1) {
            if (items.containsKey(itemname)) {
                itemid = items.get(itemname);
                if (durabilities.containsKey(itemname) && metaData == 0) {
                    metaData = durabilities.get(itemname);
                }
            }
        }

        if (itemid < 1) {
            final Material matFromName = EnumUtil.getMaterial(itemname.toUpperCase());
            if (matFromName != null) {
                itemid = matFromName.getId();
            }
        }

        if (itemid < 1) {
            throw new TranslatableException("unknownItemName", itemname);
        }

        final ItemData data = legacyIds.get(itemid);
        if (data == null) {
            throw new TranslatableException("unknownItemId", itemid);
        }

        final Material mat = getFromLegacy(itemid, (byte) metaData);
        ItemStack retval = new ItemStack(mat);
        if (nbtData.containsKey(itemname)) {
            String nbt = nbtData.get(itemname);
            if (nbt.startsWith("*")) {
                nbt = nbtData.get(nbt.substring(1));
            }
            retval = ess.getServer().getUnsafe().modifyItemStack(retval, nbt);
        }
        final Material MOB_SPAWNER = EnumUtil.getMaterial("SPAWNER", "MOB_SPAWNER");
        if (mat == MOB_SPAWNER) {
            if (metaData == 0) metaData = EntityType.PIG.getTypeId();
            try {
                retval = ess.provider(SpawnerItemProvider.class).setEntityType(retval, EntityType.fromId(metaData));
                ess.provider(PersistentDataProvider.class).set(retval, "convert", "true");
            } catch (final IllegalArgumentException e) {
                throw new Exception("Can't spawn entity ID " + metaData + " from mob spawners.");
            }
        } else if (mat.name().contains("MONSTER_EGG")) {
            final EntityType type;
            try {
                type = EntityType.fromId(metaData);
            } catch (final IllegalArgumentException e) {
                throw new Exception("Can't spawn entity ID " + metaData + " from spawn eggs.");
            }
            retval = ess.provider(SpawnEggProvider.class).createEggItem(type);
        } else if (mat.name().endsWith("POTION")
            && VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_11_R01)) { // Only apply this to pre-1.11 as items.csv might only work in 1.11
            retval = ess.provider(PotionMetaProvider.class).createPotionItem(mat, metaData);
        } else {
            retval.setDurability(metaData);
        }
        retval.setAmount(mat.getMaxStackSize());
        return retval;
    }

    @Override
    public List<String> nameList(final ItemStack item) {
        ItemData itemData = new ItemData(item.getType().getId(), item.getDurability());
        List<String> nameList = names.get(itemData);
        if (nameList == null) {
            itemData = new ItemData(item.getType().getId(), (short) 0);
            nameList = names.get(itemData);
            if (nameList == null) {
                return null;
            }
        }

        return Collections.unmodifiableList(nameList);
    }

    @Override
    public String name(final ItemStack item) {
        ItemData itemData = new ItemData(item.getType().getId(), item.getDurability());
        String name = primaryName.get(itemData);
        if (name == null) {
            itemData = new ItemData(item.getType().getId(), (short) 0);
            name = primaryName.get(itemData);
        }
        return name;
    }

    @Override
    public int getLegacyId(final Material material) throws Exception {
        for (final Map.Entry<String, Integer> entry : items.entrySet()) {
            if (material.name().toLowerCase(Locale.ENGLISH).equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }

        throw new Exception("Itemid not found for material: " + material.name());
    }

    @Override
    public Collection<String> listNames() {
        final Collection<String> values = new ArrayList<>(primaryName.values());
        values.addAll(getResolverNames());
        return values;
    }

    static class ItemData {
        private final int itemNo;
        final private short itemData;

        ItemData(final int itemNo, final short itemData) {
            this.itemNo = itemNo;
            this.itemData = itemData;
        }

        public int getItemNo() {
            return itemNo;
        }

        public short getItemData() {
            return itemData;
        }

        @Override
        public int hashCode() {
            return (31 * itemNo) ^ itemData;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ItemData)) {
                return false;
            }
            final ItemData pairo = (ItemData) o;
            return this.itemNo == pairo.getItemNo() && this.itemData == pairo.getItemData();
        }
    }

    static class LengthCompare implements java.util.Comparator<String> {

        private static final LengthCompare INSTANCE = new LengthCompare();

        LengthCompare() {
            super();
        }

        @Override
        public int compare(final String s1, final String s2) {
            return s1.length() - s2.length();
        }
    }
}
