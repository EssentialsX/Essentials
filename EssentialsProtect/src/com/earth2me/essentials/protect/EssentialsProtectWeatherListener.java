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
		if (!event.isCancelled()
			&& prot.getSettingBool(ProtectConfig.disable_weather_storm)
			&& event.toWeatherState())
		{
			event.setCancelled(true);
		}

	}

	@Override
	public void onLightningStrike(final LightningStrikeEvent event)
	{
		if (!event.isCancelled()
			&& prot.getSettingBool(ProtectConfig.disable_weather_lightning))
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onThunderChange(final ThunderChangeEvent event)
	{
		if (!event.isCancelled()
			&& prot.getSettingBool(ProtectConfig.disable_weather_thunder)
			&& event.toThunderState())
		{
			event.setCancelled(true);
		}
	}
}
