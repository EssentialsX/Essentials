package com.earth2me.essentials.metrics;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.logging.Level;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class MetricsListener implements Listener
{
	private final transient Server server;
	private final transient IEssentials ess;
	private final transient MetricsStarter starter;

	public MetricsListener(final IEssentials parent, final MetricsStarter starter)
	{
		this.ess = parent;
		this.server = parent.getServer();
		this.starter = starter;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final User player = ess.getUser(event.getPlayer());
		if (ess.getSettings().isMetricsEnabled() == false && (player.isAuthorized("essentials.essentials") || player.isAuthorized("bukkit.broadcast.admin")))
		{
			player.sendMessage("PluginMetrics collects minimal statistic data, starting in about 5 minutes.");
			player.sendMessage("To opt out, edit plugins/PluginMetrics/config.yml.");
			ess.getLogger().log(Level.INFO, "[Metrics] Admin join - Starting 5 minute opt-out period.");
			ess.getSettings().setMetricsEnabled(true);
			ess.getScheduler().scheduleAsyncDelayedTask(ess, starter, 5 * 1200);
		}
	}
}
