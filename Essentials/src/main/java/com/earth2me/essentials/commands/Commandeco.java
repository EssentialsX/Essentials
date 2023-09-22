package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Server;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

public class Commandeco extends EssentialsLoopCommand {

    public Commandeco() {
        super("eco");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final EcoCommands cmd;
        final boolean isPercent;
        final BigDecimal amount;
        try {
            cmd = EcoCommands.valueOf(args[0].toUpperCase(Locale.ENGLISH));
            isPercent = cmd != EcoCommands.RESET && args[2].endsWith("%");
            amount = (cmd == EcoCommands.RESET) ? ess.getSettings().getStartingBalance() : new BigDecimal(args[2].replaceAll("[^0-9\\.]", ""));
        } catch (final Exception ex) {
            throw new NotEnoughArgumentsException(ex);
        }

        loopOfflinePlayersConsumer(server, sender, false, true, args[1], player -> {
            BigDecimal userAmount = amount;
            if (isPercent) {
                userAmount = player.getMoney().multiply(userAmount).scaleByPowerOfTen(-2);
            }

            switch (cmd) {
                case GIVE: {
                    player.giveMoney(userAmount, sender, UserBalanceUpdateEvent.Cause.COMMAND_ECO);
                    break;
                }
                case TAKE: {
                    if (player.getMoney().subtract(userAmount).compareTo(ess.getSettings().getMinMoney()) >= 0) {
                        player.takeMoney(userAmount, sender, UserBalanceUpdateEvent.Cause.COMMAND_ECO);
                    } else {
                        ess.showError(sender, new Exception(tl("minimumBalanceError", NumberUtil.displayCurrency(ess.getSettings().getMinMoney(), ess))), commandLabel);
                    }
                    break;
                }
                case RESET:
                case SET: {
                    final BigDecimal minBal = ess.getSettings().getMinMoney();
                    final BigDecimal maxBal = ess.getSettings().getMaxMoney();
                    final boolean underMin = userAmount.compareTo(minBal) < 0;
                    final boolean aboveMax = userAmount.compareTo(maxBal) > 0;
                    player.setMoney(underMin ? minBal : aboveMax ? maxBal : userAmount, UserBalanceUpdateEvent.Cause.COMMAND_ECO);
                    player.sendMessage(tl("setBal", NumberUtil.displayCurrency(player.getMoney(), ess)));
                    sender.sendMessage(tl("setBalOthers", player.getName(), NumberUtil.displayCurrency(player.getMoney(), ess)));
                    break;
                }
            }
        });
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) throws NotEnoughArgumentsException, PlayerExemptException, ChargeException, MaxMoneyException {

    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> options = Lists.newArrayList();
            for (final EcoCommands command : EcoCommands.values()) {
                options.add(command.name().toLowerCase(Locale.ENGLISH));
            }
            return options;
        } else if (args.length == 2) {
            return getPlayers(server, sender);
        } else if (args.length == 3 && !args[0].equalsIgnoreCase(EcoCommands.RESET.name())) {
            if (args[0].equalsIgnoreCase(EcoCommands.SET.name())) {
                return Lists.newArrayList("0", ess.getSettings().getStartingBalance().toString());
            } else {
                return Lists.newArrayList("1", "10", "100", "1000");
            }
        } else {
            return Collections.emptyList();
        }
    }

    private enum EcoCommands {
        GIVE, TAKE, SET, RESET
    }
}
