package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsTreeNode;
import com.earth2me.essentials.craftbukkit.Inventories;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemTestCommand extends EssentialsTreeNode {
    public ItemTestCommand() {
        super(new String[]{"itemtest"}, true);
    }

    @Override
    protected void run(final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (!sender.isAuthorized("essentials.itemtest") || args.length < 1 || !sender.isPlayer()) {
            return;
        }

        final Player player = sender.getPlayer();
        assert player != null;

        switch (args[0]) {
            case "slot": {
                if (args.length < 2) {
                    return;
                }
                player.getInventory().setItem(Integer.parseInt(args[1]), new ItemStack(Material.DIRT));
                break;
            }
            case "overfill": {
                sender.sendMessage(Inventories.addItem(player, 42, false, new ItemStack(Material.DIAMOND_SWORD, 1), new ItemStack(Material.DIRT, 32), new ItemStack(Material.DIRT, 32)).toString());
                break;
            }
            case "overfill2": {
                if (args.length < 3) {
                    return;
                }
                final boolean armor = Boolean.parseBoolean(args[1]);
                final boolean add = Boolean.parseBoolean(args[2]);
                final ItemStack[] items = new ItemStack[]{new ItemStack(Material.DIAMOND_SWORD, 1), new ItemStack(Material.DIRT, 32), new ItemStack(Material.DIRT, 32), new ItemStack(Material.DIAMOND_HELMET, 4), new ItemStack(Material.CHAINMAIL_LEGGINGS, 1)};
                if (Inventories.hasSpace(player, 0, armor, items)) {
                    if (add) {
                        sender.sendMessage(Inventories.addItem(player, 0, armor, items).toString());
                    }
                    sender.sendMessage("SO MUCH SPACE!");
                } else {
                    sender.sendMessage("No space!");
                }
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    return;
                }
                Inventories.removeItemExact(player, new ItemStack(Material.PUMPKIN, 1), Boolean.parseBoolean(args[1]));
                break;
            }
            default: {
                break;
            }
        }
    }
}
