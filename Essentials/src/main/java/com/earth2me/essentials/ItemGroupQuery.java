package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

public class ItemGroupQuery {

    private final String itemGroup;
    private final int amount;

    public ItemGroupQuery(String group, int quantity) {
        this.itemGroup = group;
        this.amount = quantity;
    }

    public boolean contains(IEssentials ess, Material item){
        Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_ITEMS, NamespacedKey.minecraft(itemGroup), Material.class);
        if (tag != null && tag.isTagged(item)) {
            return true;
        }
        final ItemGroups groupsConfig = new ItemGroups(ess);
        return groupsConfig.getItemGroup(itemGroup).contains(item);
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "ItemGroupQuery{" +
                "itemGroup='" + itemGroup + '\'' +
                ", quantity=" + amount +
                '}';
    }
}
