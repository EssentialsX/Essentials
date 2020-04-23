package com.earth2me.essentials.items;

import com.earth2me.essentials.ManagedFile;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;
import net.ess3.nms.refl.ReflUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;


public class LegacyItemDb extends AbstractItemDb {
    protected static final Logger LOGGER = Logger.getLogger("Essentials");
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

        for (String line : lines) {
            if (line.length() > 0 && line.charAt(0) == '#') {
                continue;
            }

            String itemName = null;
            int numeric = -1;
            short data = 0;
            String nbt = null;

            int col = 0;
            Matcher matcher = csvSplitPattern.matcher(line);
            while (matcher.find()) {
                String match = matcher.group(1);
                if (StringUtils.stripToNull(match) == null) {
                    continue;
                }
                match = StringUtils.strip(match.trim(), "\"");
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
                        nbt = StringUtils.stripToNull(match);
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

            ItemData itemData = new ItemData(numeric, data);
            if (names.containsKey(itemData)) {
                List<String> nameList = names.get(itemData);
                nameList.add(itemName);
            } else {
                List<String> nameList = new ArrayList<>();
                nameList.add(itemName);
                names.put(itemData, nameList);
                primaryName.put(itemData, itemName);
            }

            legacyIds.put(numeric, itemData);
        }

        for (List<String> nameList : names.values()) {
            Collections.sort(nameList, LengthCompare.INSTANCE);
        }

        LOGGER.info(String.format("Loaded %s items from items.csv.", listNames().size()));

        ready = true;
    }

    @Override
    public ItemStack get(final String id, final boolean useResolvers) throws Exception {
        if (useResolvers) {
            ItemStack resolved = tryResolvers(id);
            if (resolved != null) {
                return resolved;
            }
        }

        int itemid = 0;
        String itemname;
        short metaData = 0;
        Matcher parts = splitPattern.matcher(id);
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
            Material matFromName = EnumUtil.getMaterial(itemname.toUpperCase());
            if (matFromName != null) {
                itemid = matFromName.getId();
            }
        }

        if (itemid < 1) {
            throw new Exception(tl("unknownItemName", itemname));
        }

        ItemData data = legacyIds.get(itemid);
        if (data == null) {
            throw new Exception(tl("unknownItemId", itemid));
        }

        Material mat = getFromLegacy(itemid, (byte) metaData);
        ItemStack retval = new ItemStack(mat);
        if (nbtData.containsKey(itemname)) {
            String nbt = nbtData.get(itemname);
            if (nbt.startsWith("*")) {
                nbt = nbtData.get(nbt.substring(1));
            }
            retval = ess.getServer().getUnsafe().modifyItemStack(retval, nbt);
        }
        Material MOB_SPAWNER = EnumUtil.getMaterial("SPAWNER", "MOB_SPAWNER");
        if (mat == MOB_SPAWNER) {
            if (metaData == 0) metaData = EntityType.PIG.getTypeId();
            try {
                retval = ess.getSpawnerProvider().setEntityType(retval, EntityType.fromId(metaData));
            } catch (IllegalArgumentException e) {
                throw new Exception("Can't spawn entity ID " + metaData + " from mob spawners.");
            }
        } else if (mat.name().contains("MONSTER_EGG")) {
            EntityType type;
            try {
                type = EntityType.fromId(metaData);
            } catch (IllegalArgumentException e) {
                throw new Exception("Can't spawn entity ID " + metaData + " from spawn eggs.");
            }
            retval = ess.getSpawnEggProvider().createEggItem(type);
        } else if (mat.name().endsWith("POTION")
                && ReflUtil.getNmsVersionObject().isLowerThan(ReflUtil.V1_11_R1)) { // Only apply this to pre-1.11 as items.csv might only work in 1.11
            retval = ess.getPotionMetaProvider().createPotionItem(mat, metaData);
        } else {
            retval.setDurability(metaData);
        }
        retval.setAmount(mat.getMaxStackSize());
        return retval;
    }

    @Override
    public List<String> nameList(ItemStack item) {
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
    public String name(ItemStack item) {
        ItemData itemData = new ItemData(item.getType().getId(), item.getDurability());
        String name = primaryName.get(itemData);
        if (name == null) {
            itemData = new ItemData(item.getType().getId(), (short) 0);
            name = primaryName.get(itemData);
            if (name == null) {
                return null;
            }
        }
        return name;
    }

    @Override
    public int getLegacyId(Material material) throws Exception {
        for(Map.Entry<String, Integer> entry : items.entrySet()) {
            if(material.name().toLowerCase(Locale.ENGLISH).equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }

        throw new Exception("Itemid not found for material: " + material.name());
    }

    @Override
    public Collection<String> listNames() {
        Collection<String> values = new ArrayList<>(primaryName.values());
        values.addAll(getResolverNames());
        return values;
    }

    static class ItemData {
        private int itemNo;
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
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ItemData)) {
                return false;
            }
            ItemData pairo = (ItemData) o;
            return this.itemNo == pairo.getItemNo() && this.itemData == pairo.getItemData();
        }
    }


    static class LengthCompare implements java.util.Comparator<String> {

        private static final LengthCompare INSTANCE = new LengthCompare();

        public LengthCompare() {
            super();
        }

        @Override
        public int compare(String s1, String s2) {
            return s1.length() - s2.length();
        }
    }
}
