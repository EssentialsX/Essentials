package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public abstract class EssentialsLoopCommand extends EssentialsCommand {
    public EssentialsLoopCommand(final String command) {
        super(command);
    }

    protected void loopOfflinePlayers(final Server server, final CommandSource sender, final boolean multipleStringMatches, final boolean matchWildcards, final String searchTerm, final String[] commandArgs) throws TranslatableException, NotEnoughArgumentsException {
        loopOfflinePlayersConsumer(server, sender, multipleStringMatches, matchWildcards, searchTerm, user -> updatePlayer(server, sender, user, commandArgs));
    }

    protected void loopOfflinePlayersConsumer(final Server server, final CommandSource sender, final boolean multipleStringMatches, final boolean matchWildcards, final String searchTerm, final UserConsumer userConsumer) throws TranslatableException, NotEnoughArgumentsException {
        if (searchTerm.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        if (sender.isPlayer() && (searchTerm.equals("@s") || searchTerm.equals("@p"))) {
            userConsumer.accept((User) sender.getUser());
            return;
        }

        final UUID uuid = StringUtil.toUUID(searchTerm);
        if (uuid != null) {
            final User matchedUser = ess.getUser(uuid);
            if (matchedUser == null) {
                throw new PlayerNotFoundException();
            }
            userConsumer.accept(matchedUser);
        } else if (matchWildcards && searchTerm.contentEquals("**")) {
            for (final UUID u : ess.getUsers().getAllUserUUIDs()) {
                final User user = ess.getUsers().loadUncachedUser(u);
                if (user != null) {
                    userConsumer.accept(user);
                }
            }
        } else if (matchWildcards && searchTerm.contentEquals("*")) {
            final boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();
            for (final User onlineUser : ess.getOnlineUsers()) {
                if (skipHidden && onlineUser.isHidden(sender.getPlayer()) && onlineUser.isHiddenFrom(sender.getPlayer())) {
                    continue;
                }
                userConsumer.accept(onlineUser);
            }
        } else if (multipleStringMatches) {
            if (searchTerm.trim().length() < 3) {
                throw new PlayerNotFoundException();
            }
            final List<Player> matchedPlayers = server.matchPlayer(searchTerm);
            if (matchedPlayers.isEmpty()) {
                final User matchedUser = getPlayer(server, searchTerm, true, true);
                userConsumer.accept(matchedUser);
            }
            for (final Player matchPlayer : matchedPlayers) {
                final User matchedUser = ess.getUser(matchPlayer);
                userConsumer.accept(matchedUser);
            }
        } else {
            final User user = getPlayer(server, searchTerm, true, true);
            userConsumer.accept(user);
        }
    }

    protected void loopOnlinePlayers(final Server server, final CommandSource sender, final boolean multipleStringMatches, final boolean matchWildcards, final String searchTerm, final String[] commandArgs) throws TranslatableException, NotEnoughArgumentsException {
        loopOnlinePlayersConsumer(server, sender, multipleStringMatches, matchWildcards, searchTerm, user -> updatePlayer(server, sender, user, commandArgs));
    }

    protected void loopOnlinePlayersConsumer(final Server server, final CommandSource sender, final boolean multipleStringMatches, final boolean matchWildcards, final String searchTerm, final UserConsumer userConsumer) throws NotEnoughArgumentsException, TranslatableException {
        if (searchTerm.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        if (sender.isPlayer() && (searchTerm.equals("@s") || searchTerm.equals("@p"))) {
            acceptOnline(sender, (User) sender.getUser(), userConsumer);
            return;
        }

        final boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();

        if (matchWildcards && (searchTerm.contentEquals("**") || searchTerm.contentEquals("*"))) {
            for (final User onlineUser : ess.getOnlineUsers()) {
                if (skipHidden && onlineUser.isHidden(sender.getPlayer()) && onlineUser.isHiddenFrom(sender.getPlayer())) {
                    continue;
                }
                acceptOnline(sender, onlineUser, userConsumer);
            }
        } else if (multipleStringMatches) {
            if (searchTerm.trim().length() < 2) {
                throw new PlayerNotFoundException();
            }
            boolean foundUser = false;
            final List<Player> matchedPlayers = server.matchPlayer(searchTerm);

            if (matchedPlayers.isEmpty()) {
                final String matchText = searchTerm.toLowerCase(Locale.ENGLISH);
                for (final User player : ess.getOnlineUsers()) {
                    if (skipHidden && player.isHidden(sender.getPlayer()) && player.isHiddenFrom(sender.getPlayer())) {
                        continue;
                    }
                    final String displayName = FormatUtil.stripFormat(player.getDisplayName()).toLowerCase(Locale.ENGLISH);
                    if (displayName.contains(matchText)) {
                        foundUser = true;
                        acceptOnline(sender, player, userConsumer);
                    }
                }
            } else {
                for (final Player matchPlayer : matchedPlayers) {
                    final User player = ess.getUser(matchPlayer);
                    if (skipHidden && player.isHidden(sender.getPlayer()) && player.isHiddenFrom(sender.getPlayer())) {
                        continue;
                    }
                    foundUser = true;
                    acceptOnline(sender, player, userConsumer);
                }
            }
            if (!foundUser) {
                throw new PlayerNotFoundException();
            }
        } else {
            final User player = getPlayer(server, sender, searchTerm);
            acceptOnline(sender, player, userConsumer);
        }
    }

    // Changing a player is only allowed from the thread that owns them, which on Folia is not necessarily the sender's.
    // When it is not, the change is made from the player's thread and any error is reported to the sender from there.
    private void acceptOnline(final CommandSource sender, final User user, final UserConsumer userConsumer) throws NotEnoughArgumentsException, TranslatableException {
        if (ess.getTaskScheduler().isOwnedByCurrentThread(user.getBase())) {
            userConsumer.accept(user);
            return;
        }
        ess.getTaskScheduler().runEntity(user.getBase(), () -> {
            try {
                userConsumer.accept(user);
            } catch (final Exception e) {
                showError(sender.getSender(), e, getName());
            }
        });
    }

    protected abstract void updatePlayer(Server server, CommandSource sender, User user, String[] args) throws NotEnoughArgumentsException, PlayerExemptException, ChargeException, MaxMoneyException;

    @Override
    protected List<String> getPlayers(final CommandSource interactor) {
        final List<String> players = super.getPlayers(interactor);
        players.add("**");
        players.add("*");
        return players;
    }

    @Override
    protected List<String> getPlayers(final User interactor) {
        final List<String> players = super.getPlayers(interactor);
        players.add("**");
        players.add("*");
        return players;
    }

    public interface UserConsumer {
        void accept(User user) throws NotEnoughArgumentsException, TranslatableException;
    }
}
