package com.earth2me.essentials.metrics;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.metrics.Metrics.Graph;
import com.earth2me.essentials.metrics.Metrics.Plotter;
import com.earth2me.essentials.register.payment.Method;
import java.util.Locale;
import java.util.logging.Level;


public class MetricsStarter implements Runnable
{
	private final IEssentials ess;
	private transient Boolean start;


	private enum Modules
	{
		Essentials,
		EssentialsAntiCheat,
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
			featureGraph.addPlotter(new Plotter("Jails")
			{
				@Override
				public int getValue()
				{
					return ess.getJails().getCount();
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

			final Graph enabledGraph = metrics.createGraph("EnabledFeatures");
			enabledGraph.addPlotter(new SimplePlotter("Total"));
			final String BKcommand = ess.getSettings().getBackupCommand();
			if (BKcommand != null && !"".equals(BKcommand))
			{
				enabledGraph.addPlotter(new SimplePlotter("Backup"));
			}
			if (ess.getJails().getCount() > 0)
			{
				enabledGraph.addPlotter(new SimplePlotter("Jails"));
			}
			if (ess.getSettings().getKits().getKeys(false).size() > 0)
			{
				enabledGraph.addPlotter(new SimplePlotter("Kits"));
			}
			if (ess.getWarps().getWarpNames().size() > 0)
			{
				enabledGraph.addPlotter(new SimplePlotter("Warps"));
			}
			if (!ess.getSettings().areSignsDisabled())
			{
				enabledGraph.addPlotter(new SimplePlotter("Signs"));
			}
			if (ess.getSettings().getAutoAfk() > 0)
			{
				enabledGraph.addPlotter(new SimplePlotter("AutoAFK"));
			}
			if (ess.getSettings().changeDisplayName())
			{
				enabledGraph.addPlotter(new SimplePlotter("DisplayName"));
			}
			if (ess.getSettings().getChatRadius() >= 1)
			{
				enabledGraph.addPlotter(new SimplePlotter("LocalChat"));
			}

			final Graph depGraph = metrics.createGraph("Dependencies");
			final Method method = ess.getPaymentMethod().getMethod();
			if (method != null)
			{
				String version = method.getVersion();
				final int dashPosition = version.indexOf('-');
				if (dashPosition > 0)
				{
					version = version.substring(0, dashPosition);
				}
				depGraph.addPlotter(new SimplePlotter(method.getName() + " " + version));
			}
			depGraph.addPlotter(new SimplePlotter(ess.getPermissionsHandler().getName()));

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