package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.CommonPlaceholders;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IUser;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

public class Commandbalance extends EssentialsCommand {
    public Commandbalance() {
        super("balance");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        final User target = getPlayer(server, args, 0, false, true);
        sender.sendTl("balanceOther", target.isHidden() ? target.getName() : CommonPlaceholders.displayName((IUser) target), NumberUtil.displayCurrency(target.getMoney(), ess));
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 1 && user.isAuthorized("essentials.balance.others")) {
            final User target = getPlayer(server, args, 0, true, true);
            user.sendTl("balanceOther", target.isHidden() ? target.getName() : CommonPlaceholders.displayName((IUser) target), NumberUtil.displayCurrency(target.getMoney(), ess));
        } else if (args.length < 2) {
            user.sendTl("balance", NumberUtil.displayCurrency(user.getMoney(), ess));
        } else {
            throw new NotEnoughArgumentsException();
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.balance.others")) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
