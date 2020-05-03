package com.earth2me.essentials.items;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.PluginKey;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;

public abstract class AbstractItemDb implements IConf, net.ess3.api.IItemDb {

    protected final IEssentials ess;
    protected boolean ready = false;

    private final Map<PluginKey, ItemResolver> resolverMap = new HashMap<>();

    AbstractItemDb(IEssentials ess) {
        this.ess = ess;
    }

    @Override
    public void registerResolver(Plugin plugin, String name, ItemResolver resolver) throws Exception {
        PluginKey key = PluginKey.fromKey(plugin, name);
        if (resolverMap.containsKey(key)) {
            throw new Exception("Tried to add a duplicate resolver with name " + key.toString());
        }

        resolverMap.put(key, resolver);
    }

    @Override
    public void unregisterResolver(Plugin plugin, String name) throws Exception {
        PluginKey key = PluginKey.fromKey(plugin, name);
        if (!resolverMap.containsKey(key)) {
            throw new Exception("Tried to remove nonexistent resolver with name " + key.toString());
        }

        resolverMap.remove(key);
    }

    @Override
    public boolean isResolverPresent(Plugin plugin, String name) {
        return resolverMap.containsKey(PluginKey.fromKey(plugin, name));
    }

    @Override
    public Map<PluginKey, ItemResolver> getResolvers() {
        return new HashMap<>(resolverMap);
    }

    @Override
    public Map<PluginKey, ItemResolver> getResolvers(Plugin plugin) {
        Map<PluginKey, ItemResolver> matchingResolvers = new HashMap<>();
        for (PluginKey key : resolverMap.keySet()) {
            if (key.getPlugin().equals(plugin)) {
                matchingResolvers.put(key, resolverMap.get(key));
            }
        }

        return matchingResolvers;
    }

    @Override
    public ItemResolver getResolver(Plugin plugin, String name) {
        return resolverMap.get(PluginKey.fromKey(plugin, name));
    }

    @Override
    public ItemStack get(String id) throws Exception {
        return get(id, true);
    }

    ItemStack tryResolvers(String id) {
        for (PluginKey key : resolverMap.keySet()) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info(String.format("Trying resolver '%s' for item '%s'...", key, id));
            }

            Function<String, ItemStack> resolver = resolverMap.get(key);
            ItemStack stack = resolver.apply(id);

            if (stack != null) {
                return stack;
            }
        }

        return null;
    }

    Collection<String> getResolverNames() {
        return resolverMap.values().stream()
            .map(ItemResolver::getNames)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
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
    public String serialize(ItemStack is) {
        String mat = name(is);
        if (VersionUtil.getServerBukkitVersion().isLowerThanOrEqualTo(VersionUtil.v1_12_2_R01) && is.getData().getData() != 0) {
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

        final Material material = is.getType();

        switch (material) {
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
        }

        if (MaterialUtil.isFirework(material)) {
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

                    sb.append("shape:").append(effect.getType().name()).append(" ");
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
                sb.append("power:").append(fireworkMeta.getPower()).append(" ");
            }
        } else if (MaterialUtil.isPotion(material)) {
            Potion potion = Potion.fromItemStack(is);
            for (PotionEffect e : potion.getEffects()) {
                // long but needs to be effect:speed power:2 duration:120 in that order.
                sb.append("splash:").append(potion.isSplash()).append(" ").append("effect:").append(e.getType().getName().toLowerCase()).append(" ").append("power:").append(e.getAmplifier()).append(" ").append("duration:").append(e.getDuration() / 20).append(" ");
            }
        } else if (MaterialUtil.isPlayerHead(material, is.getData().getData())) {
            // item stack with meta
            SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
            if (skullMeta != null && skullMeta.hasOwner()) {
                sb.append("player:").append(skullMeta.getOwner()).append(" ");
            }
        } else if (MaterialUtil.isBanner(material)) {
            if (material.toString().contains("SHIELD")) {
                // Hacky fix for accessing Shield meta - https://github.com/drtshock/Essentials/pull/745#issuecomment-234843795
                BlockStateMeta shieldMeta = (BlockStateMeta) is.getItemMeta();
                Banner shieldBannerMeta = (Banner) shieldMeta.getBlockState();
                DyeColor baseDyeColor = shieldBannerMeta.getBaseColor();
                if (baseDyeColor != null) {
                    int basecolor = baseDyeColor.getColor().asRGB();
                    sb.append("basecolor:").append(basecolor).append(" ");
                }
                for (org.bukkit.block.banner.Pattern p : shieldBannerMeta.getPatterns()) {
                    String type = p.getPattern().getIdentifier();
                    int color = p.getColor().getColor().asRGB();
                    sb.append(type).append(",").append(color).append(" ");
                }
            } else {
                BannerMeta bannerMeta = (BannerMeta) is.getItemMeta();
                if (bannerMeta != null) {
                    DyeColor baseDyeColor = bannerMeta.getBaseColor();
                    if (baseDyeColor == null) {
                        baseDyeColor = MaterialUtil.getColorOf(material);
                    }
                    if (baseDyeColor != null) {
                        int basecolor = baseDyeColor
                            .getColor()
                            .asRGB();
                        sb.append("basecolor:").append(basecolor).append(" ");
                    }
                    for (org.bukkit.block.banner.Pattern p : bannerMeta.getPatterns()) {
                        String type = p.getPattern().getIdentifier();
                        int color = p.getColor().getColor().asRGB();
                        sb.append(type).append(",").append(color).append(" ");
                    }
                }
            }
        } else if (MaterialUtil.isLeatherArmor(material)) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) is.getItemMeta();
            int rgb = leatherArmorMeta.getColor().asRGB();
            sb.append("color:").append(rgb).append(" ");
        }

        return sb.toString().trim().replaceAll("§", "&");
    }

    @Override
    public boolean isReady() {
        return ready;
    }
}
