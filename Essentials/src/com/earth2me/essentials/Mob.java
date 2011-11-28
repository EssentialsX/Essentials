package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
	WOLF("Wolf", Enemies.NEUTRAL, CreatureType.WOLF),
	CAVESPIDER("CaveSpider", Enemies.ENEMY, CreatureType.CAVE_SPIDER),
	ENDERMAN("Enderman", Enemies.ENEMY, "", CreatureType.ENDERMAN),
	SILVERFISH("Silverfish", Enemies.ENEMY, "", CreatureType.SILVERFISH),
	ENDERDRAGON("EnderDragon", Enemies.ENEMY, CreatureType.ENDER_DRAGON),
	VILLAGER("Villager", Enemies.FRIENDLY, CreatureType.VILLAGER),
	BLAZE("Blaze", Enemies.ENEMY, CreatureType.BLAZE),
	MUSHROOMCOW("MushroomCow", Enemies.FRIENDLY, CreatureType.MUSHROOM_COW),
	MAGMACUBE("MagmaCube", Enemies.ENEMY, CreatureType.MAGMA_CUBE),
	SNOWMAN("Snowman", Enemies.FRIENDLY, CreatureType.SNOWMAN);
	
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
			hashMap.put(mob.name.toLowerCase(Locale.ENGLISH), mob);
		}
	}
	
	public static Set<String> getMobList() {
		return hashMap.keySet();
	}

	public LivingEntity spawn(final Player player, final Server server, final Location loc) throws MobException
	{

		final LivingEntity entity = player.getWorld().spawnCreature(loc, this.bukkitType);
		if (entity == null)
		{
			logger.log(Level.WARNING, _("unableToSpawnMob"));
			throw new MobException();
		}
		return entity;
	}


	public enum Enemies
	{
		FRIENDLY("friendly"),
		NEUTRAL("neutral"),
		ENEMY("enemy");

		private Enemies(final String type)
		{
			this.type = type;
		}
		final protected String type;
	}

	public CreatureType getType()
	{
		return bukkitType;
	}

	public static Mob fromName(final String name)
	{
		return hashMap.get(name.toLowerCase(Locale.ENGLISH));
	}


	public static class MobException extends Exception
	{
		private static final long serialVersionUID = 1L;
	}
}
