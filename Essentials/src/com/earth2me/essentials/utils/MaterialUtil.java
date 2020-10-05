package com.earth2me.essentials.utils;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

public final class MaterialUtil {

    public static final Material SPAWNER = EnumUtil.getMaterial("MOB_SPAWNER", "SPAWNER");
    private static final Set<Material> BEDS;
    private static final Set<Material> BANNERS;
    private static final Set<Material> FIREWORKS;
    private static final Set<Material> LEGACY_SKULLS;
    private static final Set<Material> LEATHER_ARMOR;
    private static final Set<Material> MOB_HEADS;
    // includes TIPPED_ARROW which also has potion effects
    private static final Set<Material> PLAYER_HEADS;
    private static final Set<Material> POTIONS;
    private static final Set<Material> SIGN_POSTS;
    private static final Set<Material> WALL_SIGNS;

    static {

        BEDS = EnumUtil.getAllMatching(Material.class, "BED", "BED_BLOCK", "WHITE_BED", "ORANGE_BED",
            "MAGENTA_BED", "LIGHT_BLUE_BED", "YELLOW_BED", "LIME_BED", "PINK_BED", "GRAY_BED",
            "LIGHT_GRAY_BED", "CYAN_BED", "PURPLE_BED", "BLUE_BED", "BROWN_BED", "GREEN_BED",
            "RED_BED", "BLACK_BED");

        BANNERS = EnumUtil.getAllMatching(Material.class, "BANNER", "WHITE_BANNER",
            "ORANGE_BANNER", "MAGENTA_BANNER", "LIGHT_BLUE_BANNER", "YELLOW_BANNER", "LIME_BANNER",
            "PINK_BANNER", "GRAY_BANNER", "LIGHT_GRAY_BANNER", "CYAN_BANNER", "PURPLE_BANNER",
            "BLUE_BANNER", "BROWN_BANNER", "GREEN_BANNER", "RED_BANNER", "BLACK_BANNER", "SHIELD");

        FIREWORKS = EnumUtil.getAllMatching(Material.class, "FIREWORK", "FIREWORK_ROCKET",
            "FIREWORK_CHARGE", "FIREWORK_STAR");

        LEATHER_ARMOR = EnumUtil.getAllMatching(Material.class, "LEATHER_HELMET",
            "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS");

        LEGACY_SKULLS = EnumUtil.getAllMatching(Material.class, "SKULL", "SKULL_ITEM");

        MOB_HEADS = EnumUtil.getAllMatching(Material.class, "SKELETON_SKULL",
            "SKELETON_WALL_SKULL", "WITHER_SKELETON_SKULL", "WITHER_SKELETON_WALL_SKULL",
            "CREEPER_HEAD", "CREEPER_WALL_HEAD", "ZOMBIE_HEAD", "ZOMBIE_WALL_HEAD", "DRAGON_HEAD"
            , "DRAGON_WALL_HEAD");

        PLAYER_HEADS = EnumUtil.getAllMatching(Material.class, "PLAYER_HEAD", "PLAYER_WALL_HEAD");

        POTIONS = EnumUtil.getAllMatching(Material.class, "POTION", "SPLASH_POTION",
            "LINGERING_POTION", "TIPPED_ARROW");

        SIGN_POSTS = EnumUtil.getAllMatching(Material.class, "SIGN", "SIGN_POST",
            "ACACIA_SIGN", "BIRCH_SIGN",
            "DARK_OAK_SIGN", "JUNGLE_SIGN",
            "OAK_SIGN", "SPRUCE_SIGN",
            "CRIMSON_SIGN", "WARPED_SIGN");

        WALL_SIGNS = EnumUtil.getAllMatching(Material.class, "WALL_SIGN",
            "ACACIA_WALL_SIGN", "BIRCH_WALL_SIGN",
            "DARK_OAK_WALL_SIGN", "JUNGLE_WALL_SIGN",
            "OAK_WALL_SIGN", "SPRUCE_WALL_SIGN",
            "CRIMSON_WALL_SIGN", "WARPED_WALL_SIGN");
    }

    private MaterialUtil() {
    }

    public static boolean isBed(final Material material) {
        return BEDS.contains(material);
    }

    public static boolean isBanner(final Material material) {
        return BANNERS.contains(material);
    }

    public static boolean isFirework(final Material material) {
        return FIREWORKS.contains(material);
    }

    public static boolean isLeatherArmor(final Material material) {
        return LEATHER_ARMOR.contains(material);
    }

    public static boolean isMobHead(final Material material, final int durability) {
        if (MOB_HEADS.contains(material)) {
            return true;
        }

        return LEGACY_SKULLS.contains(material) && (durability != 3);
    }

    public static boolean isPlayerHead(final Material material, final int durability) {
        if (PLAYER_HEADS.contains(material)) {
            return true;
        }

        return LEGACY_SKULLS.contains(material) && durability == 3;
    }

    public static boolean isPotion(final Material material) {
        return POTIONS.contains(material);
    }

    public static boolean isSignPost(final Material material) {
        return SIGN_POSTS.contains(material);
    }

    public static boolean isWallSign(final Material material) {
        return WALL_SIGNS.contains(material);
    }

    public static boolean isSign(final Material material) {
        return isSignPost(material) || isWallSign(material);
    }

    public static boolean isSkull(final Material material) {
        return isPlayerHead(material, -1) || isMobHead(material, -1);
    }

    public static Material convertFromLegacy(final int id, final byte damage) {
        for (final Material material : EnumSet.allOf(Material.class)) {
            if (material.getId() == id) {
                try {
                    return Bukkit.getUnsafe().fromLegacy(new MaterialData(material, damage));
                } catch (final NoSuchMethodError error) {
                    break;
                }
            }
        }

        try {
            //noinspection JavaReflectionMemberAccess
            final Method getMaterialFromId = Material.class.getDeclaredMethod("getMaterial", int.class);
            return (Material) getMaterialFromId.invoke(null, id);
        } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        return null;
    }

    public static DyeColor getColorOf(final Material material) {
        for (final DyeColor color : DyeColor.values()) {
            if (material.toString().contains(color.name())) {
                return color;
            }
        }

        return DyeColor.WHITE;
    }
}
