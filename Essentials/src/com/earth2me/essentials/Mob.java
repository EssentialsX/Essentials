package com.earth2me.essentials;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public enum Mob
{
	CHICKEN("Chicken", Enemies.FRIENDLY, CreatureType.CHICKEN),
	COW("Cow", Enemies.FRIENDLY, CreatureType.COW),
	CREEPER("Creeper", Enemies.ENEMY, CreatureType.CREEPER),
	GHAST("Ghast", Enemies.ENEMY, CreatureType.GHAST),
	GIANT("Giant", Enemies.ENEMY, CreatureType.GIANT),
	PIG("Pig", Enemies.FRIENDLY, CreatureType.PIG),
	PIGZOMB("PigZombie", Enemies.NEUTRAL, CreatureType.PIG_ZOMBIE),
	SHEEP("Sheep", Enemies.FRIENDLY, "", CreatureType.SHEEP),
	SKELETON("Skeleton", Enemies.ENEMY, CreatureType.SKELETON),
	SLIME("Slime", Enemies.ENEMY, CreatureType.SLIME),
	SPIDER("Spider", Enemies.ENEMY, CreatureType.SPIDER),
	SQUID("Squid", Enemies.FRIENDLY, CreatureType.SQUID),
	ZOMBIE("Zombie", Enemies.ENEMY, CreatureType.ZOMBIE),
	MONSTER("Monster", Enemies.ENEMY, CreatureType.MONSTER),
	WOLF("Wolf", Enemies.NEUTRAL, CreatureType.WOLF);

	public static final Logger logger = Logger.getLogger("Minecraft");

	private Mob(String n, Enemies en, String s, CreatureType type)
	{
		this.suffix = s;
		this.name = n;
		this.type = en;
		this.bukkitType = type;
	}

	private Mob(String n, Enemies en, CreatureType type)
	{
		this.name = n;
		this.type = en;
		this.bukkitType = type;
	}

	public String suffix = "s";
	final public String name;
	final public Enemies type;
	final private CreatureType bukkitType;
	private static final Map<String, Mob> hashMap = new HashMap<String, Mob>();

	static
	{
		for (Mob mob : Mob.values())
		{
			hashMap.put(mob.name, mob);
		}
	}

	public LivingEntity spawn(final Player player, final Server server, final Location loc) throws MobException
	{

		final LivingEntity entity = player.getWorld().spawnCreature(loc, this.bukkitType);
		if (entity == null)
		{
			logger.log(Level.WARNING, Util.i18n("unableToSpawnMob"));
			throw new MobException();
		}
		return entity;
	}


	public enum Enemies
	{
		FRIENDLY("friendly"),
		NEUTRAL("neutral"),
		ENEMY("enemy");

		private Enemies(final String t)
		{
			this.type = t;
		}
		final protected String type;
	}


	public static class MobException extends Exception
	{
		private static final long serialVersionUID = 1L;
	}

	public static Mob fromName(String n)
	{
		return hashMap.get(n);
	}
}
