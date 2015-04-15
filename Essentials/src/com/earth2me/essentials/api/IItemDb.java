package com.earth2me.essentials.api;

import com.earth2me.essentials.User;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public interface IItemDb {
    ItemStack get(final String name, final int quantity) throws Exception;

    ItemStack get(final String name) throws Exception;

    public String names(ItemStack item);

    public String name(ItemStack item);

    List<ItemStack> getMatching(User user, String[] args) throws Exception;
}
