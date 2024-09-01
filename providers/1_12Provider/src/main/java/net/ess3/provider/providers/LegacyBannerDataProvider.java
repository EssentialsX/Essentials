package net.ess3.provider.providers;

import net.ess3.provider.BannerDataProvider;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class LegacyBannerDataProvider implements BannerDataProvider {
    @Override
    public DyeColor getBaseColor(ItemStack stack) {
        final BannerMeta bannerMeta = (BannerMeta) stack.getItemMeta();
        return bannerMeta.getBaseColor();
    }

    @Override
    public void setBaseColor(ItemStack stack, DyeColor color) {
        final BannerMeta bannerMeta = (BannerMeta) stack.getItemMeta();
        bannerMeta.setBaseColor(color);
        stack.setItemMeta(bannerMeta);
    }

    @Override
    public String getDescription() {
        return "Legacy Banner Meta Provider";
    }
}
