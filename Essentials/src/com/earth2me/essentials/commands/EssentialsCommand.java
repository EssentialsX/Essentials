package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.ess3.api.IEssentials;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public abstract class EssentialsCommand implements IEssentialsCommand {
    protected static final Logger logger = Logger.getLogger("Essentials");
    /**
     * Common time durations (in seconds), for use in tab completion.
     */
    protected static final List<String> COMMON_DURATIONS = ImmutableList.of("1", "60", "600", "3600", "86400");
    /**
     * Common date diffs, for use in tab completion
     */
    protected static final List<String> COMMON_DATE_DIFFS = ImmutableList.of("1m", "15m", "1h", "3h", "12h", "1d", "1w", "1mo", "1y");
    private final transient String name;
    protected transient IEssentials ess;
    protected transient IEssentialsModule module;

    protected EssentialsCommand(final String name) {
        this.name = name;
    }

    public static String getFinalArg(final String[] args, final int start) {
        final StringBuilder bldr = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                bldr.append(" ");
            }
            bldr.append(args[i]);
        }
        return bldr.toString();
    }

    private static boolean canInteractWith(final User interactor, final User interactee) {
        if (interactor == null) {
            return !interactee.isHidden();
        }

        if (interactor.equals(interactee)) {
            return true;
        }

        return interactor.getBase().canSee(interactee.getBase());
    }

    @Override
    public void setEssentials(final IEssentials ess) {
        this.ess = ess;
    }

    @Override
    public void setEssentialsModule(final IEssentialsModule module) {
        this.module = module;
    }

    @Override
    public String getName() {
        return name;
    }

    // Get online players - only show vanished if source has permission
    protected User getPlayer(final Server server, final CommandSource sender, final String[] args, final int pos) throws PlayerNotFoundException, NotEnoughArgumentsException {
        if (sender.isPlayer()) {
            final User user = ess.getUser(sender.getPlayer());
            return getPlayer(server, user, args, pos);
        }
        return getPlayer(server, args, pos, true, false);
    }

    // Get online players - only show vanished if source has permission
    protected User getPlayer(final Server server, final CommandSource sender, final String searchTerm) throws PlayerNotFoundException, NotEnoughArgumentsException {
        if (sender.isPlayer()) {
            final User user = ess.getUser(sender.getPlayer());
            return getPlayer(server, user, searchTerm, user.canInteractVanished(), false);
        }
        return getPlayer(server, searchTerm, true, false);
    }

    // Get online players - only show vanished if source has permission
    protected User getPlayer(final Server server, final User user, final String[] args, final int pos) throws PlayerNotFoundException, NotEnoughArgumentsException {
        return getPlayer(server, user, args, pos, user.canInteractVanished(), false);
    }

    // Get online or offline players, this method allows for raw access
    protected User getPlayer(final Server server, final String[] args, final int pos, final boolean getHidden, final boolean getOffline) throws PlayerNotFoundException, NotEnoughArgumentsException {
        return getPlayer(server, null, args, pos, getHidden, getOffline);
    }

    User getPlayer(final Server server, final User sourceUser, final String[] args, final int pos, final boolean getHidden, final boolean getOffline) throws PlayerNotFoundException, NotEnoughArgumentsException {
        if (args.length <= pos) {
            throw new NotEnoughArgumentsException();
        }
        if (args[pos].isEmpty()) {
            throw new PlayerNotFoundException();
        }
        return getPlayer(server, sourceUser, args[pos], getHidden, getOffline);
    }

    // Get online or offline players, this method allows for raw access
    protected User getPlayer(final Server server, final String searchTerm, final boolean getHidden, final boolean getOffline) throws PlayerNotFoundException {
        return getPlayer(server, null, searchTerm, getHidden, getOffline);
    }

    private User getPlayer(final Server server, final User sourceUser, final String searchTerm, final boolean getHidden, final boolean getOffline) throws PlayerNotFoundException {
        final User user;
        Player exPlayer;

        try {
            exPlayer = server.getPlayer(UUID.fromString(searchTerm));
        } catch (final IllegalArgumentException ex) {
            if (getOffline) {
                exPlayer = server.getPlayerExact(searchTerm);
            } else {
                exPlayer = server.getPlayer(searchTerm);
            }
        }

        if (exPlayer != null) {
            user = ess.getUser(exPlayer);
        } else {
            user = ess.getUser(searchTerm);
        }

        if (user != null) {
            if (!getOffline && !user.getBase().isOnline()) {
                throw new PlayerNotFoundException();
            }

            if (getHidden || canInteractWith(sourceUser, user)) {
                return user;
            } else { // not looking for hidden and cannot interact (i.e is hidden)
                if (getOffline && user.getName().equalsIgnoreCase(searchTerm)) { // if looking for offline and got an exact match
                    return user;
                }
            }
            throw new PlayerNotFoundException();
        }
        final List<Player> matches = server.matchPlayer(searchTerm);

        if (matches.isEmpty()) {
            final String matchText = searchTerm.toLowerCase(Locale.ENGLISH);
            for (final User userMatch : ess.getOnlineUsers()) {
                if (getHidden || canInteractWith(sourceUser, userMatch)) {
                    final String displayName = FormatUtil.stripFormat(userMatch.getDisplayName()).toLowerCase(Locale.ENGLISH);
                    if (displayName.contains(matchText)) {
                        return userMatch;
                    }
                }
            }
        } else {
            for (final Player player : matches) {
                final User userMatch = ess.getUser(player);
                if (userMatch.getDisplayName().startsWith(searchTerm) && (getHidden || canInteractWith(sourceUser, userMatch))) {
                    return userMatch;
                }
            }
            final User userMatch = ess.getUser(matches.get(0));
            if (getHidden || canInteractWith(sourceUser, userMatch)) {
                return userMatch;
            }
        }
        throw new PlayerNotFoundException();
    }

    @Override
    public final void run(final Server server, final User user, final String commandLabel, final Command cmd, final String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        run(server, user, commandLabel, args);
        charge.charge(user);
    }

    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        run(server, user.getSource(), commandLabel, args);
    }

    @Override
    public final void run(final Server server, final CommandSource sender, final String commandLabel, final Command cmd, final String[] args) throws Exception {
        run(server, sender, commandLabel, args);
    }

    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        throw new Exception(tl("onlyPlayers", commandLabel));
    }

    @Override
    public final List<String> tabComplete(final Server server, final User user, final String commandLabel, final Command cmd, final String[] args) {
        if (args.length == 0) {
            // Shouldn't happen, but bail out early if it does so that args[0] can always be used
            return Collections.emptyList();
        }
        final List<String> options = getTabCompleteOptions(server, user, commandLabel, args);
        if (options == null) {
            return null;
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], options, Lists.newArrayList());
    }

    // Doesn't need to do any starts-with checks
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        return getTabCompleteOptions(server, user.getSource(), commandLabel, args);
    }

    @Override
    public final List<String> tabComplete(final Server server, final CommandSource sender, final String commandLabel, final Command cmd, final String[] args) {
        if (args.length == 0) {
            // Shouldn't happen, but bail out early if it does so that args[0] can always be used
            return Collections.emptyList();
        }
        final List<String> options = getTabCompleteOptions(server, sender, commandLabel, args);
        if (options == null) {
            return null;
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], options, Lists.newArrayList());
    }

    // Doesn't need to do any starts-with checks
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        // No tab completion results
        return getPlayers(server, sender);
    }

    boolean canInteractWith(final CommandSource interactor, final User interactee) {
        if (interactor == null) {
            return !interactee.isHidden();
        }

        if (interactor.isPlayer()) {
            return canInteractWith(ess.getUser(interactor.getPlayer()), interactee);
        }

        return true; // console
    }

    /**
     * Gets a list of all player names that can be seen with by the given CommandSource,
     * for tab completion.
     */
    protected List<String> getPlayers(final Server server, final CommandSource interactor) {
        final List<String> players = Lists.newArrayList();
        for (final User user : ess.getOnlineUsers()) {
            if (canInteractWith(interactor, user)) {
                players.add(user.getName());
            }
        }
        return players;
    }

    /**
     * Gets a list of all player names that can be seen with by the given User,
     * for tab completion.
     */
    protected List<String> getPlayers(final Server server, final User interactor) {
        final List<String> players = Lists.newArrayList();
        for (final User user : ess.getOnlineUsers()) {
            if (canInteractWith(interactor, user)) {
                players.add(user.getName());
            }
        }
        return players;
    }

    /**
     * Gets a list of tab-completable items that start with the given name.
     * Due to the number of items, this may not return the entire list.
     */
    protected List<String> getItems() {
        return new ArrayList<>(ess.getItemDb().listNames());
    }

    /**
     * Gets a list of tab-completable items usable for "getMatching".
     */
    protected List<String> getMatchingItems(final String arg) {
        final List<String> items = Lists.newArrayList("hand", "inventory", "blocks");
        if (!arg.isEmpty()) {
            // Emphasize the other items if they haven't entered anything yet.
            items.addAll(getItems());
        }
        return items;
    }

    /**
     * Lists all commands.
     */
    protected final List<String> getCommands(Server server) {
        final Map<String, Command> commandMap = Maps.newHashMap(this.ess.getKnownCommandsProvider().getKnownCommands());
        final List<String> commands = Lists.newArrayListWithCapacity(commandMap.size());
        for (final Command command : commandMap.values()) {
            if (!(command instanceof PluginIdentifiableCommand)) {
                continue;
            }
            commands.add(command.getName());
        }
        return commands;
    }

    /**
     * Lists all plugin names
     *
     * @param server Server instance
     * @return List of plugin names
     */
    protected final List<String> getPlugins(final Server server) {
        final List<String> plugins = Lists.newArrayList();
        for (final Plugin p : server.getPluginManager().getPlugins()) {
            plugins.add(p.getName());
        }
        return plugins;
    }

    /**
     * Attempts to tab-complete a command or its arguments.
     */
    protected final List<String> tabCompleteCommand(final CommandSource sender, final Server server, final String label, final String[] args, final int index) {
        // TODO: Pass this to the real commandmap
        final Command command = server.getPluginCommand(label);
        if (command == null) {
            return Collections.emptyList();
        }

        final int numArgs = args.length - index - 1;
        if (ess.getSettings().isDebug()) {
            ess.getLogger().info(numArgs + " " + index + " " + Arrays.toString(args));
        }
        String[] effectiveArgs = new String[numArgs];
        System.arraycopy(args, index, effectiveArgs, 0, numArgs);
        if (effectiveArgs.length == 0) {
            effectiveArgs = new String[] {""};
        }
        if (ess.getSettings().isDebug()) {
            ess.getLogger().info(command + " -- " + Arrays.toString(effectiveArgs));
        }

        return command.tabComplete(sender.getSender(), label, effectiveArgs);
    }

    @Override
    public void showError(final CommandSender sender, final Throwable throwable, final String commandLabel) {
        sender.sendMessage(tl("errorWithMessage", throwable.getMessage()));
        if (ess.getSettings().isDebug()) {
            logger.log(Level.INFO, tl("errorCallingCommand", commandLabel), throwable);
            throwable.printStackTrace();
        }
    }

    public CompletableFuture<Boolean> getNewExceptionFuture(final CommandSource sender, final String commandLabel) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.exceptionally(e -> {
            showError(sender.getSender(), e, commandLabel);
            return false;
        });
        return future;
    }
}
