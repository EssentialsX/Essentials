package net.ess3.nms.ids;

import net.ess3.nms.ItemDbProvider;
import net.ess3.nms.PotionMetaProvider;
import net.ess3.nms.SpawnEggProvider;
import net.ess3.nms.refl.ReflUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyItemDbProvider extends ItemDbProvider {
    private final transient Map<String, Integer> items = new HashMap<>();
    private final transient Map<ItemData, List<String>> names = new HashMap<>();
    private final transient Map<ItemData, String> primaryNames = new HashMap<>();
    private final transient Map<Integer, ItemData> legacyIds = new HashMap<>();
    private final transient Map<String, Short> durabilities = new HashMap<>();
    private final transient Map<String, String> nbtData = new HashMap<>();

    private final transient Pattern splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");
    private final transient Pattern csvSplitPattern = Pattern.compile("(\"([^\"]*)\"|[^,]*)(,|$)");

    @Override
    public Material resolve(String name) {
        // TODO: refactor getStack into here
        return null;
    }

    @Override
    public ItemStack getStack(String name) throws Exception {
        int itemid = 0;
        String itemname;
        short metaData = 0;

        Matcher parts = splitPattern.matcher(name);
        if (parts.matches()) {
            itemname = parts.group(2);
            metaData = Short.parseShort(parts.group(3));
        } else {
            itemname = name;
        }

        if (isInt(itemname)) {
            itemid = Integer.parseInt(itemname);
        } else if (isInt(name)) {
            itemid = Integer.parseInt(name);
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
            throw new Exception("Unknown item name " + itemname);
        }

        ItemData data = legacyIds.get(itemid);
        if (data == null) {
            throw new Exception("Unknown item ID " + itemid);
        }

        Material mat = data.getMaterial();
        ItemStack retval = new ItemStack(mat);
        if (nbtData.containsKey(itemname)) {
            String nbt = nbtData.get(itemname);
            if (nbt.startsWith("*")) {
                nbt = nbtData.get(nbt.substring(1));
            }
            retval = Bukkit.getServer().getUnsafe().modifyItemStack(retval, nbt);
        }


        Material MOB_SPAWNER;
        try {
            MOB_SPAWNER = Material.valueOf("SPAWNER");
        } catch (Exception e) {
            MOB_SPAWNER = Material.valueOf("MOB_SPAWNER");
        }
        if (mat == MOB_SPAWNER) {
            if (metaData == 0) metaData = EntityType.PIG.getTypeId();
            try {
                retval = getSpawnerProvider().setEntityType(retval, EntityType.fromId(metaData));
            } catch (IllegalArgumentException e) {
                throw new Exception("Can't spawn entity ID " + metaData + " from mob spawners.");
            }
        } else if (mat == Material.MONSTER_EGG) {
            EntityType type;
            try {
                type = EntityType.fromId(metaData);
            } catch (IllegalArgumentException e) {
                throw new Exception("Can't spawn entity ID " + metaData + " from spawn eggs.");
            }
            retval = getSpawnEggProvider().createEggItem(type);
        } else if ((mat.name().endsWith("POTION") || mat.name().equals("TIPPED_ARROW"))
                && ReflUtil.getNmsVersionObject().isLowerThan(ReflUtil.V1_11_R1)) { // Only apply this to pre-1.11 as items.csv might only work in 1.11
            retval = getPotionMetaProvider().createPotionItem(mat, metaData);
        } else {
            retval.setDurability(metaData);
        }
        retval.setAmount(mat.getMaxStackSize());
        return retval;
    }

    @Override
    public boolean supportsLegacyIds() {
        return true;
    }

    @Override
    public int getLegacyId(Material material) throws Exception {
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            if (material.name().toLowerCase(Locale.ENGLISH).equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }

        throw new Exception("Item ID missing for material " + material.name());
    }

    @Override
    public Material getFromLegacyId(int id) {
        ItemData data = this.legacyIds.get(id);
        if (data == null) {
            return null;
        }

        return data.getMaterial();
    }

    @Override
    public String getPrimaryName(ItemStack item) {
        ItemData itemData = new ItemData(null, item.getType(), item.getTypeId(), item.getDurability(), null);
        String name = primaryNames.get(itemData);
        if (name == null) {
            itemData = new ItemData(null, item.getType(), item.getTypeId(), (short) 0, null);
            name = primaryNames.get(itemData);
            if (name == null) {
                return null;
            }
        }
        return name;
    }

    @Override
    public List<String> getNames(ItemStack item) {
        ItemData itemData = new ItemData(null, item.getType(), item.getTypeId(), item.getDurability(), null);
        List<String> nameList = names.get(itemData);
        if (nameList == null) {
            itemData = new ItemData(null, item.getType(), item.getTypeId(), (short) 0, null);
            nameList = names.get(itemData);
            if (nameList == null) {
                return null;
            }
        }

        return nameList;
    }

    @Override
    public void rebuild(List<String> lines) {
        durabilities.clear();
        items.clear();
        names.clear();
        primaryNames.clear();

        lines.stream()
                .filter(line -> line.length() > 0 && !(line.charAt(0) == '#'))
                .map(this::parseLine)
                .filter(Objects::nonNull)
                .forEach(this::addItem);

        for (List<String> nameList : names.values()) {
            nameList.sort(LengthCompare.INSTANCE);
        }
    }

    private ItemData parseLine(String line) {
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
            return null;
        }

        Material material = Material.matchMaterial(itemName);
        if (material == null) {
            return null;
        }

        return new ItemData(itemName, material, numeric, data, nbt);
    }

    private void addItem(ItemData itemData) {
        final String name = itemData.getItemName();
        final int numeric = itemData.getItemNo();
        final short data = itemData.getItemData();
        final String nbt = itemData.getNbt();

        durabilities.put(name, data);
        items.put(name, numeric);

        if (nbt != null) {
            nbtData.put(itemData.getItemName(), nbt);
        }

        if (names.containsKey(itemData)) {
            List<String> nameList = names.get(itemData);
            nameList.add(name);
        } else {
            List<String> nameList = new ArrayList<>();
            nameList.add(name);
            names.put(itemData, nameList);
            primaryNames.put(itemData, name);
        }

        legacyIds.put(numeric, itemData);
    }

    @Override
    public boolean tryProvider() {
        // Build the database initially so that we can actually test the provider
        this.rebuild(this.loadResource("/items.csv"));
        return super.tryProvider();
    }

    @Override
    public String getHumanName() {
        return "Pre-1.13 item database provider";
    }

    private boolean isInt(String integer) {
        try {
            Integer.parseInt(integer);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
