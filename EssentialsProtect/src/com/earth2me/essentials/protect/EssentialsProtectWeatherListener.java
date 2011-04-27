package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.weather.WeatherListener;
import org.bukkit.inventory.ItemStack;


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
		if (event.isCancelled()) return;
		if(EssentialsProtect.playerSettings.get("protect.disable.weather.storm") && event.toWeatherState())
		{
			event.setCancelled(true);
			return;
		}

	}

	@Override
	public void onLightningStrike(LightningStrikeEvent event)
	{
		if (event.isCancelled()) return;
		if(EssentialsProtect.playerSettings.get("protect.disable.weather.lightning"))
		{
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void onThunderChange(ThunderChangeEvent event)
	{
		if (event.isCancelled()) return;
		if(EssentialsProtect.playerSettings.get("protect.disable.weather.thunder") && event.toThunderState())
		{
			event.setCancelled(true);
			return;
		}
	}
}
