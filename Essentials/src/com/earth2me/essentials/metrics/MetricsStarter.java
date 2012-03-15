package com.earth2me.essentials.metrics;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.metrics.Metrics.Graph;
import com.earth2me.essentials.metrics.Metrics.Plotter;
import java.io.IOException;
import java.util.Locale;
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
			ess.setMetrics(metrics);

			if (!metrics.isOptOut())
			{
				if (ess.getSettings().isMetricsEnabled())
				{
					start = true;
				}
				else
				{
					ess.getLogger().info("This plugin collects minimal statistic data and sends it to http://metrics.essentials3.net.");
					ess.getLogger().info("You can opt out by running /essentials opt-out");
					ess.getLogger().info("This will start 5 minutes after the first admin/op joins.");
					start = false;
				}
				return;
			}
		}
		catch (Exception ex)
		{
			metricsError(ex);
		}
	}

	@Override
	public void run()
	{
		try
		{
			final Metrics metrics = ess.getMetrics();

			final Graph moduleGraph = metrics.createGraph("Modules Used");
			for (Modules module : Modules.values())
			{
				final String moduleName = module.toString();
				if (ess.getServer().getPluginManager().isPluginEnabled(moduleName))
				{
					moduleGraph.addPlotter(new SimplePlotter(moduleName));
				}
			}

			final Graph localeGraph = metrics.createGraph("Locale");
			localeGraph.addPlotter(new SimplePlotter(ess.getI18n().getCurrentLocale().getDisplayLanguage(Locale.ENGLISH)));

			final Graph featureGraph = metrics.createGraph("Features");
			featureGraph.addPlotter(new Plotter("Unique Accounts")
			{
				@Override
				public int getValue()
				{
					return ess.getUserMap().getUniqueUsers();
				}
			});
			featureGraph.addPlotter(new Plotter("Kits")
			{
				@Override
				public int getValue()
				{
					return ess.getSettings().getKits().getKeys(false).size();
				}
			});
			featureGraph.addPlotter(new Plotter("Warps")
			{
				@Override
				public int getValue()
				{
					return ess.getWarps().getWarpNames().size();
				}
			});

			metrics.start();

		}
		catch (Exception ex)
		{
			metricsError(ex);
		}
	}

	public void metricsError(final Exception ex)
	{
		if (ess.getSettings().isDebug())
		{
			ess.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage(), ex);
		}
		else
		{
			ess.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
		}
	}

	public Boolean getStart()
	{
		return start;
	}


	private class SimplePlotter extends Plotter
	{
		public SimplePlotter(final String name)
		{
			super(name);
		}

		@Override
		public int getValue()
		{
			return 1;
		}
	}
}