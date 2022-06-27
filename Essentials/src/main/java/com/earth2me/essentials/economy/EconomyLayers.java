package com.earth2me.essentials.economy;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.economy.layers.VaultLayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Abstraction layer for economy abstraction layers.
 */
public final class EconomyLayers {
    private static final Map<String, EconomyLayer> registeredLayers = new HashMap<>();
    private static final List<String> availableLayers = new ArrayList<>();
    private static EconomyLayer selectedLayer = null;
    private static boolean serverStarted = false;

    private EconomyLayers() {
    }

    public static void init() {
        if (!registeredLayers.isEmpty()) {
            throw new IllegalStateException("Economy layers have already been registered!");
        }

        registerLayer(new VaultLayer());
    }

    public static void onEnable(final Essentials ess) {
        ess.scheduleSyncDelayedTask(() -> {
            serverStarted = true;
            for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (!plugin.isEnabled()) {
                    continue;
                }
                final EconomyLayer layer = onPluginEnable(plugin);
                if (layer != null) {
                    ess.getLogger().log(Level.INFO, "Essentials found a compatible payment resolution method: " + layer.getName() + " (v" + layer.getPluginVersion() + ")!");
                }
            }

            onServerLoad();
        });
    }

    public static boolean isServerStarted() {
        return serverStarted;
    }

    public static EconomyLayer getSelectedLayer() {
        return selectedLayer;
    }

    public static boolean isLayerSelected() {
        return getSelectedLayer() != null;
    }

    public static EconomyLayer onPluginEnable(final Plugin plugin) {
        if (!registeredLayers.containsKey(plugin.getName())) {
            return null;
        }

        final EconomyLayer layer = registeredLayers.get(plugin.getName());
        layer.enable(plugin);
        availableLayers.add(plugin.getName());
        if (selectedLayer != null) {
            return null;
        }

        selectedLayer = layer;
        selectedLayer.enable(plugin);
        return selectedLayer;
    }

    public static boolean onPluginDisable(final Plugin plugin) {
        if (!availableLayers.contains(plugin.getName())) {
            return false;
        }

        registeredLayers.get(plugin.getName()).disable();
        availableLayers.remove(plugin.getName());

        if (selectedLayer.getPluginName().equals(plugin.getName())) {
            selectedLayer = availableLayers.isEmpty() ? null : registeredLayers.get(availableLayers.get(0));
            if (selectedLayer != null && serverStarted) {
                selectedLayer.onServerLoad();
            }
            return true;
        }
        return false;
    }

    public static void onServerLoad() {
        if (!isLayerSelected() || getSelectedLayer().onServerLoad()) {
            return;
        }

        availableLayers.remove(getSelectedLayer().getPluginName());
        selectedLayer = null;
        if (!availableLayers.isEmpty()) {
            selectedLayer = registeredLayers.get(availableLayers.get(0));
            onServerLoad();
        }
    }

    public static void registerLayer(final EconomyLayer economyLayer) {
        registeredLayers.put(economyLayer.getPluginName(), economyLayer);
    }
}
