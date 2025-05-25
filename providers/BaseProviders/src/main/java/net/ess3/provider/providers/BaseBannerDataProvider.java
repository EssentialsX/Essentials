package net.ess3.provider.providers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.ess3.provider.BannerDataProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@ProviderData(description = "1.20.5+ Banner Data Provider", weight = 1)
public class BaseBannerDataProvider implements BannerDataProvider {
    private final BiMap<Material, DyeColor> materialToDyeMap = HashBiMap.create();

    public BaseBannerDataProvider() {
        materialToDyeMap.put(Material.WHITE_BANNER, DyeColor.WHITE);
        materialToDyeMap.put(Material.LIGHT_GRAY_BANNER, DyeColor.LIGHT_GRAY);
        materialToDyeMap.put(Material.GRAY_BANNER, DyeColor.GRAY);
        materialToDyeMap.put(Material.BLACK_BANNER, DyeColor.BLACK);
        materialToDyeMap.put(Material.RED_BANNER, DyeColor.RED);
        materialToDyeMap.put(Material.ORANGE_BANNER, DyeColor.ORANGE);
        materialToDyeMap.put(Material.YELLOW_BANNER, DyeColor.YELLOW);
        materialToDyeMap.put(Material.LIME_BANNER, DyeColor.LIME);
        materialToDyeMap.put(Material.GREEN_BANNER, DyeColor.GREEN);
        materialToDyeMap.put(Material.CYAN_BANNER, DyeColor.CYAN);
        materialToDyeMap.put(Material.LIGHT_BLUE_BANNER, DyeColor.LIGHT_BLUE);
        materialToDyeMap.put(Material.BLUE_BANNER, DyeColor.BLUE);
        materialToDyeMap.put(Material.PURPLE_BANNER, DyeColor.PURPLE);
        materialToDyeMap.put(Material.MAGENTA_BANNER, DyeColor.MAGENTA);
        materialToDyeMap.put(Material.PINK_BANNER, DyeColor.PINK);
        materialToDyeMap.put(Material.BROWN_BANNER, DyeColor.BROWN);
    }

    @Override
    public DyeColor getBaseColor(ItemStack stack) {
        return materialToDyeMap.get(stack.getType());
    }

    @Override
    public void setBaseColor(ItemStack stack, DyeColor color) {
        final Material material = materialToDyeMap.inverse().get(color);
        if (material != null) {
            stack.setType(material);
        }
    }

    @ProviderTest
    public static boolean test() {
        try {
            //noinspection unused
            final Material needAVariable = Material.LIGHT_BLUE_BANNER;
            return true;
        } catch (final Throwable t) {
            return false;
        }
    }
}
