package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class EssentialsToggleCommand extends EssentialsCommand {
    final String othersPermission;

    public EssentialsToggleCommand(final String command, final String othersPermission) {
        super(command);
        this.othersPermission = othersPermission;
    }

    protected void handleToggleWithArgs(final Server server, final User user, final String[] args) throws Exception {
        if (args.length == 1) {
            final Boolean toggle = matchToggleArgument(args[0]);
            if (toggle == null && user.isAuthorized(othersPermission)) {
                toggleOtherPlayers(server, user.getSource(), args);
            } else {
                togglePlayer(user.getSource(), user, toggle);
            }
        } else if (args.length == 2 && user.isAuthorized(othersPermission)) {
            toggleOtherPlayers(server, user.getSource(), args);
        } else {
            togglePlayer(user.getSource(), user, null);
        }
    }

    protected Boolean matchToggleArgument(final String arg) {
        if (arg.equalsIgnoreCase("on") || arg.startsWith("ena") || arg.equalsIgnoreCase("1")) {
            return true;
        } else if (arg.equalsIgnoreCase("off") || arg.startsWith("dis") || arg.equalsIgnoreCase("0")) {
            return false;
        }
        return null;
    }

    protected void toggleOtherPlayers(final Server server, final CommandSource sender, final String[] args) throws PlayerNotFoundException, NotEnoughArgumentsException {
        if (args.length < 1 || args[0].trim().length() < 2) {
            throw new PlayerNotFoundException();
        }

        final boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();
        boolean foundUser = false;
        final List<Player> matchedPlayers = server.matchPlayer(args[0]);
        for (final Player matchPlayer : matchedPlayers) {
            final User player = ess.getUser(matchPlayer);
            if (skipHidden && player.isHidden(sender.getPlayer()) && player.isHiddenFrom(sender.getPlayer())) {
                continue;
            }
            foundUser = true;
            if (args.length > 1) {
                final Boolean toggle = matchToggleArgument(args[1]);
                togglePlayerOnTheirThread(sender, player, toggle);
            } else {
                togglePlayerOnTheirThread(sender, player, null);
            }
        }
        if (!foundUser) {
            throw new PlayerNotFoundException();
        }
    }

    // Changing a player is only allowed from the thread that owns them, which on Folia is not necessarily the sender's.
    // When it is not, the change is made from the player's thread and any error is reported to the sender from there.
    private void togglePlayerOnTheirThread(final CommandSource sender, final User user, final Boolean enabled) throws NotEnoughArgumentsException {
        if (ess.getTaskScheduler().isOwnedByCurrentThread(user.getBase())) {
            togglePlayer(sender, user, enabled);
            return;
        }
        ess.getTaskScheduler().runEntity(user.getBase(), () -> {
            try {
                togglePlayer(sender, user, enabled);
            } catch (final Exception e) {
                showError(sender.getSender(), e, getName());
            }
        });
    }

    // Make sure when implementing this method that all 3 Boolean states are handled, 'null' should toggle the existing state.
    protected abstract void togglePlayer(CommandSource sender, User user, Boolean enabled) throws NotEnoughArgumentsException;

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            if (user.isAuthorized(othersPermission)) {
                return getPlayers(user);
            } else {
                return Lists.newArrayList("enable", "disable");
            }
        } else if (args.length == 2 && user.isAuthorized(othersPermission)) {
            return Lists.newArrayList("enable", "disable");
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(sender);
        } else if (args.length == 2) {
            return Lists.newArrayList("enable", "disable");
        } else {
            return Collections.emptyList();
        }
    }
}
