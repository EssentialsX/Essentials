package com.earth2me.essentials.textreader;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;


public class HelpInput implements IText {

    private final IEssentials ess;
    private static final Logger LOGGER = Logger.getLogger("Essentials");
    private final transient List<String> lines = new ArrayList<>();
    private final transient List<String> chapters = new ArrayList<>();
    private final transient Map<String, Integer> bookmarks = new HashMap<>();

    public HelpInput(final User user, final String match, IEssentials ess) throws IOException {

        List<String> newLines = new ArrayList<>();
        boolean reported = false;
        this.ess = ess;

        String pluginNameLow = "";
        String pluginName;

        if (!match.equalsIgnoreCase("")) {
            lines.add(tl("helpMatching", match));
        }

        for (Plugin plugin : ess.getServer().getPluginManager().getPlugins()) {
            try {
                final Set<String> commands = getPluginCommands(plugin);
                final List<String> pluginLines = new ArrayList<>();
                pluginName = plugin.getDescription().getName();
                pluginNameLow = pluginName.toLowerCase(Locale.ENGLISH);

                //If the plugin name matches the match term get all commands from this plugin
                if (pluginNameLow.equals(match)) {
                    lines.clear();
                    newLines.clear();
                    lines.add(tl("helpFrom", pluginName));
                }
                //Whether the user can view command help from the commands in the given plugin
                final boolean isOnWhitelist = user.isAuthorized("essentials.help." + pluginNameLow);

                for (String c : commands) {
                    Command command = ess.getCommandMap().getCommand(c);
                    /*
					IF:
					- Match doesn't equal an empty string.
					- Plugin name (lowercase) doesn't contain the match.
					- Command name (lowercase) contains the match.
					- The command description exists.
					- The command description contains the match.
					 */
                    if (!match.equalsIgnoreCase("") && (!pluginNameLow.contains(match)) && (!command.getName().toLowerCase(Locale.ENGLISH).contains(match)) && (!(command.getDescription() != null && command.getDescription().toLowerCase(Locale.ENGLISH).contains(match))))
                        continue;
                    if (pluginNameLow.contains("essentials")) {
                        final String node = "essentials." + command.getName();
						/*
						IF:
						- The command isn't disabled
						- The user is authorized for the command
						 */
                        if (!ess.getSettings().isCommandDisabled(command.getName()) && user.isAuthorized(node)) {
                            pluginLines.add(tl("helpLine", command.getName(), command.getDescription()));
                        }
                    } else {
                        //If we're going to show non essentials commands we need to add them to the lines
                        if (ess.getSettings().showNonEssCommandsInHelp()) {

                            //Checks if the user has permission to see commands from this plugin and checks to see if the user has permission to view them.
                            if (isOnWhitelist || user.isAuthorized("essentials.help." + pluginNameLow + "." + command.getName())) {
                                pluginLines.add(tl("helpLine", command.getName(), command.getDescription()));
                                continue;
                            }

                            String permission;

                            //Checks if the command has a permission node attached to it or not
                            if (command.getPermission() != null) {
                                permission = command.getPermission();
                                if (!permission.equals("")) {
                                    pluginLines.add(tl("helpLine", command.getName(), command.getDescription()));
                                }
                            } else {
                                if (!ess.getSettings().hidePermissionlessHelp()) {
                                    pluginLines.add(tl("helpLine", command.getName(), command.getDescription()));
                                }
                            }
                        }
                    }
                }
				/*
				Before we add the plugin name to the help menu, we need to check if the plugin even has commands. The
				algorithm above gets any commands from the plugin and adds it to the pluginLines list. Here, we check if
				the pluginLines list is empty or not. If it isn't empty, we add the plugin to the main help menu.
				 */
                if (!pluginLines.isEmpty()) {
                    newLines.addAll(pluginLines);
                    if (pluginNameLow.equals(match)) {
                        break;
                    }
                    if (match.equalsIgnoreCase("")) {
                        lines.add(tl("helpPlugin", pluginName, pluginNameLow));
                    }
                }
            } catch (Exception e) {
                if (!reported) {
                    LOGGER.log(Level.WARNING, tl("commandHelpFailedForPlugin", pluginNameLow), e);
                }
                reported = true;
            }
        }
        lines.addAll(newLines);
    }

    private Set<String> getPluginCommands(Plugin plugin) {

        final Set<String> commands = new HashSet<>();
        // Checks if the command is a PluginIdentifiableCommand,
        // checks if the command's plugin equals the supplied plugin,
        // and checks if the commands set doesn't contain this command
        // already. If all of these are true, we add the command to
        // the commands set
        ((SimpleCommandMap) ess.getCommandMap()).getCommands().stream().filter(command -> (command instanceof PluginIdentifiableCommand) && ((PluginIdentifiableCommand) command).getPlugin().equals(plugin) && (!commands.contains(command.getName()))).forEach(command -> commands.add(command.getName()));
        return commands;
    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public List<String> getChapters() {
        return chapters;
    }

    @Override
    public Map<String, Integer> getBookmarks() {
        return bookmarks;
    }
}
