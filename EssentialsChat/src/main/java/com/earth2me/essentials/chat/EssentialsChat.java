package com.earth2me.essentials.chat;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.EssentialsLogger;
import com.earth2me.essentials.chat.processing.ChatHandler;
import com.earth2me.essentials.metrics.MetricsWrapper;
import net.ess3.api.IEssentials;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public class EssentialsChat extends JavaPlugin {
    private transient IEssentials ess;
    private transient MetricsWrapper metrics = null;

    @Override
    public void onEnable() {
        EssentialsLogger.updatePluginLogger(this);
        final PluginManager pluginManager = getServer().getPluginManager();
        ess = (IEssentials) pluginManager.getPlugin("Essentials");
        if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tlLiteral("versionMismatchAll"));
        }
        if (!ess.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        final ChatHandler legacyHandler = new ChatHandler((Essentials) ess, this);
        legacyHandler.registerListeners();

        if (metrics == null) {
            metrics = new MetricsWrapper(this, 3814, false);
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        metrics.markCommand(command.getName(), true);
        return ess.onCommandEssentials(sender, command, commandLabel, args, EssentialsChat.class.getClassLoader(), "com.earth2me.essentials.chat.Command", "essentials.", null);
    }
}
