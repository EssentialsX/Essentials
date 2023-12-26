package com.earth2me.essentials.textreader;

import com.earth2me.essentials.User;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.ess3.api.IEssentials;
import net.ess3.provider.KnownCommandsProvider;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class HelpInput implements IText {
    private final transient List<String> lines = new ArrayList<>();
    private final transient List<String> chapters = new ArrayList<>();
    private final transient Map<String, Integer> bookmarks = new HashMap<>();

    public HelpInput(final User user, final String match, final IEssentials ess) {
        boolean reported = false;
        final List<String> newLines = new ArrayList<>();
        String pluginName = "";
        String pluginNameLow = "";
        if (!match.equalsIgnoreCase("")) {
            lines.add(tl("helpMatching", match));
        }

        final Multimap<Plugin, Command> pluginCommands = HashMultimap.create();
        for (final Command command : ess.provider(KnownCommandsProvider.class).getKnownCommands().values()) {
            if (!(command instanceof PluginIdentifiableCommand)) {
                continue;
            }
            final PluginIdentifiableCommand pluginIdentifiableCommand = (PluginIdentifiableCommand) command;
            pluginCommands.put(pluginIdentifiableCommand.getPlugin(), command);
        }

        for (Plugin p : ess.getServer().getPluginManager().getPlugins()) {
            try {
                final List<String> pluginLines = new ArrayList<>();
                final PluginDescriptionFile desc = p.getDescription();
                final Map<String, Map<String, Object>> cmds = desc.getCommands();
                pluginName = p.getDescription().getName();
                pluginNameLow = pluginName.toLowerCase(Locale.ENGLISH);
                if (pluginNameLow.equals(match)) {
                    lines.clear();
                    newLines.clear();
                    lines.add(tl("helpFrom", p.getDescription().getName()));
                }
                final boolean isOnWhitelist = user.isAuthorized("essentials.help." + pluginNameLow);

                for (final Command command : pluginCommands.get(p)) {
                    try {
                        final String commandName = command.getName();
                        final String commandDescription = command.getDescription();

                        if (!match.equalsIgnoreCase("")
                                && (!pluginNameLow.contains(match))
                                && (!commandName.toLowerCase(Locale.ENGLISH).contains(match))
                                && (!commandDescription.toLowerCase(Locale.ENGLISH).contains(match))) {
                            continue;
                        }

                        if (pluginNameLow.contains("essentials")) {
                            final String node = "essentials." + commandName;
                            if (!ess.getSettings().isCommandDisabled(commandName) && user.isAuthorized(node)) {
                                pluginLines.add(tl("helpLine", commandName, commandDescription));
                            }
                        } else {
                            if (ess.getSettings().showNonEssCommandsInHelp()) {
                                final String permissionRaw = command.getPermission();
                                final String[] permissions;
                                if (permissionRaw == null) {
                                    permissions = new String[0];
                                } else {
                                    permissions = permissionRaw.split(";");
                                }

                                if (isOnWhitelist || user.isAuthorized("essentials.help." + pluginNameLow + "." + commandName)) {
                                    pluginLines.add(tl("helpLine", commandName, commandDescription));
                                } else if (permissions.length != 0) {
                                    boolean enabled = false;

                                    for (final String permission : permissions) {
                                        if (user.isAuthorized(permission)) {
                                            enabled = true;
                                            break;
                                        }
                                    }

                                    if (enabled) {
                                        pluginLines.add(tl("helpLine", commandName, commandDescription));
                                    }
                                } else {
                                    if (!ess.getSettings().hidePermissionlessHelp()) {
                                        pluginLines.add(tl("helpLine", commandName, commandDescription));
                                    }
                                }
                            }
                        }
                    } catch (final NullPointerException ignored) {
                    }
                }
                if (!pluginLines.isEmpty()) {
                    newLines.addAll(pluginLines);
                    if (pluginNameLow.equals(match)) {
                        break;
                    }
                    if (match.equalsIgnoreCase("")) {
                        lines.add(tl("helpPlugin", pluginName, pluginNameLow));
                    }
                }
            } catch (final NullPointerException ignored) {
            } catch (final Exception ex) {
                if (!reported) {
                    ess.getLogger().log(Level.WARNING, tl("commandHelpFailedForPlugin", pluginNameLow), ex);
                }
                reported = true;
            }
        }
        lines.addAll(newLines);
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
