package com.earth2me.essentials.antibuild;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.AdventureUtil;
import net.ess3.api.IEssentials;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

class EssentialsConnect {
    private final transient IEssentials ess;
    private final transient IAntiBuild protect;

    EssentialsConnect(final Plugin essPlugin, final Plugin essProtect) {
        if (!essProtect.getDescription().getVersion().equals(essPlugin.getDescription().getVersion())) {
            essProtect.getLogger().log(Level.WARNING, AdventureUtil.miniToLegacy(tlLiteral("versionMismatchAll")));
        }
        ess = (IEssentials) essPlugin;
        protect = (IAntiBuild) essProtect;
        final AntiBuildReloader pr = new AntiBuildReloader();
        pr.reloadConfig();
        ess.addReloadListener(pr);
    }

    IEssentials getEssentials() {
        return ess;
    }

    void alert(final User user, final String item, final String tlKey) {
        final Location loc = user.getLocation();
        protect.getLogger().log(Level.WARNING, AdventureUtil.miniToLegacy(tlLiteral("alertFormat", user.getName(), tlLiteral(tlKey), item, loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ())));
        for (final Player p : ess.getServer().getOnlinePlayers()) {
            final User alertUser = ess.getUser(p);
            if (alertUser.isAuthorized("essentials.protect.alerts")) {
                alertUser.sendTl("alertFormat", user.getName(), alertUser.playerTl(tlKey), item, loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
            }
        }
    }

    private class AntiBuildReloader implements IConf {
        @Override
        public void reloadConfig() {
            for (final AntiBuildConfig protectConfig : AntiBuildConfig.values()) {
                if (protectConfig.isList()) {
                    protect.getSettingsList().put(protectConfig, ess.getSettings().getProtectList(protectConfig.getConfigName()));
                } else {
                    protect.getSettingsBoolean().put(protectConfig, ess.getSettings().getProtectBoolean(protectConfig.getConfigName(), protectConfig.getDefaultValueBoolean()));
                }

            }

        }
    }
}
