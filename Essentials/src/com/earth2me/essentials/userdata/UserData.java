package com.earth2me.essentials.userdata;

import com.earth2me.essentials.storage.ListType;
import com.earth2me.essentials.storage.MapKeyType;
import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Location;
import org.bukkit.Material;


@Data
@EqualsAndHashCode(callSuper = false)
public class UserData implements StorageObject
{
	private String nickname;
	private double money;
	@MapValueType(Location.class)
	private Map<String, Location> homes = new HashMap<String, Location>();
	@ListType(Material.class)
	private Set<Material> unlimited = new HashSet<Material>();
	@MapValueType(List.class)
	@MapKeyType(Material.class)
	private Map<Material, List<String>> powerTools = new HashMap<Material, List<String>>();
	private Location lastLocation;
	@MapValueType(Long.class)
	private Map<String, Long> timestamps;
	private String jail;
	@ListType
	private List<String> mails;
	private Inventory inventory;
	private boolean teleportEnabled;
	@ListType
	private Set<String> ignore;
	private boolean godmode;
	private boolean muted;
	private boolean jailed;
	private Ban ban;
	private String ipAddress;
	private boolean afk;
	private boolean newplayer = true;
	private String geolocation;
	private boolean socialspy;
	private boolean npc;
	private boolean powertoolsenabled;

	public UserData()
	{
		unlimited.add(Material.AIR);
		unlimited.add(Material.ARROW);
		unlimited.add(Material.APPLE);
		powerTools.put(Material.DEAD_BUSH, Collections.singletonList("test"));
	}
}
