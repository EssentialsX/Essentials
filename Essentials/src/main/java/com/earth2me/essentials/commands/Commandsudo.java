package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.Locale;

public class Commandsudo extends EssentialsLoopCommand {
    public Commandsudo() {
        super("sudo");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final String command = getFinalArg(args, 1);
        final boolean multiple = !sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.sudo.multiple");

        sender.sendTl("sudoRun", args[0], command, "");
        loopOnlinePlayers(server, sender, false, multiple, args[0], new String[] {command});
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) {
        if (user.getName().equals(sender.getSender().getName())) {
            return; // Silently don't do anything.
        }

        if (user.isAuthorized("essentials.sudo.exempt") && sender.isPlayer()) {
            sender.sendTl("sudoExempt", user.getName());
            return;
        }

        if (args[0].toLowerCase(Locale.ENGLISH).startsWith("c:")) {
            user.getBase().chat(getFinalArg(args, 0).substring(2));
            return;
        }

        final String command = getFinalArg(args, 0);
        if (command.length() > 0) {
            class SudoCommandTask implements Runnable {
                @Override
                public void run() {
                    try {
                        user.getBase().chat("/" + command);
                    } catch (final Exception e) {
                        sender.sendTl("errorCallingCommand", command);
                    }
                }
            }

            ess.scheduleSyncDelayedTask(new SudoCommandTask());
        }
    }
}
