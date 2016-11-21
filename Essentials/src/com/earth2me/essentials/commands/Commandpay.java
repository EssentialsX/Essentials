package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;

import net.ess3.api.MaxMoneyException;
import org.bukkit.Server;

import java.math.BigDecimal;

import static com.earth2me.essentials.I18n.tl;


public class Commandpay extends EssentialsLoopCommand {
    BigDecimal amount;

    public Commandpay() {
        super("pay");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }
        
        if (args[1].contains("-")) {
            throw new Exception(tl("payMustBePositive"));
        }

        String stringAmount = args[1].replaceAll("[^0-9\\.]", "");

        if (stringAmount.length() < 1) {
            throw new NotEnoughArgumentsException();
        }

        amount = new BigDecimal(stringAmount);
        if (amount.compareTo(ess.getSettings().getMinimumPayAmount()) < 0) { // Check if amount is less than minimum-pay-amount
            throw new Exception(tl("minimumPayAmount", NumberUtil.displayCurrencyExactly(ess.getSettings().getMinimumPayAmount(), ess)));
        }
        loopOnlinePlayers(server, user.getSource(), false, user.isAuthorized("essentials.pay.multiple"), args[0], args);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) throws ChargeException {
        User user = ess.getUser(sender.getPlayer());
        try {
            if (!player.isAcceptingPay()) {
                sender.sendMessage(tl("notAcceptingPay", player.getDisplayName()));
                return;
            }
            user.payUser(player, amount);
            Trade.log("Command", "Pay", "Player", user.getName(), new Trade(amount, ess), player.getName(), new Trade(amount, ess), user.getLocation(), ess);
        } catch (MaxMoneyException ex) {
            sender.sendMessage(tl("maxMoney"));
            try {
                user.setMoney(user.getMoney().add(amount));
            } catch (MaxMoneyException ignored) {
                // this should never happen
            }
        }
    }
}
