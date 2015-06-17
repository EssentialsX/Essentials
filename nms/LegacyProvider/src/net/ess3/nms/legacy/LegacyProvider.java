package net.ess3.nms.legacy;

import net.ess3.nms.SpawnerProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LegacyProvider extends SpawnerProvider {
    @Override
    public ItemStack setEntityType(ItemStack is, EntityType type) {
        is.getData().setData((byte) type.getTypeId());
        ItemMeta meta = is.getItemMeta();
        String displayName;
        if (entityToDisplayName.containsKey(type)) {
            displayName = entityToDisplayName.get(type);
        } else {
            displayName = type.getName();
        }
        meta.setDisplayName(ChatColor.RESET + displayName + " Spawner");
        is.setItemMeta(meta);
        return is;
    }

    @Override
    public EntityType getEntityType(ItemStack is) {
        return EntityType.fromId((int) is.getData().getData());
    }

    @Override
    public String getHumanName() {
        return "legacy item data provider";
    }
}
