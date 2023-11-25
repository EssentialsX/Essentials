package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class Commandsell extends EssentialsCommand {
    public Commandsell() {
        super("sell");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        BigDecimal totalWorth = BigDecimal.ZERO;
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        if (args[0].equalsIgnoreCase("hand") && !user.isAuthorized("essentials.sell.hand")) {
            throw new Exception(tl("sellHandPermission"));
        } else if ((args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all")) && !user.isAuthorized("essentials.sell.bulk")) {
            throw new Exception(tl("sellBulkPermission"));
        }

        final List<ItemStack> is = ess.getItemDb().getMatching(user, args);
        int count = 0;

        final boolean isBulk = is.size() > 1;

        final List<ItemStack> notSold = new ArrayList<>();
        for (ItemStack stack : is) {
            if (!ess.getSettings().isAllowSellNamedItems()) {
                if (stack.getItemMeta() != null && stack.getItemMeta().hasDisplayName()) {
                    if (isBulk) {
                        notSold.add(stack);
                        continue;
                    }
                    throw new Exception(tl("cannotSellNamedItem"));
                }
            }
            try {
                if (!user.isAuthorized("essentials.sell.item."+stack.getType().toString())){
                    if (isBulk) {
                        notSold.add(stack);
                        continue;
                    }
                    user.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError: &4You do not have sufficient permissions to sell " + stack.getType()));
                    return;
                }

                if (stack.getAmount() > 0) {
                    totalWorth = totalWorth.add(sellItem(user, stack, args, isBulk));
                    stack = stack.clone();
                    count++;
                    for (final ItemStack zeroStack : is) {
                        if (zeroStack.isSimilar(stack)) {
                            zeroStack.setAmount(0);
                        }
                    }
                }
            } catch (final Exception e) {
                if (!isBulk) {
                    throw e;
                }
            }
        }
        if (!notSold.isEmpty()) {
            final List<String> names = new ArrayList<>();
            for (final ItemStack stack : notSold) {
                if (stack.getItemMeta() != null) { //This was already validated but IDE still freaks out
                    names.add(stack.getItemMeta().getDisplayName());
                }
            }
            ess.showError(user.getSource(), new Exception(tl("cannotSellTheseNamedItems", String.join(ChatColor.RESET + ", ", names))), commandLabel);
        }
        if (count != 1) {
            final String totalWorthStr = NumberUtil.displayCurrency(totalWorth, ess);
            if (args[0].equalsIgnoreCase("blocks")) {
                user.sendMessage(tl("totalWorthBlocks", totalWorthStr, totalWorthStr));
            } else {
                user.sendMessage(tl("totalWorthAll", totalWorthStr, totalWorthStr));
            }
        }
    }

    private BigDecimal sellItem(final User user, final ItemStack is, final String[] args, final boolean isBulkSell) throws Exception {
        final int amount = ess.getWorth().getAmount(ess, user, is, args, isBulkSell);
        final BigDecimal worth = ess.getWorth().getPrice(ess, is);

        if (worth == null) {
            throw new Exception(tl("itemCannotBeSold"));
        }

        if (amount <= 0) {
            if (!isBulkSell) {
                user.sendMessage(tl("itemSold", NumberUtil.displayCurrency(BigDecimal.ZERO, ess), BigDecimal.ZERO, is.getType().toString().toLowerCase(Locale.ENGLISH), NumberUtil.displayCurrency(worth, ess)));
            }
            return BigDecimal.ZERO;
        }

        final BigDecimal result = worth.multiply(BigDecimal.valueOf(amount));

        //TODO: Prices for Enchantments
        final ItemStack ris = is.clone();
        ris.setAmount(amount);
        if (!Inventories.containsAtLeast(user.getBase(), ris, amount)) {
            // This should never happen.
            throw new IllegalStateException("Trying to remove more items than are available.");
        }
        Inventories.removeItemAmount(user.getBase(), ris, ris.getAmount());
        user.getBase().updateInventory();
        Trade.log("Command", "Sell", "Item", user.getName(), new Trade(ris, ess), user.getName(), new Trade(result, ess), user.getLocation(), user.getMoney(), ess);
        user.giveMoney(result, null, UserBalanceUpdateEvent.Cause.COMMAND_SELL);
        user.sendMessage(tl("itemSold", NumberUtil.displayCurrency(result, ess), amount, is.getType().toString().toLowerCase(Locale.ENGLISH), NumberUtil.displayCurrency(worth, ess)));
        ess.getLogger().log(Level.INFO, tl("itemSoldConsole", user.getName(), is.getType().toString().toLowerCase(Locale.ENGLISH), NumberUtil.displayCurrency(result, ess), amount, NumberUtil.displayCurrency(worth, ess), user.getDisplayName()));
        return result;
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getMatchingItems(args[0]);
        } else if (args.length == 2) {
            return Lists.newArrayList("1", "64");
        } else {
            return Collections.emptyList();
        }
    }
}
