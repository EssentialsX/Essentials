package com.earth2me.essentials.protect;

import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;


public class EssentialsProtectWeatherListener extends WeatherListener
{
	private final transient IProtect prot;

	public EssentialsProtectWeatherListener(final IProtect prot)
	{
		this.prot = prot;
	}

	@Override
	public void onWeatherChange(final WeatherChangeEvent event)
	{
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			if (!event.isCancelled()
				&& settings.getData().isDisableStorm()
				&& event.toWeatherState())
			{
				event.setCancelled(true);
			}
		}
		finally
		{
			settings.unlock();
		}
	}

	@Override
	public void onLightningStrike(final LightningStrikeEvent event)
	{
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			if (!event.isCancelled()
				&& settings.getData().isDisableLighting())
			{
				event.setCancelled(true);
			}
		}
		finally
		{
			settings.unlock();
		}
	}

	@Override
	public void onThunderChange(final ThunderChangeEvent event)
	{
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			if (!event.isCancelled()
				&& settings.getData().isDisableThunder()
				&& event.toThunderState())
			{
				event.setCancelled(true);
			}
		}
		finally
		{
			settings.unlock();
		}
	}
}
