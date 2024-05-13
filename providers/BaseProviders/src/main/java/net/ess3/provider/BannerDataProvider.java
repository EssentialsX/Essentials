package net.ess3.provider;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public interface BannerDataProvider extends Provider {
    DyeColor getBaseColor(ItemStack stack);

    void setBaseColor(ItemStack stack, DyeColor color);
}
