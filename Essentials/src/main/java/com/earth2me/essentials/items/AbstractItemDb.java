package com.earth2me.essentials.items;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.PluginKey;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.earth2me.essentials.I18n.tl;

public abstract class AbstractItemDb implements IConf, net.ess3.api.IItemDb {

    protected final IEssentials ess;
    private final Map<PluginKey, ItemResolver> resolverMap = new HashMap<>();
    protected boolean ready = false;

    AbstractItemDb(final IEssentials ess) {
        this.ess = ess;
    }

    @Override
    public void registerResolver(final Plugin plugin, final String name, final ItemResolver resolver) throws Exception {
        final PluginKey key = PluginKey.fromKey(plugin, name);
        if (resolverMap.containsKey(key)) {
            throw new Exception("Tried to add a duplicate resolver with name " + key.toString());
        }

        resolverMap.put(key, resolver);
    }

    @Override
    public void unregisterResolver(final Plugin plugin, final String name) throws Exception {
        final PluginKey key = PluginKey.fromKey(plugin, name);
        if (!resolverMap.containsKey(key)) {
            throw new Exception("Tried to remove nonexistent resolver with name " + key.toString());
        }

        resolverMap.remove(key);
    }

    @Override
    public boolean isResolverPresent(final Plugin plugin, final String name) {
        return resolverMap.containsKey(PluginKey.fromKey(plugin, name));
    }

    @Override
    public Map<PluginKey, ItemResolver> getResolvers() {
        return new HashMap<>(resolverMap);
    }

    @Override
    public Map<PluginKey, ItemResolver> getResolvers(final Plugin plugin) {
        final Map<PluginKey, ItemResolver> matchingResolvers = new HashMap<>();
        for (final PluginKey key : resolverMap.keySet()) {
            if (key.getPlugin().equals(plugin)) {
                matchingResolvers.put(key, resolverMap.get(key));
            }
        }

        return matchingResolvers;
    }

    @Override
    public ItemResolver getResolver(final Plugin plugin, final String name) {
        return resolverMap.get(PluginKey.fromKey(plugin, name));
    }

    @Override
    public ItemStack get(final String id) throws Exception {
        return get(id, true);
    }

    ItemStack tryResolverDeserialize(final String id) {
        for (final PluginKey key : resolverMap.keySet()) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info(String.format("Trying to deserialize item '%s' with resolver '%s'...", id, key));
            }

            final Function<String, ItemStack> resolver = resolverMap.get(key);
            final ItemStack stack = resolver.apply(id);

