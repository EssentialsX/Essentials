package com.earth2me.essentials;

import net.ess3.provider.KnownCommandsProvider;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class AlternativeCommandsHandler {
    private final transient Map<String, List<WeakReference<Command>>> altCommands = new HashMap<>();
    private final transient Map<String, String> disabledList = new HashMap<>();
    private final transient IEssentials ess;

    public AlternativeCommandsHandler(final IEssentials ess) {
        this.ess = ess;
        addPlugins(ess.getServer().getPluginManager().getPlugins());
    }

    public final void addPlugins(final Plugin[] plugins) {
        // Build a set of plugins we care about for fast lookup
        final Map<Plugin, List<Map.Entry<String, Command>>> byPlugin = new HashMap<>();
        for (final Plugin plugin : plugins) {
            if (!plugin.isEnabled()) continue;
            if (plugin.getDescription().getMain().contains("com.earth2me.essentials") || plugin.getDescription().getMain().contains("net.essentialsx")) continue;
            byPlugin.put(plugin, null); // placeholder so we can check containsKey cheaply
        }

        if (byPlugin.isEmpty()) return;

        // Single pass over the command map
        for (final Map.Entry<String, Command> entry : ess.provider(KnownCommandsProvider.class).getKnownCommands().entrySet()) {
            if (!(entry.getValue() instanceof PluginIdentifiableCommand)) continue;
            final Plugin owner = ((PluginIdentifiableCommand) entry.getValue()).getPlugin();
            if (!byPlugin.containsKey(owner)) continue;

            List<Map.Entry<String, Command>> list = byPlugin.get(owner);
            if (list == null) {
                list = new ArrayList<>();
                byPlugin.put(owner, list);
            }
            list.add(entry);
        }

        for (final Map.Entry<Plugin, List<Map.Entry<String, Command>>> pluginEntry : byPlugin.entrySet()) {
            final List<Map.Entry<String, Command>> cmds = pluginEntry.getValue();
            if (cmds == null) continue;
            registerPluginCommands(pluginEntry.getKey(), cmds);
        }
    }

    public final void addPlugin(final Plugin plugin) {
        if (plugin.getDescription().getMain().contains("com.earth2me.essentials") || plugin.getDescription().getMain().contains("net.essentialsx")) {
            return;
        }
        registerPluginCommands(plugin, getPluginCommands(plugin));
    }

    private void registerPluginCommands(final Plugin plugin, final List<Map.Entry<String, Command>> entries) {
        // Sort: non-namespaced first
        final List<Map.Entry<String, Command>> plain = new ArrayList<>();
        final List<Map.Entry<String, Command>> namespaced = new ArrayList<>();
        for (final Map.Entry<String, Command> entry : entries) {
            if (entry.getKey().indexOf(':') >= 0) {
                namespaced.add(entry);
            } else {
                plain.add(entry);
            }
        }
        plain.addAll(namespaced);

        for (final Map.Entry<String, Command> entry : plain) {
            final String[] commandSplit = entry.getKey().split(":", 2);
            final String commandName = commandSplit.length > 1 ? commandSplit[1] : entry.getKey();
            final Command command = entry.getValue();

            final List<WeakReference<Command>> pluginCommands = altCommands.computeIfAbsent(commandName.toLowerCase(Locale.ENGLISH), k -> new ArrayList<>());
            boolean found = false;

            final Iterator<WeakReference<Command>> pluginCmdIterator = pluginCommands.iterator();
            while (pluginCmdIterator.hasNext()) {
                final Command cmd = pluginCmdIterator.next().get();
                if (cmd == null) {
                    if (ess.getSettings().isDebug()) {
                        ess.getLogger().log(Level.INFO, "Essentials: Alternative command for " + commandName + " removed due to garbage collection");
                    }

                    pluginCmdIterator.remove();
                    continue;
                }

                // Safe cast, everything that's added comes from getPluginCommands which already performs the cast check.
                if (((PluginIdentifiableCommand) cmd).getPlugin().equals(plugin)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                pluginCommands.add(new WeakReference<>(command));
            }
        }
    }

    private List<Map.Entry<String, Command>> getPluginCommands(Plugin plugin) {
        final List<Map.Entry<String, Command>> result = new ArrayList<>();
        for (final Map.Entry<String, Command> entry : ess.provider(KnownCommandsProvider.class).getKnownCommands().entrySet()) {
            if (entry.getValue() instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand) entry.getValue()).getPlugin().equals(plugin)) {
                result.add(entry);
            }
        }
        return result;
    }

    public void removePlugin(final Plugin plugin) {
        final Iterator<Map.Entry<String, List<WeakReference<Command>>>> iterator = altCommands.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, List<WeakReference<Command>>> entry = iterator.next();

            final Iterator<WeakReference<Command>> commands = entry.getValue().iterator();
            while (commands.hasNext()) {
                final Command pc = commands.next().get();
                if (pc instanceof PluginIdentifiableCommand && !((PluginIdentifiableCommand) pc).getPlugin().equals(plugin)) {
                    continue;
                }
                commands.remove();
            }

            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    public Command getAlternative(final String label) {
        final List<WeakReference<Command>> commands = altCommands.get(label);
        if (commands == null || commands.isEmpty()) {
            return null;
        }

        if (commands.size() == 1) {
            return commands.get(0).get();
        }

        // return the first command that is not an alias
        final Iterator<WeakReference<Command>> iterator = commands.iterator();
        while (iterator.hasNext()) {
            final Command cmd = iterator.next().get();
            if (cmd == null) {
                iterator.remove();
                continue;
            }

            if (cmd.getName().equalsIgnoreCase(label)) {
                return cmd;
            }
        }

        // return the first alias
        return commands.get(0).get();
    }

    public void executed(final String label, final Command pc) {
        if (pc instanceof PluginIdentifiableCommand) {
            final String altString = ((PluginIdentifiableCommand) pc).getPlugin().getName() + ":" + pc.getName();
            if (ess.getSettings().isDebug()) {
                ess.getLogger().log(Level.INFO, "Essentials: Alternative command " + label + " found, using " + altString);
            }
            disabledList.put(label, altString);
        }
    }

    public Map<String, String> disabledCommands() {
        return disabledList;
    }
}
