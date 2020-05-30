package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
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

        EcoCommands cmd;
        boolean isPercent;
        BigDecimal amount;
        try {
            cmd = EcoCommands.valueOf(args[0].toUpperCase(Locale.ENGLISH));
            isPercent = cmd != EcoCommands.RESET && args[2].endsWith("%");
            amount = (cmd == EcoCommands.RESET) ? ess.getSettings().getStartingBalance() : new BigDecimal(args[2].replaceAll("[^0-9\\.]", ""));
        } catch (Exception ex) {
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
                        throw new Exception(tl("minimumBalanceError", NumberUtil.displayCurrency(ess.getSettings().getMinMoney(), ess)));
                    }
                    break;
                }
                case RESET:
                case SET: {
                    BigDecimal minBal = ess.getSettings().getMinMoney();
                    BigDecimal maxBal = ess.getSettings().getMaxMoney();
                    boolean underMin = (userAmount.compareTo(minBal) < 0);
                    boolean aboveMax = (userAmount.compareTo(maxBal) > 0);
                    player.setMoney(underMin ? minBal : aboveMax ? maxBal : userAmount, UserBalanceUpdateEvent.Cause.COMMAND_ECO);
                    player.sendMessage(tl("setBal", NumberUtil.displayCurrency(player.getMoney(), ess)));
                    sender.sendMessage(tl("setBalOthers", player.getDisplayName(), NumberUtil.displayCurrency(player.getMoney(), ess)));
                    break;
                }
            }
        });
    }


    private enum EcoCommands {
        GIVE, TAKE, SET, RESET
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, final CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            List<String> options = Lists.newArrayList();
            for (EcoCommands command : EcoCommands.values()) {
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
}
