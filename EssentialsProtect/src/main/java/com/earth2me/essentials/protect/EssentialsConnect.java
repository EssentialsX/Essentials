package com.earth2me.essentials.protect;

import com.earth2me.essentials.IConf;
import net.ess3.api.IEssentials;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

class EssentialsConnect {
    private final IEssentials ess;
    private final IProtect protect;

    EssentialsConnect(final Plugin essPlugin, final Plugin essProtect) {
        if (!essProtect.getDescription().getVersion().equals(essPlugin.getDescription().getVersion())) {
            essProtect.getLogger().log(Level.WARNING, tlLiteral("versionMismatchAll"));
        }
        ess = (IEssentials) essPlugin;
        protect = (IProtect) essProtect;
        final ProtectReloader pr = new ProtectReloader();
        pr.reloadConfig();
        ess.addReloadListener(pr);
    }

    IEssentials getEssentials() {
        return ess;
    }

    private class ProtectReloader implements IConf {
        @Override
        public void reloadConfig() {
            for (final ProtectConfig protectConfig : ProtectConfig.values()) {
                if (protectConfig.isList()) {
                    protect.getSettingsList().put(protectConfig, ess.getSettings().getProtectList(protectConfig.getConfigName()));
                } else if (protectConfig.isString()) {
                    protect.getSettingsString().put(protectConfig, ess.getSettings().getProtectString(protectConfig.getConfigName()));
                } else {
                    protect.getSettingsBoolean().put(protectConfig, ess.getSettings().getProtectBoolean(protectConfig.getConfigName(), protectConfig.getDefaultValueBoolean()));
                }
            }
        }
    }
}
