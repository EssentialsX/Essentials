package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


// Suffixes can be appended on the end of a mob name to make it plural
// Entities without a suffix, will default to 's'
public enum Mob
{
	CHICKEN("Chicken", Enemies.FRIENDLY, EntityType.CHICKEN),
	COW("Cow", Enemies.FRIENDLY, EntityType.COW),
	CREEPER("Creeper", Enemies.ENEMY, EntityType.CREEPER),
	GHAST("Ghast", Enemies.ENEMY, EntityType.GHAST),
	GIANT("Giant", Enemies.ENEMY, EntityType.GIANT),
	PIG("Pig", Enemies.FRIENDLY, EntityType.PIG),
	PIGZOMB("PigZombie", Enemies.NEUTRAL, EntityType.PIG_ZOMBIE),
	SHEEP("Sheep", Enemies.FRIENDLY, "", EntityType.SHEEP),
	SKELETON("Skeleton", Enemies.ENEMY, EntityType.SKELETON),
	SLIME("Slime", Enemies.ENEMY, EntityType.SLIME),
	SPIDER("Spider", Enemies.ENEMY, EntityType.SPIDER),
	SQUID("Squid", Enemies.FRIENDLY, EntityType.SQUID),
	ZOMBIE("Zombie", Enemies.ENEMY, EntityType.ZOMBIE),
	WOLF("Wolf", Enemies.NEUTRAL, EntityType.WOLF),
	CAVESPIDER("CaveSpider", Enemies.ENEMY, EntityType.CAVE_SPIDER),
	ENDERMAN("Enderman", Enemies.ENEMY, "", EntityType.ENDERMAN),
	SILVERFISH("Silverfish", Enemies.ENEMY, "", EntityType.SILVERFISH),
	ENDERDRAGON("EnderDragon", Enemies.ENEMY, EntityType.ENDER_DRAGON),
	VILLAGER("Villager", Enemies.FRIENDLY, EntityType.VILLAGER),
	BLAZE("Blaze", Enemies.ENEMY, EntityType.BLAZE),
	MUSHROOMCOW("MushroomCow", Enemies.FRIENDLY, EntityType.MUSHROOM_COW),
	MAGMACUBE("MagmaCube", Enemies.ENEMY, EntityType.MAGMA_CUBE),
	SNOWMAN("Snowman", Enemies.FRIENDLY, "", EntityType.SNOWMAN),
	OCELOT("Ocelot", Enemies.NEUTRAL, EntityType.OCELOT),
	IRONGOLEM("IronGolem", Enemies.NEUTRAL, EntityType.IRON_GOLEM),
	WITHER("Wither", Enemies.ENEMY, EntityType.WITHER),
	BAT("Bat", Enemies.FRIENDLY, EntityType.BAT),
	WITCH("Witch", Enemies.ENEMY, EntityType.WITCH),
	BOAT("Boat", Enemies.NEUTRAL, EntityType.BOAT),
	MINECART("Minecart", Enemies.NEUTRAL, EntityType.MINECART),
	ENDERCRYSTAL("EnderCrystal", Enemies.NEUTRAL, EntityType.ENDER_CRYSTAL),
	EXPERIENCEORB("ExperienceOrb", Enemies.NEUTRAL, EntityType.EXPERIENCE_ORB);
	public static final Logger logger = Logger.getLogger("Minecraft");

	private Mob(String n, Enemies en, String s, EntityType type)
	{
		this.suffix = s;
		this.name = n;
		this.type = en;
		this.bukkitType = type;
	}

	private Mob(String n, Enemies en, EntityType type)
	{
		this.name = n;
		this.type = en;
		this.bukkitType = type;
	}
	public String suffix = "s";
	final public String name;
	final public Enemies type;
	final private EntityType bukkitType;
	private static final Map<String, Mob> hashMap = new HashMap<String, Mob>();

	static
	{
		for (Mob mob : Mob.values())
		{
			hashMap.put(mob.name.toLowerCase(Locale.ENGLISH), mob);
		}
	}

	public static Set<String> getMobList()
	{
		return Collections.unmodifiableSet(hashMap.keySet());
	}

	public Entity spawn(final Player player, final Server server, final Location loc) throws MobException
	{
		return spawn(player.getWorld(), server, loc);
	}

	public Entity spawn(final World world, final Server server, final Location loc) throws MobException
	{
		final Entity entity = world.spawn(loc, (Class<? extends Entity>)this.bukkitType.getEntityClass());
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

	public EntityType getType()
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
