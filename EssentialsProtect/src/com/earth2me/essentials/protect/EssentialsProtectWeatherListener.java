package com.earth2me.essentials.protect;

import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;


public class EssentialsProtectWeatherListener extends WeatherListener
{
	private EssentialsProtect parent;

	public EssentialsProtectWeatherListener(EssentialsProtect parent)
	{
		this.parent = parent;
	}

	@Override
	public void onWeatherChange(WeatherChangeEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (EssentialsProtect.playerSettings.get("protect.disable.weather.storm") && event.toWeatherState())
		{
			event.setCancelled(true);
			return;
		}

	}

	@Override
	public void onLightningStrike(LightningStrikeEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (EssentialsProtect.playerSettings.get("protect.disable.weather.lightning"))
		{
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void onThunderChange(ThunderChangeEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (EssentialsProtect.playerSettings.get("protect.disable.weather.thunder") && event.toThunderState())
		{
			event.setCancelled(true);
			return;
		}
	}
}
