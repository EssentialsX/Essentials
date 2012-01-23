package com.earth2me.essentials.geoip;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.settings.geoip.GeoIP;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import java.io.File;
import java.io.IOException;
import org.bukkit.plugin.Plugin;

public class ConfigHolder extends AsyncStorageObjectHolder<GeoIP>
{
	private final transient Plugin geoip;

	public ConfigHolder(final IEssentials ess, final Plugin geoip)
	{
		super(ess, GeoIP.class);
		this.geoip = geoip;
		onReload();
	}

	@Override
	public File getStorageFile() throws IOException
	{
		return new File(geoip.getDataFolder(), "config.yml");
	}

}
