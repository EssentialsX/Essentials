package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.collect.Lists;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.TranslatableException;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Server;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Commandpay extends EssentialsLoopCommand {
    private static final BigDecimal THOUSAND = new BigDecimal(1000);
    private static final BigDecimal MILLION = new BigDecimal(1_000_000);
    private static final BigDecimal BILLION = new BigDecimal(1_000_000_000);
    private static final BigDecimal TRILLION = new BigDecimal(1_000_000_000_000L);

    public Commandpay() {
        super("pay");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final String ogStr = args[1];

        if (ogStr.contains("-")) {
            throw new TranslatableException("payMustBePositive");
        }

        final String sanitizedString = ogStr.replaceAll("[^0-9.]", "");

        if (sanitizedString.isEmpty()) {
            throw new NotEnoughArgumentsException();
        }

        BigDecimal tempAmount = new BigDecimal(sanitizedString);
        switch (Character.toLowerCase(ogStr.charAt(ogStr.length() - 1))) {
            case 'k': {
                tempAmount = tempAmount.multiply(THOUSAND);
                break;
            }
            case 'm': {
                tempAmount = tempAmount.multiply(MILLION);
                break;
            }
            case 'b': {
                tempAmount = tempAmount.multiply(BILLION);
                break;
            }
            case 't': {
                tempAmount = tempAmount.multiply(TRILLION);
                break;
            }
            default: {
                break;
            }
        }

        final BigDecimal amount = tempAmount;

        if (amount.compareTo(ess.getSettings().getMinimumPayAmount()) < 0) { // Check if amount is less than minimum-pay-amount
            throw new TranslatableException("minimumPayAmount", NumberUtil.displayCurrencyExactly(ess.getSettings().getMinimumPayAmount(), ess));
        }
        final AtomicBoolean informToConfirm = new AtomicBoolean(false);
        final boolean canPayOffline = user.isAuthorized("essentials.pay.offline");
        if (!canPayOffline && args[0].equals("**")) {
            user.sendTl("payOffline");
            return;
        }
        loopOfflinePlayersConsumer(server, user.getSource(), false, user.isAuthorized("essentials.pay.multiple"), args[0], player -> {
            try {
                if (player.getBase() != null && (!player.getBase().isOnline() || player.isHidden(user.getBase())) && !canPayOffline) {
                    user.sendTl("payOffline");
                    return;
                }

                if (!player.isAcceptingPay() || (ess.getSettings().isPayExcludesIgnoreList() && player.isIgnoredPlayer(user))) {
                    user.sendTl("notAcceptingPay", player.getDisplayName());
                    return;
                }
                if (user.isPromptingPayConfirm() && !amount.equals(user.getConfirmingPayments().get(player))) { // checks if exists and if command needs to be repeated.
                    // Used to reset confirmations and inform to confirm when a new pay command has been inserted.
                    if (!informToConfirm.get()) {
                        // User hasnt been asked to confirm payment to this player, reset all confirmed payments and ask to confirm again.
                        // Clear previous confirmations to ensure that a new confirmation message is brought up.
                        user.getConfirmingPayments().clear();
                        informToConfirm.set(true);
                    }
                    user.getConfirmingPayments().put(player, amount);
                    return;
                }
                user.payUser(player, amount, UserBalanceUpdateEvent.Cause.COMMAND_PAY);
                user.getConfirmingPayments().remove(player);
                Trade.log("Command", "Pay", "Player", user.getName(), new Trade(amount, ess), player.getName(), new Trade(amount, ess), user.getLocation(), user.getMoney(), ess);
            } catch (final MaxMoneyException ex) {
                user.sendTl("maxMoney");
                try {
                    user.setMoney(user.getMoney().add(amount));
                } catch (final MaxMoneyException ignored) {
                }
            } catch (final Exception e) {
                user.sendMessage(e.getMessage());
            }
        });
        if (informToConfirm.get()) {
            final String cmd = "/" + commandLabel + " " + StringUtil.joinList(" ", args);
            user.sendTl("confirmPayment", NumberUtil.displayCurrency(amount, ess), cmd);
        }
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) {

    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else if (args.length == 2) {
            return Lists.newArrayList(ess.getSettings().getMinimumPayAmount().toString());
        } else {
            return Collections.emptyList();
        }
    }
}
