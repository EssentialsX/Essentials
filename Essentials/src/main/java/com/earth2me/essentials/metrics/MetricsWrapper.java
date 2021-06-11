package com.earth2me.essentials.metrics;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.economy.EconomyLayer;
import com.earth2me.essentials.economy.EconomyLayers;
import com.google.common.collect.ImmutableList;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsWrapper {

    private static final List<String> KNOWN_FORCED_METRICS = ImmutableList.of(
            "ChatControl",
            "catserver.server.Metrics");
    private static boolean hasWarned = false;
    private final Essentials ess;
    private final Metrics metrics;
    private final Map<String, Boolean> commands = new HashMap<>();
    private final Plugin plugin;

    public MetricsWrapper(final Plugin plugin, final int pluginId, final boolean includeCommands) {
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
        addReleaseBranchChart();

        // bStats' backend currently doesn't support multi-line charts or advanced bar charts
        // These are included for when bStats is ready to accept this data
        addVersionHistoryChart();
        if (includeCommands) addCommandsChart();
    }

    public void markCommand(final String command, final boolean state) {
        commands.put(command, state);
    }

    public void addCustomChart(final Metrics.CustomChart chart) {
        metrics.addCustomChart(chart);
    }

    private void addPermsChart() {
        metrics.addCustomChart(new Metrics.DrilldownPie("permsPlugin", () -> {
            final Map<String, Map<String, Integer>> result = new HashMap<>();
            final String handler = ess.getPermissionsHandler().getName();
            final Map<String, Integer> backend = new HashMap<>();
            backend.put(ess.getPermissionsHandler().getBackendName(), 1);
            result.put(handler, backend);
            return result;
        }));
    }

    private void addEconomyChart() {
        metrics.addCustomChart(new Metrics.DrilldownPie("econPlugin", () -> {
            final Map<String, Map<String, Integer>> result = new HashMap<>();
            final Map<String, Integer> backend = new HashMap<>();
            final EconomyLayer layer = EconomyLayers.getSelectedLayer();
            if (layer != null) {
                backend.put(layer.getBackendName(), 1);
                result.put(layer.getPluginName(), backend);
            } else {
                backend.put("Essentials", 1);
                result.put("Essentials", backend);
            }
            return result;
        }));
    }

    private void addVersionHistoryChart() {
        metrics.addCustomChart(new Metrics.MultiLineChart("versionHistory", () -> {
            final HashMap<String, Integer> result = new HashMap<>();
            result.put(plugin.getDescription().getVersion(), 1);
            return result;
        }));
    }

    private void addReleaseBranchChart() {
        metrics.addCustomChart(new Metrics.SimplePie("releaseBranch", ess.getUpdateChecker()::getVersionBranch));
    }

    private void addCommandsChart() {
        for (final String command : plugin.getDescription().getCommands().keySet()) {
            markCommand(command, false);
        }

        metrics.addCustomChart(new Metrics.AdvancedBarChart("commands", () -> {
            final Map<String, int[]> result = new HashMap<>();
            for (final Map.Entry<String, Boolean> entry : commands.entrySet()) {
                if (entry.getValue()) {
                    result.put(entry.getKey(), new int[]{1, 0});
                } else {
                    result.put(entry.getKey(), new int[]{0, 1});
                }
            }
            return result;
        }));
    }

    private boolean isForcedMetricsClass(Class<?> bStatsService) {
        for (String identifier : KNOWN_FORCED_METRICS) {
            if (bStatsService.getCanonicalName().contains(identifier)) {
                return true;
            }
        }

        final JavaPlugin owningPlugin = getProvidingPlugin(bStatsService);
        if (owningPlugin != null && KNOWN_FORCED_METRICS.contains(owningPlugin.getName())) {
            return true;
        }
        return false;
    }

    private void checkForcedMetrics() {
        if (hasWarned) return;
        hasWarned = true;

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (final Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
                try {
                    service.getField("B_STATS_VERSION"); // Identifies bStats classes

                    if (isForcedMetricsClass(service)) {
                        warnForcedMetrics(service);
                    } else {
                        try {
                            service.getDeclaredField("pluginId"); // Only present in recent bStats classes, which should also have the enabled field unless modified
                        } catch (final NoSuchFieldException e) {
                            // Old bStats class found so "enabled" field detection is unreliable.
                            break;
                        }

                        try {
                            service.getDeclaredField("enabled"); // In some modified forced metrics classes, this will fail
                        } catch (final NoSuchFieldException e) {
                            warnForcedMetrics(service);
                        }
                    }
                } catch (final NoSuchFieldException ignored) {
                }
            }
        });
    }

    private void warnForcedMetrics(final Class<?> service) {
        final Plugin servicePlugin = JavaPlugin.getProvidingPlugin(service);
        plugin.getLogger().severe("WARNING: Potential forced metrics collection by plugin '" + servicePlugin.getName() + "' v" + servicePlugin.getDescription().getVersion());
        plugin.getLogger().severe("Your server is running a plugin that may not respect bStats' opt-out settings.");
        plugin.getLogger().severe("This may cause data to be uploaded to bStats.org for plugins that use bStats, even if you've opted out in the bStats config.");
        plugin.getLogger().severe("Please report this to bStats and to the authors of '" + servicePlugin.getName() + "'. (Offending class: " + service.getName() + ")");
    }

    private JavaPlugin getProvidingPlugin(final Class<?> clazz) {
        try {
            return JavaPlugin.getProvidingPlugin(clazz);
        } catch (final Exception ignored) {
        }

        final ClassLoader parent = clazz.getClassLoader().getParent();
        if (parent.getClass().getName().equals("org.bukkit.plugin.java.PluginClassLoader")) {
            try {
                final Field pluginField = parent.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                return (JavaPlugin) pluginField.get(parent);
            } catch (final Exception ignored) {
            }
        }

        return null;
    }
}
