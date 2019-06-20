package com.earth2me.essentials.utils;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * <p>MaterialUtil class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class MaterialUtil {


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

    /** Constant <code>SPAWNER</code> */
    public static final Material SPAWNER = EnumUtil.getMaterial("MOB_SPAWNER", "SPAWNER");

    static {

        BEDS = EnumUtil.getAllMatching(Material.class, "BED", "WHITE_BED", "ORANGE_BED",
            "MAGENTA_BED", "LIGHT_BLUE_BED", "YELLOW_BED", "LIME_BED", "PINK_BED", "GRAY_BED",
            "LIGHT_GRAY_BED", "CYAN_BED", "PURPLE_BED", "BLUE_BED", "BROWN_BED", "GREEN_BED",
            "RED_BED", "BLACK_BED");

        BANNERS = EnumUtil.getAllMatching(Material.class, "BANNER", "WHITE_BANNER",
            "ORANGE_BANNER", "MAGENTA_BANNER", "LIGHT_BLUE_BANNER", "YELLOW_BANNER", "LIME_BANNER",
            "PINK_BANNER","GRAY_BANNER","LIGHT_GRAY_BANNER", "CYAN_BANNER", "PURPLE_BANNER",
            "BLUE_BANNER", "BROWN_BANNER", "GREEN_BANNER", "RED_BANNER", "BLACK_BANNER", "SHIELD");

        FIREWORKS = EnumUtil.getAllMatching(Material.class, "FIREWORK", "FIREWORK_ROCKET",
            "FIREWORK_CHARGE", "FIREWORK_STAR");

        LEATHER_ARMOR = EnumUtil.getAllMatching(Material.class, "LEATHER_HELMET",
            "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS");

        LEGACY_SKULLS = EnumUtil.getAllMatching(Material.class,"SKULL", "SKULL_ITEM");

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
            "OAK_SIGN", "SPRUCE_SIGN");

        WALL_SIGNS = EnumUtil.getAllMatching(Material.class, "WALL_SIGN",
            "ACACIA_WALL_SIGN", "BIRCH_WALL_SIGN", "DARK_OAK_WALL_SIGN", "JUNGLE_WALL_SIGN",
            "OAK_WALL_SIGN", "SPRUCE_WALL_SIGN");
    }

    /**
     * <p>isBed.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    public static boolean isBed(Material material) {
        return BEDS.contains(material);
    }

    /**
     * <p>isBanner.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    public static boolean isBanner(Material material) {
        return BANNERS.contains(material);
    }

    /**
     * <p>isFirework.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    public static boolean isFirework(Material material) {
        return FIREWORKS.contains(material);
    }

    /**
     * <p>isLeatherArmor.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    public static boolean isLeatherArmor(Material material) {
        return LEATHER_ARMOR.contains(material);
    }

    /**
     * <p>isMobHead.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @param durability a int.
     * @return a boolean.
     */
    public static boolean isMobHead(Material material, int durability) {
        if (MOB_HEADS.contains(material)) {
            return true;
        }

        return LEGACY_SKULLS.contains(material) && (durability < 0 || durability != 3);
    }

    /**
     * <p>isPlayerHead.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @param durability a int.
     * @return a boolean.
     */
    public static boolean isPlayerHead(Material material, int durability) {
        if (PLAYER_HEADS.contains(material)) {
            return true;
        }

        return LEGACY_SKULLS.contains(material) && durability == 3;
    }

    /**
     * <p>isPotion.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    public static boolean isPotion(Material material) {
        return POTIONS.contains(material);
    }

    /**
     * <p>isSignPost.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    public static boolean isSignPost(Material material) {
        return SIGN_POSTS.contains(material);
    }

    /**
     * <p>isWallSign.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    public static boolean isWallSign(Material material) {
        return WALL_SIGNS.contains(material);
    }

    /**
     * <p>isSign.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    public static boolean isSign(Material material) {
        return isSignPost(material) || isWallSign(material);
    }

    /**
     * <p>isSkull.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    public static boolean isSkull(Material material) {
        return isPlayerHead(material, -1) || isMobHead(material, -1);
    }

    /**
     * <p>convertFromLegacy.</p>
     *
     * @param id a int.
     * @param damage a byte.
     * @return a {@link org.bukkit.Material} object.
     */
    public static Material convertFromLegacy(int id, byte damage) {
        for (Material material : EnumSet.allOf(Material.class)) {
            if (material.getId() == id) {
                try {
                    return Bukkit.getUnsafe().fromLegacy(new MaterialData(material, damage));
                } catch (NoSuchMethodError error) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * <p>getColorOf.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a {@link org.bukkit.DyeColor} object.
     */
    public static DyeColor getColorOf(Material material) {
        for (DyeColor color : DyeColor.values()) {
            if (material.toString().contains(color.name())) {
                return color;
            }
        }

        return DyeColor.WHITE;
    }
}
