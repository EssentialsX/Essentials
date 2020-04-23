package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.utils.ArrayUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.earth2me.essentials.I18n.tl;


public class Commandclearinventory extends EssentialsCommand {

    private static final int BASE_AMOUNT = 100000;
    private static final int EXTENDED_CAP = 8;

    public Commandclearinventory() {
        super("clearinventory");
    }

    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        parseCommand(server, user.getSource(), commandLabel, args, user.isAuthorized("essentials.clearinventory.others"),
                user.isAuthorized("essentials.clearinventory.all") || user.isAuthorized("essentials.clearinventory.multiple"));
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        parseCommand(server, sender, commandLabel, args, true, true);
    }

    private void parseCommand(Server server, CommandSource sender, String commandLabel, String[] args, boolean allowOthers, boolean allowAll)
            throws Exception {
        Collection<Player> players = new ArrayList<>();
        User senderUser = ess.getUser(sender.getPlayer());
        String previousClearCommand = "";
        String[] handlerArgs = args;

        if (sender.isPlayer()) {
            players.add(sender.getPlayer());
            // Clear previous command execution before potential errors to reset confirmation.
            previousClearCommand = senderUser.getConfirmingClearCommand();
            senderUser.setConfirmingClearCommand(null);
        }

        if (allowAll && args.length > 0 && args[0].contentEquals("*")) {
            sender.sendMessage(tl("inventoryClearingFromAll"));
            handlerArgs = ArrayUtil.removeFirst(args, 1);
            players = ess.getOnlinePlayers();
        } else if (allowOthers && args.length > 0 && args[0].trim().length() > 2) {
            handlerArgs = ArrayUtil.removeFirst(args, 1);
            players = server.matchPlayer(args[0].trim());
        }

        if (players.size() < 1) {
            throw new PlayerNotFoundException();
        }


        // Confirm
        String formattedCommand = formatCommand(commandLabel, args);
        if (senderUser != null && senderUser.isPromptingClearConfirm()) {
            if (!formattedCommand.equals(previousClearCommand)) {
                senderUser.setConfirmingClearCommand(formattedCommand);
                senderUser.sendMessage(tl("confirmClear", formattedCommand));
                return;
            }
        }

        for (Player player : players) {
            handleClear(sender, player, handlerArgs, players.size() < EXTENDED_CAP);
        }
    }

    private void handleClear(CommandSource sender, Player player, String[] args, boolean showExtended) throws Exception {
        ClearHandlerType type = ClearHandlerType.ALL_EXCEPT_ARMOR;
        Material mat = null;
        short data = -1;
        int amount = -1;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("**")) {
                type = ClearHandlerType.ALL_INCLUDING_ARMOR;
            } else if (!args[0].equalsIgnoreCase("*")) {
                final String[] split = args[0].split(":");
                final ItemStack item = ess.getItemDb().get(split[0]);
                type = ClearHandlerType.SPECIFIC_ITEM;
                mat = item.getType();

                if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_13_0_R01)) {
                    if (split.length > 1 && NumberUtil.isInt(split[1])) {
                        data = Short.parseShort(split[1]);
                    } else {
                        data = item.getDurability();
                    }
                }
            }
        }

        if (type != ClearHandlerType.SPECIFIC_ITEM) {
            clearAll(sender, player, type, showExtended);
            return;
        }

        if (args.length > 1 && NumberUtil.isInt(args[1])) {
            amount = Integer.parseInt(args[1]);
        }

        if (data == -1) { // data -1 means that all subtypes will be cleared, or we're on 1.13+
            if (amount == -1) {
                ItemStack stack = new ItemStack(mat);
                if (showExtended) {
                    sender.sendMessage(tl("inventoryClearingAllStack", stack.getType().toString().toLowerCase(Locale.ENGLISH), player.getDisplayName()));
                }
                player.getInventory().remove(mat);
            } else {
                if (amount < 0) {
                    amount = 1;
                }
                ItemStack stack = new ItemStack(mat, amount);
                clearAmount(sender, player, showExtended, amount, stack);
            }
        } else { // there's a data value here
            if (amount == -1) { // amount -1 means all items will be cleared
                ItemStack stack = new ItemStack(mat, BASE_AMOUNT, data);
                ItemStack removedStack = player.getInventory().removeItem(stack).get(0);
                final int removedAmount = (BASE_AMOUNT - removedStack.getAmount());
                if (removedAmount > 0 || showExtended) {
                    sender.sendMessage(tl("inventoryClearingStack", removedAmount, stack.getType().toString().toLowerCase(Locale.ENGLISH), player.getDisplayName()));
                }
            } else {
                if (amount < 0) {
                    amount = 1;
                }
                ItemStack stack = new ItemStack(mat, amount, data);
                clearAmount(sender, player, showExtended, amount, stack);
            }
        }
    }

    private void clearAmount(CommandSource sender, Player player, boolean showExtended, int amount, ItemStack stack) {
        if (player.getInventory().containsAtLeast(stack, amount)) {
            sender.sendMessage(tl("inventoryClearingStack", amount, stack.getType().toString().toLowerCase(Locale.ENGLISH), player.getDisplayName()));
            player.getInventory().removeItem(stack);
        } else {
            if (showExtended) {
                sender.sendMessage(tl("inventoryClearFail", player.getDisplayName(), amount, stack.getType().toString().toLowerCase(Locale.ENGLISH)));
            }
        }
    }

    private void clearAll(CommandSource sender, Player player, ClearHandlerType type, boolean showExtended) {
        if (type == ClearHandlerType.ALL_EXCEPT_ARMOR) { // wildcard; all items except for armor
            if (showExtended) {
                sender.sendMessage(tl("inventoryClearingAllItems", player.getDisplayName()));
            }
        } else if (type == ClearHandlerType.ALL_INCLUDING_ARMOR) { // double wildcard; all items including armor
            if (showExtended) {
                sender.sendMessage(tl("inventoryClearingAllArmor", player.getDisplayName()));
            }
            player.getInventory().setArmorContents(null);
        }

        InventoryWorkaround.clearInventoryNoArmor(player.getInventory());
        InventoryWorkaround.setItemInOffHand(player, null);
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

    private enum ClearHandlerType {
        ALL_EXCEPT_ARMOR,
        ALL_INCLUDING_ARMOR,
        SPECIFIC_ITEM
    }
}
