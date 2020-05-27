package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.earth2me.essentials.I18n.tl;


public class Commandclearinventory extends EssentialsLoopCommand {

    public Commandclearinventory() {
        super("clearinventory");
    }

    private static final int BASE_AMOUNT = 100000;

    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        final String previousClearCommand = user.getConfirmingClearCommand();
        user.setConfirmingClearCommand(null);

        String formattedCommand = formatCommand(commandLabel, args);
        if (user.isPromptingClearConfirm()) {
            if (!formattedCommand.equals(previousClearCommand)) {
                user.setConfirmingClearCommand(formattedCommand);
                user.sendMessage(tl("confirmClear", formattedCommand));
                return;
            }
        }

        if (args.length == 0 || !args[0].contains("*") || server.matchPlayer(args[0]).isEmpty()) {
            clearHandler(user.getSource(), user.getBase(), args, 0);
            return;
        }

        if (user.isAuthorized("essentials.clearinventory.others")) {
            loopOnlinePlayers(server, user.getSource(), true, true, args[0], null);
        }
        throw new PlayerNotFoundException();
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        loopOnlinePlayers(server, sender, true, true, args[0], args);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) throws PlayerExemptException {
        clearHandler(sender, player.getBase(), args, 1);
    }

    private static class Item {
        private final Material material;
        private final short data;

        public Item(Material material, short data) {
            this.material = material;
            this.data = data;
        }

        public Material getMaterial() {
            return material;
        }

        public short getData() {
            return data;
        }
    }

    private enum ClearHandlerType {
        ALL_EXCEPT_ARMOR, ALL_INCLUDING_ARMOR, SPECIFIC_ITEM
    }

    protected void clearHandler(CommandSource sender, Player player, String[] args, int offset) {
        ClearHandlerType type = ClearHandlerType.ALL_EXCEPT_ARMOR;
        final Set<Item> items = new HashSet<>();
        int amount = -1;

        if (args.length > (offset + 1) && NumberUtil.isInt(args[(offset + 1)])) {
            amount = Integer.parseInt(args[(offset + 1)]);
        }
        if (args.length > offset) {
            if (args[offset].equalsIgnoreCase("**")) {
                type = ClearHandlerType.ALL_INCLUDING_ARMOR;
            } else if (!args[offset].equalsIgnoreCase("*")) {
                final String[] split = args[offset].split(",");
                for (String item : split) {
                    final String[] itemParts = item.split(":");
                    short data;
                    try {
                        data = Short.parseShort(itemParts[1]);
                    } catch (Exception e) {
                        data = 0;
                    }
                    try {
                        items.add(new Item(ess.getItemDb().get(itemParts[0]).getType(), data));
                    } catch (Exception ignored) {}
                }
                type = ClearHandlerType.SPECIFIC_ITEM;
            }
        }

        if (type == ClearHandlerType.ALL_EXCEPT_ARMOR) {
            sender.sendMessage(tl("inventoryClearingAllItems", player.getDisplayName()));
            InventoryWorkaround.clearInventoryNoArmor(player.getInventory());
            InventoryWorkaround.setItemInOffHand(player, null);
        } else if (type == ClearHandlerType.ALL_INCLUDING_ARMOR) {
            sender.sendMessage(tl("inventoryClearingAllArmor", player.getDisplayName()));
            InventoryWorkaround.clearInventoryNoArmor(player.getInventory());
            InventoryWorkaround.setItemInOffHand(player, null);
            player.getInventory().setArmorContents(null);
        } else {
            for (Item item : items) {
                ItemStack stack = new ItemStack(item.getMaterial());
                if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_13_0_R01)) {
                    stack.setDurability(item.getData());
                }
                if (amount == -1) // amount -1 means all items will be cleared
                {
                    stack.setAmount(BASE_AMOUNT);
                    ItemStack removedStack = player.getInventory().removeItem(stack).get(0);
                    final int removedAmount = (BASE_AMOUNT - removedStack.getAmount());
                    if (removedAmount > 0) {
                        sender.sendMessage(tl("inventoryClearingStack", removedAmount, stack.getType().toString().toLowerCase(Locale.ENGLISH), player.getDisplayName()));
                    }
                } else {
                    stack.setAmount(amount < 0 ? 1 : amount);
                    if (player.getInventory().containsAtLeast(stack, amount)) {
                        sender.sendMessage(tl("inventoryClearingStack", amount, stack.getType().toString().toLowerCase(Locale.ENGLISH), player.getDisplayName()));
                        player.getInventory().removeItem(stack);
                    }
                }
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (user.isAuthorized("essentials.clearinventory.others")) {
            if (args.length == 1) {
                List<String> options = getPlayers(server, user);
                if (user.isAuthorized("essentials.clearinventory.all") || user.isAuthorized("essentials.clearinventory.multiple")) {
                    // Assume that nobody will have the 'all' permission without the 'others' permission
                    options.add("*");
                }
                return options;
            } else if (args.length == 2) {
                List<String> items = new ArrayList<>(getItems());
                items.add("*");
                items.add("**");
                return items;
            } else {
                return Collections.emptyList();
            }
        } else {
            if (args.length == 1) {
                List<String> items = new ArrayList<>(getItems());
                items.add("*");
                items.add("**");
                return items;
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            List<String> options = getPlayers(server, sender);
            options.add("*");
            return options;
        } else if (args.length == 2) {
            List<String> items = new ArrayList<>(getItems());
            items.add("*");
            items.add("**");
            return items;
        } else {
            return Collections.emptyList();
        }
    }

    private String formatCommand(String commandLabel, String[] args) {
        return "/" + commandLabel + " " + StringUtil.joinList(" ", args);
    }
}
