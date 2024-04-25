package net.ess3.provider.providers;

import net.ess3.provider.BannerDataProvider;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class BaseBannerDataProvider implements BannerDataProvider {
    @Override
    public DyeColor getBaseColor(ItemStack stack) {
        final DyeColor base;
        switch (stack.getType()) {
            case WHITE_BANNER: {
                base = DyeColor.WHITE;
                break;
            }
            case LIGHT_GRAY_BANNER: {
                base = DyeColor.LIGHT_GRAY;
                break;
            }
            case GRAY_BANNER: {
                base = DyeColor.GRAY;
                break;
            }
            case BLACK_BANNER: {
                base = DyeColor.BLACK;
                break;
            }
            case RED_BANNER: {
                base = DyeColor.RED;
                break;
            }
            case ORANGE_BANNER: {
                base = DyeColor.ORANGE;
                break;
            }
            case YELLOW_BANNER: {
                base = DyeColor.YELLOW;
                break;
            }
            case LIME_BANNER: {
                base = DyeColor.LIME;
                break;
            }
            case GREEN_BANNER: {
                base = DyeColor.GREEN;
                break;
            }
            case CYAN_BANNER: {
                base = DyeColor.CYAN;
                break;
            }
            case LIGHT_BLUE_BANNER: {
                base = DyeColor.LIGHT_BLUE;
                break;
            }
            case BLUE_BANNER: {
                base = DyeColor.BLUE;
                break;
            }
            case PURPLE_BANNER: {
                base = DyeColor.PURPLE;
                break;
            }
            case MAGENTA_BANNER: {
                base = DyeColor.MAGENTA;
                break;
            }
            case PINK_BANNER: {
                base = DyeColor.PINK;
                break;
            }
            case BROWN_BANNER: {
                base = DyeColor.BROWN;
                break;
            }
            default: {
                base = null;
                break;
            }
        }
        return base;
    }

    @Override
    public void setBaseColor(ItemStack stack, DyeColor color) {
        switch (color) {
            case WHITE: {
                stack.setType(org.bukkit.Material.WHITE_BANNER);
                break;
            }
            case LIGHT_GRAY: {
                stack.setType(org.bukkit.Material.LIGHT_GRAY_BANNER);
                break;
            }
            case GRAY: {
                stack.setType(org.bukkit.Material.GRAY_BANNER);
                break;
            }
            case BLACK: {
                stack.setType(org.bukkit.Material.BLACK_BANNER);
                break;
            }
            case RED: {
                stack.setType(org.bukkit.Material.RED_BANNER);
                break;
            }
            case ORANGE: {
                stack.setType(org.bukkit.Material.ORANGE_BANNER);
                break;
            }
            case YELLOW: {
                stack.setType(org.bukkit.Material.YELLOW_BANNER);
                break;
            }
            case LIME: {
                stack.setType(org.bukkit.Material.LIME_BANNER);
                break;
            }
            case GREEN: {
                stack.setType(org.bukkit.Material.GREEN_BANNER);
                break;
            }
            case CYAN: {
                stack.setType(org.bukkit.Material.CYAN_BANNER);
                break;
            }
            case LIGHT_BLUE: {
                stack.setType(org.bukkit.Material.LIGHT_BLUE_BANNER);
                break;
            }
            case BLUE: {
                stack.setType(org.bukkit.Material.BLUE_BANNER);
                break;
            }
            case PINK: {
                stack.setType(org.bukkit.Material.PINK_BANNER);
                break;
            }
            case BROWN: {
                stack.setType(org.bukkit.Material.BROWN_BANNER);
                break;
            }
            case PURPLE: {
                stack.setType(org.bukkit.Material.PURPLE_BANNER);
                break;
            }
            case MAGENTA: {
                stack.setType(org.bukkit.Material.MAGENTA_BANNER);
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public String getDescription() {
        return "1.20.5+ Banner Data Provider.";
    }
}
