package com.earth2me.essentials.metrics;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.metrics.Metrics.Graph;
import java.io.IOException;
import java.util.logging.Level;


public class MetricsStarter implements Runnable
{
	private final IEssentials ess;
	private transient Boolean start;


	private enum Modules
	{
		Essentials,
		EssentialsChat,
		EssentialsSpawn,
		EssentialsProtect,
		EssentialsGeoIP,
		EssentialsXMPP
	};

	public MetricsStarter(final IEssentials plugin)
	{
		ess = plugin;
		try
		{
			final Metrics metrics = new Metrics(ess);

			if (!metrics.isOptOut())
			{
				if (ess.getSettings().isMetricsEnabled())
				{
					start = true;
				}
				else
				{
					ess.getLogger().info("This plugin collects minimal statistic data and sends it to http://metrics.essentials3.net.");
					ess.getLogger().info("You can opt out by changing plugins/PluginMetrics/config.yml, set opt-out to true.");
					ess.getLogger().info("This will start 5 minutes after the first admin/op joins.");
					start = false;
				}
				return;
			}
		}
		catch (IOException e)
		{
			ess.getLogger().log(Level.WARNING, "[Metrics] " + e.getMessage(), e);
		}
	}

	@Override
	public void run()
	{
		try
		{
			final Metrics metrics = new Metrics(ess);

			Graph moduleGraph = metrics.createGraph("Modules Used");
			for (Modules module : Modules.values())
			{
				final String moduleName = module.toString();
				if (ess.getServer().getPluginManager().isPluginEnabled(moduleName))
				{
					moduleGraph.addPlotter(new Metrics.Plotter(moduleName)
					{
						@Override
						public int getValue()
						{
							return 1;
						}
					});
				}
			}

			metrics.start();

		}
		catch (IOException e)
		{
			ess.getLogger().log(Level.WARNING, "[Metrics] " + e.getMessage(), e);
		}
	}

	public Boolean getStart()
	{
		return start;
	}
}