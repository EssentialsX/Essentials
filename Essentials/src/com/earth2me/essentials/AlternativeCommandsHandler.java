package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AlternativeCommandsHandler {
    private static final Logger LOGGER = Logger.getLogger("Essentials");
    private final transient Map<String, List<PluginCommand>> altcommands = new HashMap<>();
    private final transient Map<String, String> disabledList = new HashMap<>();
    private final transient IEssentials ess;

    public AlternativeCommandsHandler(final IEssentials ess) {
        this.ess = ess;
        for (Plugin plugin : ess.getServer().getPluginManager().getPlugins()) {
            if (plugin.isEnabled()) {
                addPlugin(plugin);
            }
        }
    }

    public final void addPlugin(final Plugin plugin) {
        if (plugin.getDescription().getMain().contains("com.earth2me.essentials")) {
            return;
        }
        final List<Command> commands = PluginCommandYamlParser.parse(plugin);
        final String pluginName = plugin.getDescription().getName().toLowerCase(Locale.ENGLISH);

        for (Command command : commands) {
            final PluginCommand pc = (PluginCommand) command;
            final List<String> labels = new ArrayList<>(pc.getAliases());
            labels.add(pc.getName());

            PluginCommand reg = ess.getServer().getPluginCommand(pluginName + ":" + pc.getName().toLowerCase(Locale.ENGLISH));
            if (reg == null) {
                reg = ess.getServer().getPluginCommand(pc.getName().toLowerCase(Locale.ENGLISH));
            }
            if (reg == null || !reg.getPlugin().equals(plugin)) {
                continue;
            }
            for (String label : labels) {
                List<PluginCommand> plugincommands = altcommands.computeIfAbsent(label.toLowerCase(Locale.ENGLISH), k -> new ArrayList<>());
                boolean found = false;
                for (PluginCommand pc2 : plugincommands) {
                    if (pc2.getPlugin().equals(plugin)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    plugincommands.add(reg);
                }
            }
        }
    }

    public void removePlugin(final Plugin plugin) {
        final Iterator<Map.Entry<String, List<PluginCommand>>> iterator = altcommands.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, List<PluginCommand>> entry = iterator.next();
            entry.getValue().removeIf(pc -> pc.getPlugin() == null || pc.getPlugin().equals(plugin));
            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    public PluginCommand getAlternative(final String label) {
        final List<PluginCommand> commands = altcommands.get(label);
        if (commands == null || commands.isEmpty()) {
            return null;
        }
        if (commands.size() == 1) {
            return commands.get(0);
        }
        // return the first command that is not an alias
        for (PluginCommand command : commands) {
            if (command.getName().equalsIgnoreCase(label)) {
                return command;
            }
        }
        // return the first alias
        return commands.get(0);
    }

    public void executed(final String label, final PluginCommand pc) {
        final String altString = pc.getPlugin().getName() + ":" + pc.getLabel();
        if (ess.getSettings().isDebug()) {
            LOGGER.log(Level.INFO, "Essentials: Alternative command " + label + " found, using " + altString);
        }
        disabledList.put(label, altString);
    }

    public Map<String, String> disabledCommands() {
        return disabledList;
    }
}
