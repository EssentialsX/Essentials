package com.earth2me.essentials;

import com.earth2me.essentials.utils.NumberUtil;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.nms.updatedmeta.BasePotionDataProvider;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.IEssentials;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;


public class ItemDb implements IConf, net.ess3.api.IItemDb {
    private final transient IEssentials ess;
    private final transient Map<String, Integer> items = new HashMap<>();
    private final transient Map<ItemData, List<String>> names = new HashMap<>();
    private final transient Map<ItemData, String> primaryName = new HashMap<>();
    private final transient Map<String, Short> durabilities = new HashMap<>();
    private final transient Map<String, String> nbtData = new HashMap<>();
    private final transient ManagedFile file;
    private final transient Pattern splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");
    private final transient Pattern csvSplitPattern = Pattern.compile("(\"([^\"]*)\"|[^,]*)(,|$)");

    public ItemDb(final IEssentials ess) {
        this.ess = ess;
        file = new ManagedFile("items.csv", ess);
    }

    @Override
    public void reloadConfig() {
        final List<String> lines = file.getLines();

        if (lines.isEmpty()) {
            return;
        }

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
        }

        for (List<String> nameList : names.values()) {
            Collections.sort(nameList, LengthCompare.INSTANCE);
        }
    }

    @Override
    public ItemStack get(final String id, final int quantity) throws Exception {
        final ItemStack retval = get(id.toLowerCase(Locale.ENGLISH));
        retval.setAmount(quantity);
        return retval;
    }

    @Override
    public ItemStack get(final String id) throws Exception {
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
            } else if (Material.getMaterial(itemname.toUpperCase(Locale.ENGLISH)) != null) {
                Material bMaterial = Material.getMaterial(itemname.toUpperCase(Locale.ENGLISH));
                itemid = bMaterial.getId();
            } else {
                try {
                    Material bMaterial = Bukkit.getUnsafe().getMaterialFromInternalName(itemname.toLowerCase(Locale.ENGLISH));
                    itemid = bMaterial.getId();
                } catch (Throwable throwable) {
                    throw new Exception(tl("unknownItemName", itemname), throwable);
                }
            }
        }

        if (itemid < 1) {
            throw new Exception(tl("unknownItemName", itemname));
        }

        final Material mat = Material.getMaterial(itemid);
        if (mat == null) {
            throw new Exception(tl("unknownItemId", itemid));
        }
        ItemStack retval = new ItemStack(mat);
        if (nbtData.containsKey(itemname)) {
            String nbt = nbtData.get(itemname);
            if (nbt.startsWith("*")) {
                nbt = nbtData.get(nbt.substring(1));
            }
            retval = ess.getServer().getUnsafe().modifyItemStack(retval, nbt);
        }
        if (mat == Material.MOB_SPAWNER) {
            if (metaData == 0) metaData = EntityType.PIG.getTypeId();
            try {
                retval = ess.getSpawnerProvider().setEntityType(retval, EntityType.fromId(metaData));
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
    public List<ItemStack> getMatching(User user, String[] args) throws Exception {
        List<ItemStack> is = new ArrayList<>();

        if (args.length < 1) {
            is.add(user.getItemInHand().clone());
        } else if (args[0].equalsIgnoreCase("hand")) {
            is.add(user.getItemInHand().clone());
        } else if (args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all")) {
            for (ItemStack stack : user.getBase().getInventory().getContents()) {
                if (stack == null || stack.getType() == Material.AIR) {
                    continue;
                }
                is.add(stack.clone());
            }
        } else if (args[0].equalsIgnoreCase("blocks")) {
            for (ItemStack stack : user.getBase().getInventory().getContents()) {
                if (stack == null || stack.getTypeId() > 255 || stack.getType() == Material.AIR) {
                    continue;
                }
                is.add(stack.clone());
            }
        } else {
            is.add(get(args[0]));
        }

        if (is.isEmpty() || is.get(0).getType() == Material.AIR) {
            throw new Exception(tl("itemSellAir"));
        }

        return is;
    }

    @Override
    public String names(ItemStack item) {
        ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
        List<String> nameList = names.get(itemData);
        if (nameList == null) {
            itemData = new ItemData(item.getTypeId(), (short) 0);
            nameList = names.get(itemData);
            if (nameList == null) {
                return null;
            }
        }

        if (nameList.size() > 15) {
            nameList = nameList.subList(0, 14);
        }
        return StringUtil.joinList(", ", nameList);
    }

    @Override
    public String name(ItemStack item) {
        ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
        String name = primaryName.get(itemData);
        if (name == null) {
            itemData = new ItemData(item.getTypeId(), (short) 0);
            name = primaryName.get(itemData);
            if (name == null) {
                return null;
            }
        }
        return name;
    }

    @Override
    public String serialize(ItemStack is) {
        String mat = is.getType().name();
        if (is.getData().getData() != 0) {
            mat = mat + ":" + is.getData().getData();
        }
        int quantity = is.getAmount();
        StringBuilder sb = new StringBuilder(); // Add space AFTER you add something. We can trim at end.
        sb.append(mat).append(" ").append(quantity).append(" ");

        // ItemMeta applies to anything.
        if (is.hasItemMeta()) {
            ItemMeta meta = is.getItemMeta();
            if (meta.hasDisplayName()) {
                sb.append("name:").append(meta.getDisplayName().replaceAll(" ", "_")).append(" ");
            }

            if (meta.hasLore()) {
                sb.append("lore:");
                boolean first = true;
                for (String s : meta.getLore()) {
                    // Add | before the line if it's not the first one. Easy but weird way
                    // to do this since we need each line separated by |
                    if (!first) {
                        sb.append("|");
                    }
                    first = false;
                    sb.append(s.replaceAll(" ", "_"));
                }
                sb.append(" ");
            }

            if (meta.hasEnchants()) {
                for (Enchantment e : meta.getEnchants().keySet()) {
                    sb.append(e.getName().toLowerCase()).append(":").append(meta.getEnchantLevel(e)).append(" ");
                }
            }
        }

        switch (is.getType()) {
            case WRITTEN_BOOK:
                // Everything from http://wiki.ess3.net/wiki/Item_Meta#Books in that order.
                // Interesting as I didn't see a way to do pages or chapters.
                BookMeta bookMeta = (BookMeta) is.getItemMeta();
                if (bookMeta.hasTitle()) {
                    sb.append("title:").append(bookMeta.getTitle()).append(" ");
                }
                if (bookMeta.hasAuthor()) {
                    sb.append("author:").append(bookMeta.getAuthor()).append(" ");
                }
                // Only other thing it could have is lore but that's done up there ^^^
                break;
            case ENCHANTED_BOOK:
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) is.getItemMeta();
                for (Enchantment e : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                    sb.append(e.getName().toLowerCase()).append(":").append(enchantmentStorageMeta.getStoredEnchantLevel(e)).append(" ");
                }
                break;
            case FIREWORK:
                // Everything from http://wiki.ess3.net/wiki/Item_Meta#Fireworks in that order.
                FireworkMeta fireworkMeta = (FireworkMeta) is.getItemMeta();
                if (fireworkMeta.hasEffects()) {
                    for (FireworkEffect effect : fireworkMeta.getEffects()) {
                        if (effect.getColors() != null && !effect.getColors().isEmpty()) {
                            sb.append("color:");
                            boolean first = true;
                            for (Color c : effect.getColors()) {
                                if (!first) {
                                    sb.append(","); // same thing as above.
                                }
                                sb.append(c.toString());
                                first = false;
                            }
                            sb.append(" ");
                        }

                        sb.append("shape: ").append(effect.getType().name()).append(" ");
                        if (effect.getFadeColors() != null && !effect.getFadeColors().isEmpty()) {
                            sb.append("fade:");
                            boolean first = true;
                            for (Color c : effect.getFadeColors()) {
                                if (!first) {
                                    sb.append(","); // same thing as above.
                                }
                                sb.append(c.toString());
                                first = false;
                            }
                            sb.append(" ");
                        }
                    }
                    sb.append("power: ").append(fireworkMeta.getPower()).append(" ");
                }
                break;
            case POTION:
                Potion potion = Potion.fromItemStack(is);
                for (PotionEffect e : potion.getEffects()) {
                    // long but needs to be effect:speed power:2 duration:120 in that order.
                    sb.append("splash:").append(potion.isSplash()).append(" ").append("effect:").append(e.getType().getName().toLowerCase()).append(" ").append("power:").append(e.getAmplifier()).append(" ").append("duration:").append(e.getDuration() / 20).append(" ");
                }
                break;
            case SKULL_ITEM:
                // item stack with meta
                SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
                if (skullMeta != null && skullMeta.hasOwner()) {
                    sb.append("player:").append(skullMeta.getOwner()).append(" ");
                }
                break;
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) is.getItemMeta();
                int rgb = leatherArmorMeta.getColor().asRGB();
                sb.append("color:").append(rgb).append(" ");
                break;
            case BANNER:
                BannerMeta bannerMeta = (BannerMeta) is.getItemMeta();
                if (bannerMeta != null) {
                    int basecolor = bannerMeta.getBaseColor().getColor().asRGB();
                    sb.append("basecolor:").append(basecolor).append(" ");
                    for (org.bukkit.block.banner.Pattern p : bannerMeta.getPatterns()) {
                        String type = p.getPattern().getIdentifier();
                        int color = p.getColor().getColor().asRGB();
                        sb.append(type).append(",").append(color).append(" ");
                    }
                }
                break;
            case SHIELD:
                // Hacky fix for accessing Shield meta - https://github.com/drtshock/Essentials/pull/745#issuecomment-234843795
                BlockStateMeta shieldMeta = (BlockStateMeta) is.getItemMeta();
                Banner shieldBannerMeta = (Banner) shieldMeta.getBlockState();
                int basecolor = shieldBannerMeta.getBaseColor().getColor().asRGB();
                sb.append("basecolor:").append(basecolor).append(" ");
                for (org.bukkit.block.banner.Pattern p : shieldBannerMeta.getPatterns()) {
                    String type = p.getPattern().getIdentifier();
                    int color = p.getColor().getColor().asRGB();
                    sb.append(type).append(",").append(color).append(" ");
                }
                break;
        }

        return sb.toString().trim().replaceAll("§", "&");
    }

    @Override
    public Collection<String> listNames() {
        return primaryName.values();
    }

    static class ItemData {
        final private int itemNo;
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
