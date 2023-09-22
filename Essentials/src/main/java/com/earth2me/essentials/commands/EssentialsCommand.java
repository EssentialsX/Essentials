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
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;

public abstract class EssentialsCommand implements IEssentialsCommand {
    /**
     * Common time durations (in seconds), for use in tab completion.
     */
    protected static final List<String> COMMON_DURATIONS = ImmutableList.of("1", "60", "600", "3600", "86400");
    /**
     * Common date diffs, for use in tab completion
     */
    protected static final List<String> COMMON_DATE_DIFFS = ImmutableList.of("1m", "15m", "1h", "3h", "12h", "1d", "1w", "1mo", "1y");
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("([ :>])(([\\[<])[A-Za-z |]+[>\\]])");

    private final transient String name;
    private final transient Map<String, String> usageStrings = new LinkedHashMap<>();
    protected transient IEssentials ess;
    protected transient IEssentialsModule module;

    protected EssentialsCommand(final String name) {
        this.name = name;
        int i = 1;
        try {
            // This is not actually infinite, it will throw an unchecked exception if a resource key is missing
            //noinspection InfiniteLoopStatement
            while (true) {
                final String baseKey = name + "CommandUsage" + i;
                addUsageString(tl(baseKey), tl(baseKey + "Description"));
                i++;
            }
        } catch (MissingResourceException ignored) {
        }
    }

    private void addUsageString(final String usage, final String description) {
        final StringBuffer buffer = new StringBuffer();
        final Matcher matcher = ARGUMENT_PATTERN.matcher(usage);
        while (matcher.find()) {
            final String color = matcher.group(3).equals("<") ? tl("commandArgumentRequired") : tl("commandArgumentOptional");
            matcher.appendReplacement(buffer, "$1" + color + matcher.group(2).replace("|", tl("commandArgumentOr") + "|" + color) + ChatColor.RESET);
        }
        matcher.appendTail(buffer);
        usageStrings.put(buffer.toString(), description);
    }

    @Override
    public Map<String, String> getUsageStrings() {
        return usageStrings;
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

    private boolean canInteractWith(final User interactor, final User interactee) {
        return ess.canInteractWith(interactor, interactee);
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
        return getPlayer(server, sender, args, pos, false);
    }

    protected User getPlayer(final Server server, final CommandSource sender, final String[] args, final int pos, final boolean getOffline) throws PlayerNotFoundException, NotEnoughArgumentsException {
        if (sender.isPlayer()) {
            final User user = ess.getUser(sender.getPlayer());
            return getPlayer(server, user, args, pos, getOffline);
        }
        return getPlayer(server, args, pos, true, getOffline);
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
        return getPlayer(server, user, args, pos, false);
    }

    protected User getPlayer(final Server server, final User user, final String[] args, final int pos, final boolean getOffline) throws PlayerNotFoundException, NotEnoughArgumentsException {
        return getPlayer(server, user, args, pos, user.canInteractVanished(), getOffline);
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
        return ess.matchUser(server, sourceUser, searchTerm, getHidden, getOffline);
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
        return ess.canInteractWith(interactor, interactee);
    }

    /**
     * Gets a list of all player names that can be seen with by the given CommandSource,
     * for tab completion.
     */
    protected List<String> getPlayers(final Server server, final CommandSource interactor) {
        final List<String> players = Lists.newArrayList();
        for (final User user : ess.getOnlineUsers()) {
            if (canInteractWith(interactor, user)) {
                players.add(ess.getSettings().changeTabCompleteName() ? FormatUtil.stripFormat(user.getName()) : user.getName());
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
                players.add(ess.getSettings().changeTabCompleteName() ? FormatUtil.stripFormat(user.getName()) : user.getName());
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
            ess.getLogger().log(Level.INFO, tl("errorCallingCommand", commandLabel), throwable);
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
