package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

public class Commandworth extends EssentialsCommand {
    public Commandworth() {
        super("worth");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final List<ItemStack> is = ess.getItemDb().getMatching(user, args);
        int count = 0;
        final boolean isBulk = is.size() > 1;
        BigDecimal totalWorth = BigDecimal.ZERO;
        for (ItemStack stack : is) {
            try {
                if (stack.getAmount() > 0) {
                    totalWorth = totalWorth.add(itemWorth(user.getSource(), user, stack, args));
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
        if (count > 1) {
            final String totalWorthStr = NumberUtil.displayCurrency(totalWorth, ess);
            if (args.length > 0 && args[0].equalsIgnoreCase("blocks")) {
                user.sendMessage(tl("totalSellableBlocks", totalWorthStr, totalWorthStr));
                return;
            }
            user.sendMessage(tl("totalSellableAll", totalWorthStr, totalWorthStr));
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        itemWorth(sender, null, ess.getItemDb().get(args[0]), args);
    }

    private BigDecimal itemWorth(final CommandSource sender, final User user, final ItemStack is, final String[] args) throws Exception {
        int amount = 1;
        if (user == null) {
            if (args.length > 1) {
                try {
                    amount = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
                } catch (final NumberFormatException ex) {
                    throw new NotEnoughArgumentsException(ex);
                }

            }
        } else {
            amount = ess.getWorth().getAmount(ess, user, is, args, true);
        }

        final BigDecimal worth = ess.getWorth().getPrice(ess, is);
        if (worth == null) {
            throw new Exception(tl("itemCannotBeSold"));
        }

        if (amount < 0) {
            amount = 0;
        }

        final BigDecimal result = worth.multiply(BigDecimal.valueOf(amount));
        sender.sendMessage(MaterialUtil.getDamage(is) != 0 ? tl("worthMeta", is.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", ""), MaterialUtil.getDamage(is), NumberUtil.displayCurrency(result, ess), amount, NumberUtil.displayCurrency(worth, ess)) : tl("worth", is.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", ""), NumberUtil.displayCurrency(result, ess), amount, NumberUtil.displayCurrency(worth, ess)));
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

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getItems();
        } else if (args.length == 2) {
            return Lists.newArrayList("1", "64");
        } else {
            return Collections.emptyList();
        }
    }
}
