package com.earth2me.essentials;

import com.earth2me.essentials.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ess3.api.IEssentials;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;


public class ItemDb implements IConf, net.ess3.api.IItemDb {
    protected static final Logger LOGGER = Logger.getLogger("Essentials");
    private final transient IEssentials ess;private static Gson gson = new Gson();

    // Maps primary name to ItemData
    private final transient Map<String, ItemData> items = new HashMap<>();

    // Maps alias to primary name
    private final transient Map<String, String> itemAliases = new HashMap<>();

    // Every known alias
    private final transient Set<String> allAliases = new HashSet<>();

    private transient ManagedFile file = null;

    public ItemDb(final IEssentials ess) {
        this.ess = ess;
    }

    @Override
    public void reloadConfig() {
        if (file == null) {
            file = new ManagedFile("items.json", ess);
        }

        this.rebuild();
        LOGGER.info(String.format("Loaded %s items.", listNames().size()));
    }

    private void rebuild() {
        this.reset();
        this.loadJSON(String.join("\n", file.getLines()));
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
                } catch (Exception e) {
                    // TODO: log invalid entry
                }
            }

            if (valid) {
                allAliases.add(key);
            }
        }
    }

    @Override
    public ItemStack get(final String id, final int quantity) throws Exception {
        ItemStack is = get(id);
        is.setAmount(quantity);
        return is;
    }

    @Override
    public ItemStack get(final String id) throws Exception {
        ItemData data = Objects.requireNonNull(getByName(id));
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
                if (stack == null || stack.getType() == Material.AIR) {
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
        List<String> nameList = nameList(item);

        if (nameList.size() > 15) {
            nameList = nameList.subList(0, 14);
        }
        return StringUtil.joinList(", ", nameList);
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

            Set<ItemFlag> flags = meta.getItemFlags();
            if (flags != null && !flags.isEmpty()) {
                sb.append("itemflags:");
                boolean first = true;
                for (ItemFlag flag : flags) {
                    if (!first) {
                        sb.append(",");
                    }
                    sb.append(flag.name());
                    first = false;
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
            case FIREWORK_ROCKET:
            case FIREWORK_STAR:
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
            case SKELETON_SKULL:
            case WITHER_SKELETON_SKULL:
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
            case BLACK_BANNER:
            case BLUE_BANNER:
            case BROWN_BANNER:
            case CYAN_BANNER:
            case GRAY_BANNER:
            case GREEN_BANNER:
            case LIGHT_BLUE_BANNER:
            case LIGHT_GRAY_BANNER:
            case LIME_BANNER:
            case MAGENTA_BANNER:
            case ORANGE_BANNER:
            case PINK_BANNER:
            case PURPLE_BANNER:
            case RED_BANNER:
            case WHITE_BANNER:
            case YELLOW_BANNER:
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
