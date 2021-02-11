package com.earth2me.essentials.economy;

import com.earth2me.essentials.economy.layers.VaultLayer;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstraction layer for economy abstraction layers.
 */
public final class EconomyLayers {
    private static final Map<String, EconomyLayer> registeredLayers = new HashMap<>();
    private static final Map<String, EconomyLayer> availableLayers = new HashMap<>();
    private static EconomyLayer selectedLayer = null;

    private EconomyLayers() {
    }

    public static void init() {
        if (selectedLayer != null) {
            throw new IllegalStateException("The economy layer has already been selected!");
        }

        registerLayer(new VaultLayer());
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
        availableLayers.put(plugin.getName(), layer);
        if (selectedLayer != null) {
            return null;
        }

        selectedLayer = layer;
        selectedLayer.enable(plugin);
        return selectedLayer;
    }

    public static boolean onPluginDisable(final Plugin plugin, final boolean serverStarted) {
        if (!availableLayers.containsKey(plugin.getName())) {
            return false;
        }

        availableLayers.get(plugin.getName()).disable();
        availableLayers.remove(plugin.getName());

        if (selectedLayer.getPluginName().equals(plugin.getName())) {
            selectedLayer = availableLayers.isEmpty() ? null : availableLayers.values().iterator().next();
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

        availableLayers.remove(getSelectedLayer().getPluginVersion());
        selectedLayer = null;
        if (!availableLayers.isEmpty()) {
            selectedLayer = availableLayers.values().iterator().next();
            onServerLoad();
        }
    }

    public static void registerLayer(final EconomyLayer economyLayer) {
        registeredLayers.put(economyLayer.getPluginName(), economyLayer);
    }
}
