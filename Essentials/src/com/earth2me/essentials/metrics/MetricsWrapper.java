package com.earth2me.essentials.metrics;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.register.payment.Methods;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsWrapper {

    private final Essentials ess;
    private final Metrics metrics;
    private final Map<String, Boolean> commands = new HashMap<>();
    private final Plugin plugin;

    private static boolean hasWarned = false;
    private static final List<String> KNOWN_FORCED_METRICS = ImmutableList.of("ChatControl");

    public MetricsWrapper(Plugin plugin, int pluginId, boolean includeCommands) {
        this.plugin = plugin;
        this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        this.metrics = new Metrics(plugin, pluginId);

        if (metrics.isEnabled()) {
            plugin.getLogger().info("Starting Metrics. Opt-out using the global bStats config.");
        } else {
            plugin.getLogger().info("Metrics disabled per bStats config.");
        }

        checkForcedMetrics();
        addPermsChart();
        addEconomyChart();

        // bStats' backend currently doesn't support multi-line charts or advanced bar charts
        // These are included for when bStats is ready to accept this data
        addVersionHistoryChart();
        if (includeCommands) addCommandsChart();
    }

    public void markCommand(String command, boolean state) {
        commands.put(command, state);
    }

    public void addCustomChart(Metrics.CustomChart chart) {
        metrics.addCustomChart(chart);
    }

    private void addPermsChart() {
        metrics.addCustomChart(new Metrics.DrilldownPie("permsPlugin", () -> {
            Map<String, Map<String, Integer>> result = new HashMap<>();
            String handler = ess.getPermissionsHandler().getName();
            Map<String, Integer> backend = new HashMap<>();
            backend.put(ess.getPermissionsHandler().getBackendName(), 1);
            result.put(handler, backend);
            return result;
        }));
    }

    private void addEconomyChart() {
        metrics.addCustomChart(new Metrics.DrilldownPie("econPlugin", () -> {
            Map<String, Map<String, Integer>> result = new HashMap<>();
            Map<String, Integer> backend = new HashMap<>();
            if (Methods.hasMethod()) {
                backend.put(Methods.getMethod().getBackend(), 1);
                result.put(Methods.getMethod().getName(), backend);
            } else {
                backend.put("Essentials", 1);
                result.put("Essentials", backend);
            }
            return result;
        }));
    }

    private void addVersionHistoryChart() {
        metrics.addCustomChart(new Metrics.MultiLineChart("versionHistory", () -> {
            HashMap<String, Integer> result = new HashMap<>();
            result.put(plugin.getDescription().getVersion(), 1);
            return result;
        }));
    }

    private void addCommandsChart() {
        for (String command : plugin.getDescription().getCommands().keySet()) {
            markCommand(command, false);
        }

        metrics.addCustomChart(new Metrics.AdvancedBarChart("commands", () -> {
            Map<String, int[]> result = new HashMap<>();
            for (Map.Entry<String, Boolean> entry : commands.entrySet()) {
                if (entry.getValue()) {
                    result.put(entry.getKey(), new int[]{1, 0});
                } else {
                    result.put(entry.getKey(), new int[]{0, 1});
                }
            }
            return result;
        }));
    }

    private void checkForcedMetrics() {
        if (hasWarned) return;
        hasWarned = true;

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
                try {
                    service.getField("B_STATS_VERSION"); // Identifies bStats classes

                    JavaPlugin owningPlugin = getProvidingPlugin(service);
                    if (owningPlugin != null && KNOWN_FORCED_METRICS.contains(owningPlugin.getName())) {
                        warnForcedMetrics(service);
                    } else {
                        try {
                            service.getDeclaredField("pluginId"); // Only present in recent bStats classes, which should also have the enabled field unless modified
                        } catch (NoSuchFieldException e) {
                            // Old bStats class found so "enabled" field detection is unreliable.
                            break;
                        }

                        try {
                            service.getDeclaredField("enabled"); // In modified forced metrics classes, this will fail
                        } catch (NoSuchFieldException e) {
                            warnForcedMetrics(service);
                        }
                    }
                } catch (NoSuchFieldException ignored) {}
            }
        });
    }

    private void warnForcedMetrics(Class<?> service) {
        Plugin servicePlugin = JavaPlugin.getProvidingPlugin(service);
        plugin.getLogger().severe("WARNING: Potential forced metrics collection by plugin '" + servicePlugin.getName() + "' v" + servicePlugin.getDescription().getVersion());
        plugin.getLogger().severe("Your server is running a plugin that may not respect bStats' opt-out settings.");
        plugin.getLogger().severe("This may cause data to be uploaded to bStats.org for plugins that use bStats, even if you've opted out in the bStats config.");
        plugin.getLogger().severe("Please report this to bStats and to the authors of '" + servicePlugin.getName() + "'. (Offending class: " + service.getName() + ")");
    }

    private JavaPlugin getProvidingPlugin(Class<?> clazz) {
        try {
            return JavaPlugin.getProvidingPlugin(clazz);
        } catch (Exception ignored) {}

        ClassLoader parent = clazz.getClassLoader().getParent();
        if (parent.getClass().getName().equals("org.bukkit.plugin.java.PluginClassLoader")) {
            try {
                Field pluginField = parent.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                return (JavaPlugin) pluginField.get(parent);
            } catch (Exception ignored) {}
        }

        return null;
    }
}
