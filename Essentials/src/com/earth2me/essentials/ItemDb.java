package com.earth2me.essentials;

import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.IEssentials;
import net.ess3.nms.ItemDbProvider;
import net.ess3.nms.ids.LegacyItemDbProvider;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;


public class ItemDb implements IConf, net.ess3.api.IItemDb {
    protected static final Logger LOGGER = Logger.getLogger("Essentials");
    private final transient IEssentials ess;
    private final transient ItemDbProvider provider;

    private final transient ManagedFile file;

    public ItemDb(final IEssentials ess) {
        this.ess = ess;
        this.provider = ess.getItemDbProvider();

        if (provider instanceof LegacyItemDbProvider) {
            file = new ManagedFile("items.csv", ess);
        } else {
            file = new ManagedFile("items.json", ess);
        }
    }

    @Override
    public void reloadConfig() {
        provider.rebuild(file.getLines());
    }

    @Override
    public ItemStack get(final String id, final int quantity) throws Exception {
        return provider.getStack(id, quantity);
    }

    @Override
    public ItemStack get(final String id) throws Exception {
        return provider.getStack(id);
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
        List<String> nameList = provider.getNames(item);

        if (nameList.size() > 15) {
            nameList = nameList.subList(0, 14);
        }
        return StringUtil.joinList(", ", nameList);
    }

    @Override
    public String name(ItemStack item) {
        return provider.getPrimaryName(item);
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
    public Material getFromLegacyId(int id) {
        return provider.getFromLegacyId(id);
    }

    @Override
    public int getLegacyId(Material material) throws Exception {
        return provider.getLegacyId(material);
    }

    @Override
    public Collection<String> listNames() {
        return provider.listNames();
    }

    static class ItemData {
        final private Material material;
        private int legacyId;
        final private short itemData;

        ItemData(Material material, short itemData) {
            this.material = material;
            this.itemData = itemData;
        }

        @Deprecated
        ItemData(Material material, final int legacyId, final short itemData) {
            this.material = material;
            this.legacyId = legacyId;
            this.itemData = itemData;
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
            return this.material == pairo.getMaterial() && this.itemData == pairo.getItemData();
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
