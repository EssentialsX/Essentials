package com.earth2me.essentials;

import com.earth2me.essentials.textreader.BookInput;
import com.earth2me.essentials.textreader.BookPager;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.base.Joiner;
import net.ess3.api.IEssentials;
import net.ess3.api.TranslatableException;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.PatternType;
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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class MetaItemStack {
    private static final Map<String, DyeColor> colorMap = new HashMap<>();
    private static final Map<String, FireworkEffect.Type> fireworkShape = new HashMap<>();
    private static boolean useNewSkullMethod = true;

    static {
        for (final DyeColor color : DyeColor.values()) {
            colorMap.put(color.name(), color);
        }
        for (final FireworkEffect.Type type : FireworkEffect.Type.values()) {
            fireworkShape.put(type.name(), type);
        }
    }

    private static final transient Pattern splitPattern = Pattern.compile("[:+',;.]");
    private static final transient Pattern hexPattern = Pattern.compile("#([0-9a-fA-F]{6})");
    private ItemStack stack;
    private FireworkEffect.Builder builder = FireworkEffect.builder();
    private PotionEffectType pEffectType;
    private PotionEffect pEffect;
    private boolean validFirework = false;
    private boolean validFireworkCharge = false;
    private boolean validPotionEffect = false;
    private boolean validPotionDuration = false;
    private boolean validPotionPower = false;
    private boolean isSplashPotion = false;
    private boolean completePotion = false;
    private int power = 1;
    private int duration = 120;

    public MetaItemStack(final ItemStack stack) {
        this.stack = stack.clone();
    }

    private static void setSkullOwner(final IEssentials ess, final ItemStack stack, final String owner) {
        if (!(stack.getItemMeta() instanceof SkullMeta)) return;

        final SkullMeta meta = (SkullMeta) stack.getItemMeta();
        if (useNewSkullMethod) {
            try {
                meta.setOwningPlayer(ess.getServer().getOfflinePlayer(owner));
                stack.setItemMeta(meta);
                return;
            } catch (final NoSuchMethodError e) {
                useNewSkullMethod = false;
            }
        }

        meta.setOwner(owner);
        stack.setItemMeta(meta);
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public boolean isValidFirework() {
        return validFirework;
    }

    public boolean isValidPotion() {
        return validPotionEffect && validPotionDuration && validPotionPower;
    }

    public FireworkEffect.Builder getFireworkBuilder() {
        return builder;
    }

    public PotionEffect getPotionEffect() {
        return pEffect;
    }

    public boolean completePotion() {
        return completePotion;
    }

    private void resetPotionMeta() {
        pEffect = null;
        pEffectType = null;
        validPotionEffect = false;
        validPotionDuration = false;
        validPotionPower = false;
        isSplashPotion = false;
        completePotion = true;
    }

    public boolean canSpawn(final IEssentials ess) {
        if (VersionUtil.PRE_FLATTENING) {
            try {
                ess.getServer().getUnsafe().modifyItemStack(stack.clone(), "{}");
                return true;
            } catch (final NoSuchMethodError nsme) {
                return true;
            } catch (final Throwable npe) {
                if (ess.getSettings().isDebug()) {
                    ess.getLogger().log(Level.INFO, "Itemstack is invalid", npe);
                }
                return false;
            }
        }
        return stack.getType().isItem();
    }

    public void parseStringMeta(final CommandSource sender, final boolean allowUnsafe, final String[] string, final int fromArg, final IEssentials ess) throws Exception {
        final boolean nbtIsKill = VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_20_6_R01);

        if (string[fromArg].startsWith("{") && hasMetaPermission(sender, "vanilla", false, true, ess)) {
            if (nbtIsKill) {
                throw new TranslatableException("noMetaNbtKill");
            }

            try {
                stack = ess.getServer().getUnsafe().modifyItemStack(stack, Joiner.on(' ').join(Arrays.asList(string).subList(fromArg, string.length)));
            } catch (final NullPointerException npe) {
                if (ess.getSettings().isDebug()) {
                    ess.getLogger().log(Level.INFO, "Itemstack is invalid", npe);
                }
            } catch (final NoSuchMethodError nsme) {
                throw new TranslatableException(nsme, "noMetaJson");
            } catch (final Throwable throwable) {
                throw new Exception(throwable.getMessage(), throwable);
            }
        } else if (string[fromArg].startsWith("[") && hasMetaPermission(sender, "vanilla", false, true, ess)) {
            if (!nbtIsKill) {
                throw new TranslatableException("noMetaComponents");
            }

            try {
                final String components = Joiner.on(' ').join(Arrays.asList(string).subList(fromArg, string.length));
                // modifyItemStack requires that the item namespaced key is prepended to the components for some reason
                stack = ess.getServer().getUnsafe().modifyItemStack(stack, stack.getType().getKey() + components);
            } catch (final NullPointerException npe) {
                if (ess.getSettings().isDebug()) {
                    ess.getLogger().log(Level.INFO, "Itemstack is invalid", npe);
                }
            } catch (final Throwable throwable) {
                throw new Exception(throwable.getMessage(), throwable);
            }
        } else {
            for (int i = fromArg; i < string.length; i++) {
                addStringMeta(sender, allowUnsafe, string[i], ess);
            }
            if (validFirework) {
                if (!hasMetaPermission(sender, "firework", true, true, ess)) {
                    throw new TranslatableException("noMetaFirework");
                }
                final FireworkEffect effect = builder.build();
                final FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
                fmeta.addEffect(effect);
                if (fmeta.getEffects().size() > 1 && !hasMetaPermission(sender, "firework-multiple", true, true, ess)) {
                    throw new TranslatableException("multipleCharges");
                }
                stack.setItemMeta(fmeta);
            }
            if (validFireworkCharge) {
                if (!hasMetaPermission(sender, "firework", true, true, ess)) {
                    throw new TranslatableException("noMetaFirework");
                }
                final FireworkEffect effect = builder.build();
                final FireworkEffectMeta meta = (FireworkEffectMeta) stack.getItemMeta();
                meta.setEffect(effect);
                stack.setItemMeta(meta);
            }
        }
    }

    public void addStringMeta(final CommandSource sender, final boolean allowUnsafe, final String string, final IEssentials ess) throws Exception {
        final String[] split = splitPattern.split(string, 2);
        if (split.length < 1) {
            return;
        }

        final Material WRITTEN_BOOK = EnumUtil.getMaterial("WRITTEN_BOOK");

        if (split.length > 1 && split[0].equalsIgnoreCase("name") && hasMetaPermission(sender, "name", false, true, ess)) {
            final String displayName = FormatUtil.replaceFormat(split[1].replaceAll("(?<!\\\\)_", " ").replace("\\_", "_"));
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(displayName);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && (split[0].equalsIgnoreCase("lore") || split[0].equalsIgnoreCase("desc")) && hasMetaPermission(sender, "lore", false, true, ess)) {
            final List<String> lore = new ArrayList<>();
            for (final String line : split[1].split("(?<!\\\\)\\|")) {
                lore.add(FormatUtil.replaceFormat(line.replaceAll("(?<!\\\\)_", " ").replace("\\_", "_").replace("\\|", "|")));
            }
            final ItemMeta meta = stack.getItemMeta();
            meta.setLore(lore);
            stack.setItemMeta(meta);
        } else if ((split[0].equalsIgnoreCase("custom-model-data") || split[0].equalsIgnoreCase("cmd")) && hasMetaPermission(sender, "custom-model-data", false, true, ess)) {
            if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_14_R01)) {
                final int value = split.length <= 1 ? 0 : Integer.parseInt(split[1]);
                final ItemMeta meta = stack.getItemMeta();
                meta.setCustomModelData(value);
                stack.setItemMeta(meta);
            }
        } else if (split[0].equalsIgnoreCase("unbreakable") && hasMetaPermission(sender, "unbreakable", false, true, ess)) {
            final boolean value = split.length <= 1 || Boolean.parseBoolean(split[1]);
            setUnbreakable(ess, stack, value);
        } else if (split.length > 1 && (split[0].equalsIgnoreCase("player") || split[0].equalsIgnoreCase("owner")) && hasMetaPermission(sender, "head", false, true, ess)) {
            if (MaterialUtil.isPlayerHead(stack)) {
                final String owner = split[1];
                setSkullOwner(ess, stack, owner);
            } else {
                throw new TranslatableException("onlyPlayerSkulls");
            }
        } else if (split.length > 1 && split[0].equalsIgnoreCase("book") && MaterialUtil.isEditableBook(stack.getType()) && (hasMetaPermission(sender, "book", true, true, ess) || hasMetaPermission(sender, "chapter-" + split[1].toLowerCase(Locale.ENGLISH), true, true, ess))) {
            final BookMeta meta = (BookMeta) stack.getItemMeta();
            final IText input = new BookInput("book", true, ess);
            final BookPager pager = new BookPager(input);
            // This fix only applies to written books - which require an author and a title. https://bugs.mojang.com/browse/MC-59153
            if (stack.getType() == WRITTEN_BOOK) {
                if (!meta.hasAuthor()) {
                    // The sender can be null when this method is called from {@link  com.earth2me.essentials.signs.EssentialsSign#getItemMeta(ItemStack, String, IEssentials)}
                    meta.setAuthor(sender == null ? Console.getInstance().getDisplayName() : sender.getPlayer().getName());
                }
                if (!meta.hasTitle()) {
                    final String title = FormatUtil.replaceFormat(split[1].replace('_', ' '));
                    meta.setTitle(title.length() > 32 ? title.substring(0, 32) : title);
                }
            }
            final List<String> pages = pager.getPages(split[1]);
            meta.setPages(pages);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && split[0].equalsIgnoreCase("author") && stack.getType() == WRITTEN_BOOK && hasMetaPermission(sender, "author", false, true, ess)) {
            final String author = FormatUtil.replaceFormat(split[1]);
            final BookMeta meta = (BookMeta) stack.getItemMeta();
            meta.setAuthor(author);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && split[0].equalsIgnoreCase("title") && stack.getType() == WRITTEN_BOOK && hasMetaPermission(sender, "title", false, true, ess)) {
            final String title = FormatUtil.replaceFormat(split[1].replaceAll("(?<!\\\\)_", " ").replace("\\_", "_"));
            final BookMeta meta = (BookMeta) stack.getItemMeta();
            meta.setTitle(title);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && split[0].startsWith("page") && split[0].length() > 4 && MaterialUtil.isEditableBook(stack.getType()) && hasMetaPermission(sender, "page", false, true, ess)) {
            final int page = NumberUtil.isInt(split[0].substring(4)) ? (Integer.parseInt(split[0].substring(4)) - 1) : 0;
            final BookMeta meta = (BookMeta) stack.getItemMeta();
            final List<String> pages = meta.hasPages() ? new ArrayList<>(meta.getPages()) : new ArrayList<>();
            final List<String> lines = new ArrayList<>();
            for (final String line : split[1].split("(?<!\\\\)\\|")) {
                lines.add(FormatUtil.replaceFormat(line.replaceAll("(?<!\\\\)_", " ").replace("\\_", "_").replace("\\|", "|")));
            }
            final String content = String.join("\n", lines);
            if (page >= pages.size()) {
                for (int i = 0; i <= page - pages.size(); i++) {
                    pages.add("");
                }
            }
            pages.set(page, content);
            meta.setPages(pages);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && split[0].equalsIgnoreCase("power") && MaterialUtil.isFirework(stack.getType()) && hasMetaPermission(sender, "firework-power", false, true, ess)) {
            final int power = NumberUtil.isInt(split[1]) ? Integer.parseInt(split[1]) : 0;
            final FireworkMeta meta = (FireworkMeta) stack.getItemMeta();
            meta.setPower(power > 3 ? 4 : power);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && split[0].equalsIgnoreCase("itemflags") && hasMetaPermission(sender, "itemflags", false, true, ess)) {
            addItemFlags(string);
        } else if (MaterialUtil.isFirework(stack.getType())) {
            if (!parseEnchantmentStrings(sender, allowUnsafe, split, ess)) {
                //WARNING - Meta for fireworks will be ignored after this point.
                addFireworkMeta(sender, false, string, ess);
            }
        } else if (MaterialUtil.isFireworkCharge(stack.getType())) {
            if (!parseEnchantmentStrings(sender, allowUnsafe, split, ess)) {
                //WARNING - Meta for fireworks will be ignored after this point.
                addChargeMeta(sender, false, string, ess);
            }
        } else if (MaterialUtil.isPotion(stack.getType())) {
            if (split[0].equalsIgnoreCase("power") || !parseEnchantmentStrings(sender, allowUnsafe, split, ess)) {
                //WARNING - Meta for potions will be ignored after this point.
                addPotionMeta(sender, false, string, ess);
            }
        } else if (MaterialUtil.isBanner(stack.getType())) {
            if (!parseEnchantmentStrings(sender, allowUnsafe, split, ess)) {
                //WARNING - Meta for banners will be ignored after this point.
                addBannerMeta(sender, false, string, ess);
            }
        } else if (split.length > 1 && (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour")) && MaterialUtil.isLeatherArmor(stack.getType())) {
            final String[] color = split[1].split("[|,]");
            if (color.length == 1 && (NumberUtil.isInt(color[0]) || color[0].startsWith("#"))) {
                // Either integer or hexadecimal
                final LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
                final String input = color[0];
                if (input.startsWith("#")) { // Hex
                    meta.setColor(Color.fromRGB(
                        Integer.valueOf(input.substring(1, 3), 16),
                        Integer.valueOf(input.substring(3, 5), 16),
                        Integer.valueOf(input.substring(5, 7), 16)));
                } else { // Int
                    meta.setColor(Color.fromRGB(Integer.parseInt(input)));
                }
                stack.setItemMeta(meta);
            } else if (color.length == 3) { // r,g,b
                final int red = NumberUtil.isInt(color[0]) ? Integer.parseInt(color[0]) : 0;
                final int green = NumberUtil.isInt(color[1]) ? Integer.parseInt(color[1]) : 0;
                final int blue = NumberUtil.isInt(color[2]) ? Integer.parseInt(color[2]) : 0;
                final LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
                meta.setColor(Color.fromRGB(red, green, blue));
                stack.setItemMeta(meta);
            } else {
                throw new TranslatableException("leatherSyntax");
            }
        } else {
            parseEnchantmentStrings(sender, allowUnsafe, split, ess);
        }
    }

    public void addItemFlags(final String string) throws Exception {
        final String[] separate = splitPattern.split(string, 2);
        if (separate.length != 2) {
            throw new TranslatableException("invalidItemFlagMeta", string);
        }

        final String[] split = separate[1].split(",");
        final ItemMeta meta = stack.getItemMeta();

        for (final String s : split) {
            for (final ItemFlag flag : ItemFlag.values()) {
                if (s.equalsIgnoreCase(flag.name())) {
                    meta.addItemFlags(flag);
                }
            }
        }

        if (meta.getItemFlags().isEmpty()) {
            throw new TranslatableException("invalidItemFlagMeta", string);
        }

        stack.setItemMeta(meta);
    }

    private void addChargeMeta(final CommandSource sender, final boolean allowShortName, final String string, final IEssentials ess) throws Exception {
        final String[] split = splitPattern.split(string, 2);
        if (split.length < 2) {
            return;
        }

        if (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour") || (allowShortName && split[0].equalsIgnoreCase("c"))) {
            final List<Color> primaryColors = new ArrayList<>();
            final String[] colors = split[1].split(",");
            for (final String color : colors) {
                if (colorMap.containsKey(color.toUpperCase())) {
                    validFireworkCharge = true;
                    primaryColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
                } else if (hexPattern.matcher(color).matches()) {
                    validFireworkCharge = true;
                    primaryColors.add(Color.fromRGB(Integer.decode(color)));
                } else {
                    throw new TranslatableException("invalidFireworkFormat", split[1], split[0]);
                }
            }
            builder.withColor(primaryColors);
        } else if (split[0].equalsIgnoreCase("shape") || split[0].equalsIgnoreCase("type") || (allowShortName && (split[0].equalsIgnoreCase("s") || split[0].equalsIgnoreCase("t")))) {
            FireworkEffect.Type finalEffect = null;
            split[1] = split[1].equalsIgnoreCase("large") ? "BALL_LARGE" : split[1];
            if (fireworkShape.containsKey(split[1].toUpperCase())) {
                finalEffect = fireworkShape.get(split[1].toUpperCase());
            } else {
                throw new TranslatableException("invalidFireworkFormat", split[1], split[0]);
            }
            if (finalEffect != null) {
                builder.with(finalEffect);
            }
        } else if (split[0].equalsIgnoreCase("fade") || (allowShortName && split[0].equalsIgnoreCase("f"))) {
            final List<Color> fadeColors = new ArrayList<>();
            final String[] colors = split[1].split(",");
            for (final String color : colors) {
                if (colorMap.containsKey(color.toUpperCase())) {
                    fadeColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
                } else if (hexPattern.matcher(color).matches()) {
                    fadeColors.add(Color.fromRGB(Integer.decode(color)));
                } else {
                    throw new TranslatableException("invalidFireworkFormat", split[1], split[0]);
                }
            }
            if (!fadeColors.isEmpty()) {
                builder.withFade(fadeColors);
            }
        } else if (split[0].equalsIgnoreCase("effect") || (allowShortName && split[0].equalsIgnoreCase("e"))) {
            final String[] effects = split[1].split(",");
            for (final String effect : effects) {
                if (effect.equalsIgnoreCase("twinkle")) {
                    builder.flicker(true);
                } else if (effect.equalsIgnoreCase("trail")) {
                    builder.trail(true);
                } else {
                    throw new TranslatableException("invalidFireworkFormat", split[1], split[0]);
                }
            }
        }
    }

    public void addFireworkMeta(final CommandSource sender, final boolean allowShortName, final String string, final IEssentials ess) throws Exception {
        if (MaterialUtil.isFirework(stack.getType())) {
            final String[] split = splitPattern.split(string, 2);
            if (split.length < 2) {
                return;
            }

            if (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour") || (allowShortName && split[0].equalsIgnoreCase("c"))) {
                if (validFirework) {
                    if (!hasMetaPermission(sender, "firework", true, true, ess)) {
                        throw new TranslatableException("noMetaFirework");
                    }
                    final FireworkEffect effect = builder.build();
                    final FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
                    fmeta.addEffect(effect);
                    if (fmeta.getEffects().size() > 1 && !hasMetaPermission(sender, "firework-multiple", true, true, ess)) {
                        throw new TranslatableException("multipleCharges");
                    }
                    stack.setItemMeta(fmeta);
                    builder = FireworkEffect.builder();
                }

                final List<Color> primaryColors = new ArrayList<>();
                final String[] colors = split[1].split(",");
                for (final String color : colors) {
                    if (colorMap.containsKey(color.toUpperCase())) {
                        validFirework = true;
                        primaryColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
                    } else if (hexPattern.matcher(color).matches()) {
                        validFirework = true;
                        primaryColors.add(Color.fromRGB(Integer.decode(color)));
                    } else {
                        throw new TranslatableException("invalidFireworkFormat", split[1], split[0]);
                    }
                }
                builder.withColor(primaryColors);
            } else if (split[0].equalsIgnoreCase("shape") || split[0].equalsIgnoreCase("type") || (allowShortName && (split[0].equalsIgnoreCase("s") || split[0].equalsIgnoreCase("t")))) {
                FireworkEffect.Type finalEffect = null;
                split[1] = split[1].equalsIgnoreCase("large") ? "BALL_LARGE" : split[1];
                if (fireworkShape.containsKey(split[1].toUpperCase())) {
                    finalEffect = fireworkShape.get(split[1].toUpperCase());
                } else {
                    throw new TranslatableException("invalidFireworkFormat", split[1], split[0]);
                }
                if (finalEffect != null) {
                    builder.with(finalEffect);
                }
            } else if (split[0].equalsIgnoreCase("fade") || (allowShortName && split[0].equalsIgnoreCase("f"))) {
                final List<Color> fadeColors = new ArrayList<>();
                final String[] colors = split[1].split(",");
                for (final String color : colors) {
                    if (colorMap.containsKey(color.toUpperCase())) {
                        fadeColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
                    } else if (hexPattern.matcher(color).matches()) {
                        fadeColors.add(Color.fromRGB(Integer.decode(color)));
                    } else {
                        throw new TranslatableException("invalidFireworkFormat", split[1], split[0]);
                    }
                }
                if (!fadeColors.isEmpty()) {
                    builder.withFade(fadeColors);
                }
            } else if (split[0].equalsIgnoreCase("effect") || (allowShortName && split[0].equalsIgnoreCase("e"))) {
                final String[] effects = split[1].split(",");
                for (final String effect : effects) {
                    if (effect.equalsIgnoreCase("twinkle")) {
                        builder.flicker(true);
                    } else if (effect.equalsIgnoreCase("trail")) {
                        builder.trail(true);
                    } else {
                        throw new TranslatableException("invalidFireworkFormat", split[1], split[0]);
                    }
                }
            }
        }
    }

    public void addPotionMeta(final CommandSource sender, final boolean allowShortName, final String string, final IEssentials ess) throws Exception {
        if (MaterialUtil.isPotion(stack.getType())) {
            final String[] split = splitPattern.split(string, 2);

            if (split.length < 2) {
                return;
            }

            if (split[0].equalsIgnoreCase("effect") || (allowShortName && split[0].equalsIgnoreCase("e"))) {
                pEffectType = Potions.getByName(split[1]);
                if (pEffectType != null && pEffectType.getName() != null) {
                    if (hasMetaPermission(sender, "potions." + pEffectType.getName().toLowerCase(Locale.ENGLISH), true, false, ess)) {
                        validPotionEffect = true;
                    } else {
                        throw new TranslatableException("noPotionEffectPerm", pEffectType.getName().toLowerCase(Locale.ENGLISH));
                    }
                } else {
                    throw new TranslatableException("invalidPotionMeta", split[1]);
                }
            } else if (split[0].equalsIgnoreCase("power") || (allowShortName && split[0].equalsIgnoreCase("p"))) {
                if (NumberUtil.isInt(split[1])) {
                    validPotionPower = true;
                    power = Integer.parseInt(split[1]);
                    if (power > 0 && power < 4) {
                        power -= 1;
                    }
                } else {
                    throw new TranslatableException("invalidPotionMeta", split[1]);
                }
            } else if (split[0].equalsIgnoreCase("amplifier") || (allowShortName && split[0].equalsIgnoreCase("a"))) {
                if (NumberUtil.isInt(split[1])) {
                    validPotionPower = true;
                    power = Integer.parseInt(split[1]);
                } else {
                    throw new TranslatableException("invalidPotionMeta", split[1]);
                }
            } else if (split[0].equalsIgnoreCase("duration") || (allowShortName && split[0].equalsIgnoreCase("d"))) {
                if (NumberUtil.isInt(split[1])) {
                    validPotionDuration = true;
                    duration = Integer.parseInt(split[1]) * 20; //Duration is in ticks by default, converted to seconds
                } else {
                    throw new TranslatableException("invalidPotionMeta", split[1]);
                }
            } else if (split[0].equalsIgnoreCase("splash") || (allowShortName && split[0].equalsIgnoreCase("s"))) {
                isSplashPotion = Boolean.parseBoolean(split[1]);
            }

            if (isValidPotion()) {
                final PotionMeta pmeta = (PotionMeta) stack.getItemMeta();
                pEffect = pEffectType.createEffect(duration, power);
                if (pmeta.getCustomEffects().size() > 1 && !hasMetaPermission(sender, "potions.multiple", true, false, ess)) {
                    throw new TranslatableException("multiplePotionEffects");
                }
                pmeta.addCustomEffect(pEffect, true);
                stack.setItemMeta(pmeta);
                ess.getPotionMetaProvider().setSplashPotion(stack, isSplashPotion);
                resetPotionMeta();
            }
        }
    }

    private boolean parseEnchantmentStrings(final CommandSource sender, final boolean allowUnsafe, final String[] split, final IEssentials ess) throws Exception {
        final Enchantment enchantment = Enchantments.getByName(split[0]);
        if (enchantment == null) {
            return false;
        }
        if (hasMetaPermission(sender, "enchantments." + Enchantments.getRealName(enchantment), false, false, ess)) {
            int level = -1;
            if (split.length > 1) {
                try {
                    level = Integer.parseInt(split[1]);
                } catch (final NumberFormatException ex) {
                    level = -1;
                }
            }

            if (level < 0 || (!allowUnsafe && level > enchantment.getMaxLevel())) {
                level = enchantment.getMaxLevel();
            }
            addEnchantment(sender, allowUnsafe, enchantment, level);
        }
        return true;
    }

    public void addEnchantment(final CommandSource sender, final boolean allowUnsafe, final Enchantment enchantment, final int level) throws Exception {
        if (enchantment == null) {
            throw new TranslatableException("enchantmentNotFound");
        }
        try {
            if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
                final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
                if (level == 0) {
                    meta.removeStoredEnchant(enchantment);
                } else {
                    meta.addStoredEnchant(enchantment, level, allowUnsafe);
                }
                stack.setItemMeta(meta);
            } else { // all other material types besides ENCHANTED_BOOK
                if (level == 0) {
                    stack.removeEnchantment(enchantment);
                } else {
                    if (allowUnsafe) {
                        stack.addUnsafeEnchantment(enchantment, level);
                    } else {
                        stack.addEnchantment(enchantment, level);
                    }
                }
            }
        } catch (final Exception ex) {
            throw new Exception("Enchantment " + Enchantments.getRealName(enchantment) + ": " + ex.getMessage(), ex);
        }
    }

    public Enchantment getEnchantment(final User user, final String name) throws Exception {
        final Enchantment enchantment = Enchantments.getByName(name);
        if (enchantment == null) {
            return null;
        }

        final String enchantmentName = Enchantments.getRealName(enchantment);

        if (!hasMetaPermission(user, "enchantments." + enchantmentName, true, false)) {
            throw new TranslatableException("enchantmentPerm", enchantmentName);
        }
        return enchantment;
    }

    public void addBannerMeta(final CommandSource sender, final boolean allowShortName, final String string, final IEssentials ess) throws Exception {
        if (MaterialUtil.isBanner(stack.getType()) && !stack.getType().toString().equals("SHIELD") && string != null) {
            final String[] split = splitPattern.split(string, 2);

            if (split.length < 2) {
                throw new TranslatableException("invalidBanner", split[1]);
            }

            PatternType patternType = null;
            try {
                //noinspection removal
                patternType = PatternType.getByIdentifier(split[0]);
            } catch (final Exception ignored) {
            }

            final BannerMeta meta = (BannerMeta) stack.getItemMeta();
            if (split[0].equalsIgnoreCase("basecolor")) {
                final Color color = Color.fromRGB(Integer.parseInt(split[1]));
                ess.getBannerDataProvider().setBaseColor(stack, DyeColor.getByColor(color));
            } else if (patternType != null) {
                //noinspection removal
                final PatternType type = PatternType.getByIdentifier(split[0]);
                final DyeColor color = DyeColor.getByColor(Color.fromRGB(Integer.parseInt(split[1])));
                final org.bukkit.block.banner.Pattern pattern = new org.bukkit.block.banner.Pattern(color, type);
                meta.addPattern(pattern);
            }

            stack.setItemMeta(meta);
        } else if (stack.getType().toString().equals("SHIELD") && string != null) {
            final String[] split = splitPattern.split(string, 2);

            if (split.length < 2) {
                throw new TranslatableException("invalidBanner", split[1]);
            }

            PatternType patternType = null;
            try {
                //noinspection removal
                patternType = PatternType.getByIdentifier(split[0]);
            } catch (final Exception ignored) {
            }

            // Hacky fix for accessing Shield meta - https://github.com/drtshock/Essentials/pull/745#issuecomment-234843795
            final BlockStateMeta meta = (BlockStateMeta) stack.getItemMeta();
            final Banner banner = (Banner) meta.getBlockState();
            if (split[0].equalsIgnoreCase("basecolor")) {
                final Color color = Color.fromRGB(Integer.parseInt(split[1]));
                banner.setBaseColor(DyeColor.getByColor(color));
            } else if (patternType != null) {
                //noinspection removal
                final PatternType type = PatternType.getByIdentifier(split[0]);
                final DyeColor color = DyeColor.getByColor(Color.fromRGB(Integer.parseInt(split[1])));
                final org.bukkit.block.banner.Pattern pattern = new org.bukkit.block.banner.Pattern(color, type);
                banner.addPattern(pattern);
            }
            banner.update();
            meta.setBlockState(banner);
            stack.setItemMeta(meta);
        }
    }

    private boolean hasMetaPermission(final CommandSource sender, final String metaPerm, final boolean graceful, final boolean includeBase, final IEssentials ess) throws Exception {
        final User user = sender != null && sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;
        return hasMetaPermission(user, metaPerm, graceful, includeBase);
    }

    private boolean hasMetaPermission(final User user, final String metaPerm, final boolean graceful, final boolean includeBase) throws Exception {
        final String permBase = includeBase ? "essentials.itemspawn.meta-" : "essentials.";
        if (user == null || user.isAuthorized(permBase + metaPerm)) {
            return true;
        }

        if (graceful) {
            return false;
        } else {
            throw new TranslatableException("noMetaPerm", metaPerm);
        }
    }

    private void setUnbreakable(final IEssentials ess, final ItemStack is, final boolean unbreakable) {
        final ItemMeta meta = is.getItemMeta();
        ess.getItemUnbreakableProvider().setUnbreakable(meta, unbreakable);
        is.setItemMeta(meta);
    }
}
