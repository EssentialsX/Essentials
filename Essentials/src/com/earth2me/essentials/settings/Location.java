package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Server;


@Data
@EqualsAndHashCode(callSuper = false)
public class Location extends StorageObject
{
	private String worldName = "Test";
	private double x;
	private double y;
	private double z;
	private Float yaw;
	private Float pitch;

	public org.bukkit.Location getBukkit(Server server)
	{
		if (yaw == null || pitch == null)
		{
			return new org.bukkit.Location(server.getWorld(worldName), x, y, z);
		}
		return new org.bukkit.Location(server.getWorld(worldName), x, y, z, yaw, pitch);
	}
}