            if (stack != null) {
                return stack;
            }
        }

        return null;
    }

    String tryResolverSerialize(final ItemStack stack) {
        for (final PluginKey key : resolverMap.keySet()) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info(String.format("Trying to serialize '%s' with resolver '%s'...", stack.toString(), key));
            }

            final ItemResolver resolver = resolverMap.get(key);
            final String serialized = resolver.serialize(stack);

            if (serialized != null) {
                return serialized;
            }
        }

        return null;
    }

    Collection<String> getResolverNames() {
        final List<String> result = new ArrayList<>();
        for (final ItemResolver resolver : resolverMap.values()) {
            final Collection<String> resolverNames = resolver.getNames();
            if (resolverNames != null) {
                result.addAll(resolverNames);
            }
        }
        return result;
    }

    @Override
    public List<ItemStack> getMatching(final User user, final String[] args) throws Exception {
        final List<ItemStack> is = new ArrayList<>();

        if (args.length < 1) {
            is.add(user.getItemInHand().clone());
        } else if (args[0].equalsIgnoreCase("hand")) {
            is.add(user.getItemInHand().clone());
        } else if (args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all")) {
            for (final ItemStack stack : user.getBase().getInventory().getContents()) {
                if (stack == null || stack.getType() == Material.AIR) {
                    continue;
                }
                is.add(stack.clone());
            }
        } else if (args[0].equalsIgnoreCase("blocks")) {
            for (final ItemStack stack : user.getBase().getInventory().getContents()) {
                if (stack == null || stack.getType() == Material.AIR || !stack.getType().isBlock()) {
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
    public String serialize(final ItemStack is) {
        return serialize(is, true);
    }

    @Override
    public String serialize(final ItemStack is, final boolean useResolvers) {
        if (useResolvers) {
            final String serialized = tryResolverSerialize(is);
            if (serialized != null) {
                return serialized;
            }
        }

        String mat = name(is);
        if (VersionUtil.getServerBukkitVersion().isLowerThanOrEqualTo(VersionUtil.v1_12_2_R01) && is.getData().getData() != 0) {
            mat = mat + ":" + is.getData().getData();
        }
        final int quantity = is.getAmount();
        final StringBuilder sb = new StringBuilder(); // Add space AFTER you add something. We can trim at end.
        sb.append(mat).append(" ").append(quantity).append(" ");

        // ItemMeta applies to anything.
        if (is.hasItemMeta()) {
            final ItemMeta meta = is.getItemMeta();
            if (meta.hasDisplayName()) {
                sb.append("name:").append(FormatUtil.unformatString(meta.getDisplayName()).replace(" ", "_")).append(" ");
            }

            if (meta.hasLore()) {
                sb.append("lore:").append(serializeLines(meta.getLore())).append(" ");
            }

            if (meta.hasEnchants()) {
                for (final Enchantment e : meta.getEnchants().keySet()) {
                    sb.append(e.getName().toLowerCase()).append(":").append(meta.getEnchantLevel(e)).append(" ");
                }
            }

            final Set<ItemFlag> flags = meta.getItemFlags();
            if (flags != null && !flags.isEmpty()) {
                sb.append("itemflags:");
                boolean first = true;
                for (final ItemFlag flag : flags) {
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
            case WRITABLE_BOOK:
                // Everything from http://wiki.ess3.net/wiki/Item_Meta#Books in that order.
                // Interesting as I didn't see a way to do pages or chapters.
                final BookMeta bookMeta = (BookMeta) is.getItemMeta();
                if (bookMeta.hasTitle()) {
                    sb.append("title:").append(FormatUtil.unformatString(bookMeta.getTitle()).replace(' ', '_')).append(" ");
                }
                if (bookMeta.hasAuthor()) {
                    sb.append("author:").append(FormatUtil.unformatString(bookMeta.getAuthor()).replace(' ', '_')).append(" ");
                }
                if (bookMeta.hasPages()) {
                    final List<String> pages = bookMeta.getPages();
                    for (int i = 0; i < pages.size(); i++) {
                        sb.append("page").append(i + 1).append(":");
                        sb.append(serializeLines(Arrays.asList(pages.get(i).split("\n"))));
                        sb.append(" ");
                    }
                }
                // Only other thing it could have is lore but that's done up there ^^^
                break;
            case ENCHANTED_BOOK:
                final EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) is.getItemMeta();
                for (final Enchantment e : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                    sb.append(e.getName().toLowerCase()).append(":").append(enchantmentStorageMeta.getStoredEnchantLevel(e)).append(" ");
                }
                break;
        }

        if (MaterialUtil.isFirework(material)) {
            // Everything from http://wiki.ess3.net/wiki/Item_Meta#Fireworks in that order.
            final FireworkMeta fireworkMeta = (FireworkMeta) is.getItemMeta();
            if (fireworkMeta.hasEffects()) {
                for (final FireworkEffect effect : fireworkMeta.getEffects()) {
                    serializeEffectMeta(sb, effect);
                }
                sb.append("power:").append(fireworkMeta.getPower()).append(" ");
            }
        } else if (MaterialUtil.isFireworkCharge(material)) {
            final FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) is.getItemMeta();
            if (fireworkEffectMeta.hasEffect()) {
                serializeEffectMeta(sb, fireworkEffectMeta.getEffect());
            }
        } else if (MaterialUtil.isPotion(material)) {
            final Potion potion = Potion.fromDamage(is.getDurability());
            for (final PotionEffect e : potion.getEffects()) {
                // long but needs to be effect:speed power:2 duration:120 in that order.
                sb.append("splash:").append(potion.isSplash()).append(" ").append("effect:").append(e.getType().getName().toLowerCase()).append(" ").append("power:").append(e.getAmplifier()).append(" ").append("duration:").append(e.getDuration() / 20).append(" ");
            }
        } else if (MaterialUtil.isPlayerHead(material, is.getData().getData())) {
            // item stack with meta
            final SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
            if (skullMeta != null && skullMeta.hasOwner()) {
                sb.append("player:").append(skullMeta.getOwner()).append(" ");
            }
        } else if (MaterialUtil.isBanner(material)) {
            if (material.toString().contains("SHIELD")) {
                // Hacky fix for accessing Shield meta - https://github.com/drtshock/Essentials/pull/745#issuecomment-234843795
                final BlockStateMeta shieldMeta = (BlockStateMeta) is.getItemMeta();
                final Banner shieldBannerMeta = (Banner) shieldMeta.getBlockState();
                final DyeColor baseDyeColor = shieldBannerMeta.getBaseColor();
                if (baseDyeColor != null) {
                    final int basecolor = baseDyeColor.getColor().asRGB();
                    sb.append("basecolor:").append(basecolor).append(" ");
                }
                for (final org.bukkit.block.banner.Pattern p : shieldBannerMeta.getPatterns()) {
                    final String type = p.getPattern().getIdentifier();
                    final int color = p.getColor().getColor().asRGB();
                    sb.append(type).append(",").append(color).append(" ");
                }
            } else {
                final BannerMeta bannerMeta = (BannerMeta) is.getItemMeta();
                if (bannerMeta != null) {
                    DyeColor baseDyeColor = bannerMeta.getBaseColor();
                    if (baseDyeColor == null) {
                        baseDyeColor = MaterialUtil.getColorOf(material);
                    }
                    if (baseDyeColor != null) {
                        final int basecolor = baseDyeColor
                            .getColor()
                            .asRGB();
                        sb.append("basecolor:").append(basecolor).append(" ");
                    }
                    for (final org.bukkit.block.banner.Pattern p : bannerMeta.getPatterns()) {
                        final String type = p.getPattern().getIdentifier();
                        final int color = p.getColor().getColor().asRGB();
                        sb.append(type).append(",").append(color).append(" ");
                    }
                }
            }
        } else if (MaterialUtil.isLeatherArmor(material)) {
            final LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) is.getItemMeta();
            final int rgb = leatherArmorMeta.getColor().asRGB();
            sb.append("color:").append(rgb).append(" ");
        }

        return sb.toString().trim().replaceAll("§", "&");
    }

    private void serializeEffectMeta(StringBuilder sb, FireworkEffect effect) {
        if (effect.getColors() != null && !effect.getColors().isEmpty()) {
            sb.append("color:");
            boolean first = true;
            for (final Color c : effect.getColors()) {
                if (!first) {
                    sb.append(","); // same thing as above.
                }
                sb.append("#").append(Integer.toHexString(c.asRGB()));
                first = false;
            }
            sb.append(" ");
        }

        sb.append("shape:").append(effect.getType().name()).append(" ");
        if (effect.getFadeColors() != null && !effect.getFadeColors().isEmpty()) {
            sb.append("fade:");
            boolean first = true;
            for (final Color c : effect.getFadeColors()) {
                if (!first) {
                    sb.append(","); // same thing as above.
                }
                sb.append("#").append(Integer.toHexString(c.asRGB()));
                first = false;
            }
            sb.append(" ");
        }
    }

    private String serializeLines(Iterable<String> lines) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String line : lines) {
            // Add | before the line if it's not the first one. Easy but weird way
            // to do this since we need each line separated by |
            if (!first) {
                sb.append("|");
            }
            first = false;
            sb.append(FormatUtil.unformatString(line).replace(" ", "_").replace("|", "\\|"));
        }
        return sb.toString();
    }

    @Override
    public boolean isReady() {
        return ready;
    }
}
