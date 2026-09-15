package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.adventure.AdventureUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import net.ess3.api.TranslatableException;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.Map;

import static com.earth2me.essentials.I18n.tlLiteral;

// The file commandsell.java has been used as a "template"
// Most of the code in this file is therefor a one-to-one copy from that file
public class Commandbuy extends EssentialsCommand {
    public Commandbuy() {
        super("buy");
    }

    // Starting point for when the command is run
    // Args is an array with name of item in postion 0 and amount in position 1.
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        BigDecimal totalWorth = BigDecimal.ZERO;

        // Throw an error if the user has not specified enough arguments.
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final ItemStack is = ess.getItemDb().get(args[0]);

        totalWorth = totalWorth.add(buyItem(user, is, args));
    }

    private BigDecimal buyItem(final User user, final ItemStack is, final String[] args) throws Exception {
        final int amount = Integer.parseInt(args[1]);
        final BigDecimal originalWorth = ess.getWorth().getPrice(ess, is);
        final BigDecimal worth = originalWorth == null ? null : originalWorth.multiply(ess.getSettings().getMultiplier(user));
        final BigDecimal playerMoney = user.getMoney();
        final boolean isDropItemsIfFull = ess.getSettings().isDropItemsIfFull();

        // Check if the item can be sold
        if (worth == null) {
            throw new TranslatableException("itemCannotBeBought");
        }

        // Input validation, check if the user is trying to buy a valid amount of item
        if (amount <= 0) {
            return BigDecimal.ZERO;
        }

        // Get the total worth of all instances of the item
        final BigDecimal result = worth.multiply(BigDecimal.valueOf(amount));
        final ItemStack ris = is.clone();
        ris.setAmount(amount);

        // Take the money from the account
        // But only if the player has enough money
        if (playerMoney.compareTo(result) > 0) {
            user.takeMoney(result, null, UserBalanceUpdateEvent.Cause.COMMAND_BUY);
        } else {
            throw new TranslatableException("notEnoughMoney");
        }

        // Give the items the user is trying to buy
        // addITem returns any leftover items that can not be given to the player
        final Map<Integer, ItemStack> leftoverItems = Inventories.addItem(user.getBase(), user.isAuthorized("essentials.oversizedstacks") ? ess.getSettings().getOversizedStackSize() : 0, ris);

        // Only drop items if Essentials is configured to drop items if full
        for (final ItemStack item : leftoverItems.values()) {
            if (isDropItemsIfFull) {
                final World w = user.getWorld();
                w.dropItemNaturally(user.getLocation(), item);
            } else {
                user.sendTl("giveSpawnFailure", item.getAmount(), args[0], user.getDisplayName());
            }
        }

        user.getBase().updateInventory();
        Trade.log("Command", "Buy", "Item", user.getName(), new Trade(ris, ess), user.getName(), new Trade(result, ess), user.getLocation(), user.getMoney(), ess);

        final String typeName = is.getType().toString().toLowerCase(Locale.ENGLISH);
        final AdventureUtil.ParsedPlaceholder worthDisplay = AdventureUtil.parsed(NumberUtil.displayCurrency(worth, ess));
        user.sendTl("itemBought", AdventureUtil.parsed(NumberUtil.displayCurrency(result, ess)), amount, typeName, worthDisplay);
        ess.getLogger().log(Level.INFO, ess.getAdventureFacet().miniToLegacy(tlLiteral("itemBoughtConsole", user.getName(), typeName, ess.getAdventureFacet().miniToLegacy(NumberUtil.displayCurrency(result, ess)), amount, ess.getAdventureFacet().miniToLegacy(worthDisplay.toString()), user.getDisplayName())));
        return result;
    }

    // We need to override the getMatchingItems function because we only want to list out all the possible items that exists.
    // The default ArrayList of inventory, hand and block does not make sense with this command at the moment.
    @Override
    protected List<String> getMatchingItems(final String arg) {
        final List<String> items = Lists.newArrayList();
        items.addAll(getItems());
        return items;
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
