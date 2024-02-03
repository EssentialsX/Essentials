package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.utils.CommonPlaceholders;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Commandexp extends EssentialsLoopCommand {
    public Commandexp() {
        super("exp");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        final IUser user = sender.getUser();
        if (args.length == 0 || (args.length < 2 && user == null)) {
            if (user == null) {
                throw new NotEnoughArgumentsException();
            }
            showExp(sender, user);
            return;
        }

        final ExpCommands cmd;
        try {
            cmd = ExpCommands.valueOf(args[0].toUpperCase(Locale.ENGLISH));
        } catch (final Exception ex) {
            throw new NotEnoughArgumentsException(ex);
        }

        if (!cmd.hasPermission(user)) {
            user.sendTl("noAccessSubCommand", "/" + commandLabel + " " + cmd.name().toLowerCase(Locale.ENGLISH));
            return;
        }

        switch (cmd) {
            case SET: {
                if (args.length == 3 && cmd.hasOtherPermission(user)) {
                    loopOnlinePlayersConsumer(server, sender, false, true, args[1], player -> setExp(sender, player, args[2], false));
                } else if (args.length == 2 && user != null) {
                    setExp(sender, user, args[1], false);
                } else {
                    throw new NotEnoughArgumentsException();
                }
                return;
            }
            case GIVE: {
                if (args.length == 3 && cmd.hasOtherPermission(user)) {
                    loopOnlinePlayersConsumer(server, sender, false, true, args[1], player -> setExp(sender, player, args[2], true));
                } else if (args.length == 2 && user != null) {
                    setExp(sender, user, args[1], true);
                } else {
                    throw new NotEnoughArgumentsException();
                }
                return;
            }
            case TAKE: {
                if (args.length == 3 && cmd.hasOtherPermission(user)) {
                    loopOnlinePlayersConsumer(server, sender, false, true, args[1], player -> setExp(sender, player, "-" + args[2], true));
                } else if (args.length == 2) {
                    setExp(sender, user, "-" + args[1], true);
                } else {
                    throw new NotEnoughArgumentsException();
                }
                return;
            }
            case RESET: {
                if (args.length == 2 && cmd.hasOtherPermission(user)) {
                    loopOnlinePlayersConsumer(server, sender, false, true, args[1], player -> setExp(sender, player, "0", false));
                } else if (user != null) {
                    setExp(sender, user, "0", false);
                } else {
                    throw new NotEnoughArgumentsException();
                }
                return;
            }
            case SHOW: {
                if (args.length == 2 && (user == null || user.isAuthorized("essentials.exp.others"))) {
                    showExp(sender, getPlayer(server, sender, args[1]));
                } else if (user != null) {
                    showExp(sender, user);
                } else {
                    throw new NotEnoughArgumentsException();
                }
                return;
            }
        }
        throw new NotEnoughArgumentsException(); //Should never happen but in the impossible chance it does...
    }

    private void showExp(final CommandSource sender, final IUser target) {
        sender.sendTl("exp", CommonPlaceholders.displayNameRecipient((IMessageRecipient) target), SetExpFix.getTotalExperience(target.getBase()), target.getBase().getLevel(), SetExpFix.getExpUntilNextLevel(target.getBase()));
    }

    //TODO: Limit who can give negative exp?
    private void setExp(final CommandSource sender, final IUser target, String strAmount, final boolean give) throws NotEnoughArgumentsException {
        long amount;
        strAmount = strAmount.toLowerCase(Locale.ENGLISH);
        if (strAmount.contains("l")) {
            strAmount = strAmount.replaceAll("l", "");
            int neededLevel = Integer.parseInt(strAmount);
            if (give) {
                neededLevel += target.getBase().getLevel();
            }
            amount = SetExpFix.getExpToLevel(neededLevel);
            SetExpFix.setTotalExperience(target.getBase(), 0);
        } else {
            amount = Long.parseLong(strAmount);
            if (amount > Integer.MAX_VALUE || amount < Integer.MIN_VALUE) {
                throw new NotEnoughArgumentsException();
            }
        }

        if (give) {
            amount += SetExpFix.getTotalExperience(target.getBase());
        }
        if (amount > Integer.MAX_VALUE) {
            amount = Integer.MAX_VALUE;
        }
        if (amount < 0L) {
            amount = 0L;
        }
        SetExpFix.setTotalExperience(target.getBase(), (int) amount);
        sender.sendTl("expSet", CommonPlaceholders.displayNameRecipient((IMessageRecipient) target), amount);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) {
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> options = Lists.newArrayList("show");
            for (final ExpCommands cmd : ExpCommands.values()) {
                if (cmd.hasPermission(user)) {
                    options.add(cmd.name().toLowerCase(Locale.ENGLISH));
                }
            }
            return options;
        } else if (args.length == 2) {
            if ((args[0].equalsIgnoreCase("set") && user.isAuthorized("essentials.exp.set")) || (args[0].equalsIgnoreCase("give") && user.isAuthorized("essentials.exp.give")) || (args[0].equalsIgnoreCase("take") && user.isAuthorized("essentials.exp.take"))) {
                final String levellessArg = args[1].toLowerCase(Locale.ENGLISH).replaceAll("l", "");
                if (NumberUtil.isInt(levellessArg)) {
                    return Lists.newArrayList(levellessArg + "l");
                }
            }
            if (user.isAuthorized("essentials.exp.others")) {
                return getPlayers(server, user);
            }
        } else if (args.length == 3 && !(args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("reset"))) {
            final String levellessArg = args[2].toLowerCase(Locale.ENGLISH).replaceAll("l", "");
            if (NumberUtil.isInt(levellessArg)) {
                return Lists.newArrayList(levellessArg + "l");
            }
        }
        return Collections.emptyList();
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> list = new ArrayList<>();
            for (final ExpCommands cmd : ExpCommands.values()) {
                list.add(cmd.name().toLowerCase(Locale.ENGLISH));
            }
            return list;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("give")) {
                final String levellessArg = args[1].toLowerCase(Locale.ENGLISH).replace("l", "");
                if (NumberUtil.isInt(levellessArg)) {
                    return Lists.newArrayList(levellessArg, args[1] + "l");
                } else {
                    return Collections.emptyList();
                }
            } else { // even without 'show'
                return getPlayers(server, sender);
            }
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("give"))) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }

    private enum ExpCommands {
        SET,
        GIVE,
        TAKE,
        RESET,
        SHOW(false);

        private final boolean permCheck;

        ExpCommands() {
            permCheck = true;
        }

        ExpCommands(final boolean perm) {
            permCheck = perm;
        }

        boolean hasPermission(final IUser user) {
            return user == null || !permCheck || user.isAuthorized("essentials.exp." + name().toLowerCase(Locale.ENGLISH));
        }

        boolean hasOtherPermission(final IUser user) {
            return user == null || user.isAuthorized("essentials.exp." + name().toLowerCase(Locale.ENGLISH) + ".others");
        }
    }
}
