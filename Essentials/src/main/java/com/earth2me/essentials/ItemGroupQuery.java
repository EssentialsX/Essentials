package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.Material;

public class ItemGroupQuery {

    private final String itemGroup;
    private final int amount;

    public ItemGroupQuery(String group, int quantity) {
        this.itemGroup = group;
        this.amount = quantity;
    }

    public boolean contains(IEssentials ess, Material item){
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
