package com.neximation.essentials.protect;

import com.neximation.essentials.IConf;
import net.ess3.api.IEssentials;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.neximation.essentials.I18n.tl;


public class EssentialsConnect {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private final IEssentials ess;
    private final IProtect protect;

    public EssentialsConnect(Plugin essPlugin, Plugin essProtect) {
        if (!essProtect.getDescription().getVersion().equals(essPlugin.getDescription().getVersion())) {
            LOGGER.log(Level.WARNING, tl("versionMismatchAll"));
        }
        ess = (IEssentials) essPlugin;
        protect = (IProtect) essProtect;
        ProtectReloader pr = new ProtectReloader();
        pr.reloadConfig();
        ess.addReloadListener(pr);
    }

    public IEssentials getEssentials() {
        return ess;
    }

    private class ProtectReloader implements IConf {
        @Override
        public void reloadConfig() {
            for (ProtectConfig protectConfig : ProtectConfig.values()) {
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