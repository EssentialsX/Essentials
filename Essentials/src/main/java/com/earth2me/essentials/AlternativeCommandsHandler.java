package com.earth2me.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlternativeCommandsHandler {
    private static final Logger LOGGER = Logger.getLogger("Essentials");
    private final transient Map<String, List<Command>> altcommands = new HashMap<>();
    private final transient Map<String, String> disabledList = new HashMap<>();
    private final transient IEssentials ess;

    public AlternativeCommandsHandler(final IEssentials ess) {
        this.ess = ess;
        for (final Plugin plugin : ess.getServer().getPluginManager().getPlugins()) {
            if (plugin.isEnabled()) {
                addPlugin(plugin);
            }
        }
    }

    public final void addPlugin(final Plugin plugin) {
        if (plugin.getDescription().getMain().contains("com.earth2me.essentials")) {
            return;
        }
        final List<Command> commands = getPluginCommands(plugin);
        final String pluginName = plugin.getDescription().getName().toLowerCase(Locale.ENGLISH);

        for (final Command command : commands) {
            final List<String> labels = new ArrayList<>(command.getAliases());
            labels.add(command.getName());

            for (final String label : labels) {
                final List<Command> plugincommands = altcommands.computeIfAbsent(label.toLowerCase(Locale.ENGLISH), k -> new ArrayList<>());
                boolean found = false;
                for (final Command pc2 : plugincommands) {
                    if (pc2 instanceof PluginIdentifiableCommand) {
                        if (((PluginIdentifiableCommand) pc2).getPlugin().equals(plugin)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    plugincommands.add(command);
                }
            }
        }
    }

    private List<Command> getPluginCommands(Plugin plugin) {
        final List<Command> commands = new ArrayList<>();
        for (Command cmd : ess.getKnownCommandsProvider().getKnownCommands().values()) {
            if (cmd instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand) cmd).getPlugin().getName().equals(plugin.getName())) {
                commands.add(cmd);
            }
        }
        return commands;
    }

    public void removePlugin(final Plugin plugin) {
        final Iterator<Map.Entry<String, List<Command>>> iterator = altcommands.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, List<Command>> entry = iterator.next();
            entry.getValue().removeIf(pc -> !(pc instanceof PluginIdentifiableCommand) || ((PluginIdentifiableCommand) pc).getPlugin().equals(plugin));
            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    public Command getAlternative(final String label) {
        final List<Command> commands = altcommands.get(label);
        if (commands == null || commands.isEmpty()) {
            return null;
        }
        if (commands.size() == 1) {
            return commands.get(0);
        }
        // return the first command that is not an alias
        for (final Command command : commands) {
            if (command.getName().equalsIgnoreCase(label)) {
                return command;
            }
        }
        // return the first alias
        return commands.get(0);
    }

    public void executed(final String label, final Command pc) {
        if (pc instanceof PluginIdentifiableCommand) {
            final String altString = ((PluginIdentifiableCommand) pc).getPlugin().getName() + ":" + pc.getLabel();
            if (ess.getSettings().isDebug()) {
                LOGGER.log(Level.INFO, "Essentials: Alternative command " + label + " found, using " + altString);
            }
            disabledList.put(label, altString);
        }
    }

    public Map<String, String> disabledCommands() {
        return disabledList;
    }
}
