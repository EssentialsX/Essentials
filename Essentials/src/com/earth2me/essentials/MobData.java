package com.earth2me.essentials;

import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.utils.StringUtil;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;


public enum MobData
{
	BABY_AGEABLE("baby", Ageable.class, Data.BABY, true),
	ADULT_AGEABLE("adult", Ageable.class, Data.ADULT, true),
	BABY_PIG("piglet", EntityType.PIG, Data.BABY, false),
	BABY_WOLF("puppy", EntityType.WOLF, Data.BABY, false),
	BABY_CHICKEN("chick", EntityType.CHICKEN, Data.BABY, false),
	BABY_HORSE("colt", EntityType.HORSE, Data.BABY, false),
	BABY_OCELOT("kitten", EntityType.OCELOT, Data.BABY, false),
	BABY_SHEEP("lamb", EntityType.SHEEP, Data.BABY, false),
	BABY_COW("calf", EntityType.COW.getEntityClass(), Data.BABY, false),
	BABY_VILLAGER("child", EntityType.VILLAGER, Data.BABY, false),
	TAMED_TAMEABLE("tamed", Tameable.class, Data.TAMED, true),
	TAME_TAMEABLE("tame", Tameable.class, Data.TAMED, false),
	RANDOM_SHEEP("random", EntityType.SHEEP, Data.COLORABLE, true),
	COLORABLE_SHEEP("", StringUtil.joinList(DyeColor.values()).toLowerCase(Locale.ENGLISH), EntityType.SHEEP, Data.COLORABLE, true),
	DONKEY_HORSE("donkey", EntityType.HORSE, Horse.Variant.DONKEY, true),
	MULE_HORSE("mule", EntityType.HORSE, Horse.Variant.MULE, true),
	SKELETON_HORSE("skeleton", EntityType.HORSE, Horse.Variant.SKELETON_HORSE, true),
	UNDEAD_HORSE("undead", EntityType.HORSE, Horse.Variant.UNDEAD_HORSE, true),
	ZOMBIE_HORSE("zombie", EntityType.HORSE, Horse.Variant.UNDEAD_HORSE, false),
	POLKA_HORSE("polka", EntityType.HORSE, Horse.Style.BLACK_DOTS, true),
	SOOTY_HORSE("sooty", EntityType.HORSE, Horse.Style.BLACK_DOTS, false),
	BLAZE_HORSE("blaze", EntityType.HORSE, Horse.Style.WHITE, true),
	SOCKS_HORSE("socks", EntityType.HORSE, Horse.Style.WHITE, false),
	LEOPARD_HORSE("leopard", EntityType.HORSE, Horse.Style.WHITE_DOTS, true),
	APPALOOSA_HORSE("appaloosa", EntityType.HORSE, Horse.Style.WHITE_DOTS, false),
	PAINT_HORSE("paint", EntityType.HORSE, Horse.Style.WHITEFIELD, true),
	MILKY_HORSE("milky", EntityType.HORSE, Horse.Style.WHITEFIELD, false),
	SPLOTCHY_HORSE("splotchy", EntityType.HORSE, Horse.Style.WHITEFIELD, false),
	BLACK_HORSE("black", EntityType.HORSE, Horse.Color.BLACK, true),
	CHESTNUT_HORSE("chestnut", EntityType.HORSE, Horse.Color.CHESTNUT, true),
	LIVER_HORSE("liver", EntityType.HORSE, Horse.Color.CHESTNUT, false),
	CREAMY_HORSE("creamy", EntityType.HORSE, Horse.Color.CREAMY, true),
	FLAXEN_HORSE("flaxen", EntityType.HORSE, Horse.Color.CREAMY, false),
	GRAY_HORSE("gray", EntityType.HORSE, Horse.Color.GRAY, true),
	DAPPLE_HORSE("dapple", EntityType.HORSE, Horse.Color.GRAY, false),
	BUCKSKIN_HORSE("buckskin", EntityType.HORSE, Horse.Color.DARK_BROWN, true),
	DARKBROWN_HORSE("darkbrown", EntityType.HORSE, Horse.Color.DARK_BROWN, false),
	DARK_HORSE("dark", EntityType.HORSE, Horse.Color.DARK_BROWN, false),
	DBROWN_HORSE("dbrown", EntityType.HORSE, Horse.Color.DARK_BROWN, false),
	BAY_HORSE("bay", EntityType.HORSE, Horse.Color.BROWN, true),
	BROWN_HORSE("brown", EntityType.HORSE, Horse.Color.BROWN, false),
	CHEST_HORSE("chest", EntityType.HORSE, Data.CHEST, true),
	SADDLE_HORSE("saddle", EntityType.HORSE, Data.HORSESADDLE, true),
	GOLD_ARMOR_HORSE("goldarmor", EntityType.HORSE, Material.GOLD_BARDING, true),
	DIAMOND_ARMOR_HORSE("diamondarmor", EntityType.HORSE, Material.DIAMOND_BARDING, true),
	ARMOR_HORSE("armor", EntityType.HORSE, Material.IRON_BARDING, true),
	SIAMESE_CAT("siamese", EntityType.OCELOT, Ocelot.Type.SIAMESE_CAT, true),
	WHITE_CAT("white", EntityType.OCELOT, Ocelot.Type.SIAMESE_CAT, false),
	RED_CAT("red", EntityType.OCELOT, Ocelot.Type.RED_CAT, true),
	ORANGE_CAT("orange", EntityType.OCELOT, Ocelot.Type.RED_CAT, false),
	TABBY_CAT("tabby", EntityType.OCELOT, Ocelot.Type.RED_CAT, false),
	BLACK_CAT("black", EntityType.OCELOT, Ocelot.Type.BLACK_CAT, true),
	TUXEDO_CAT("tuxedo", EntityType.OCELOT, Ocelot.Type.BLACK_CAT, false),
	VILLAGER_ZOMBIE("villager", EntityType.ZOMBIE.getEntityClass(), Data.VILLAGER, true),
	BABY_ZOMBIE("baby", EntityType.ZOMBIE.getEntityClass(), Data.BABYZOMBIE, true),
	ADULT_ZOMBIE("adult", EntityType.ZOMBIE.getEntityClass(), Data.ADULTZOMBIE, true),
	DIAMOND_SWORD_ZOMBIE("diamondsword", EntityType.ZOMBIE.getEntityClass(), Material.DIAMOND_SWORD, true),
	GOLD_SWORD_ZOMBIE("goldsword", EntityType.ZOMBIE.getEntityClass(), Material.GOLD_SWORD, true),
	IRON_SWORD_ZOMBIE("ironsword", EntityType.ZOMBIE.getEntityClass(), Material.IRON_SWORD, true),
	STONE_SWORD_ZOMBIE("stonesword", EntityType.ZOMBIE.getEntityClass(), Material.STONE_SWORD, false),
	SWORD_ZOMBIE("sword", EntityType.ZOMBIE.getEntityClass(), Material.STONE_SWORD, true),
	DIAMOND_SWORD_SKELETON("diamondsword", EntityType.SKELETON, Material.DIAMOND_SWORD, true),
	GOLD_SWORD_SKELETON("goldsword", EntityType.SKELETON, Material.GOLD_SWORD, true),
	IRON_SWORD_SKELETON("ironsword", EntityType.SKELETON, Material.IRON_SWORD, true),
	STONE_SWORD_SKELETON("stonesword", EntityType.SKELETON, Material.STONE_SWORD, false),
	SWORD_SKELETON("sword", EntityType.SKELETON, Material.STONE_SWORD, true),
	BOW_SKELETON("bow", EntityType.SKELETON, Material.BOW, true),
	WHITHER_SKELETON("wither", EntityType.SKELETON, Data.WITHER, true),
	POWERED_CREEPER("powered", EntityType.CREEPER, Data.ELECTRIFIED, true),
	ELECTRIC_CREEPER("electric", EntityType.CREEPER, Data.ELECTRIFIED, false),
	CHARGED_CREEPER("charged", EntityType.CREEPER, Data.ELECTRIFIED, false),
	SADDLE_PIG("saddle", EntityType.PIG, Data.PIGSADDLE, true),
	ANGRY_WOLF("angry", EntityType.WOLF, Data.ANGRY, true),
	RABID_WOLF("rabid", EntityType.WOLF, Data.ANGRY, false),
	FARMER_VILLAGER("farmer", EntityType.VILLAGER, Villager.Profession.FARMER, true),
	LIBRARIAN_VILLAGER("librarian", EntityType.VILLAGER, Villager.Profession.LIBRARIAN, true),
	PRIEST_VILLAGER("priest", EntityType.VILLAGER, Villager.Profession.PRIEST, true),
	FATHER_VILLAGER("father", EntityType.VILLAGER, Villager.Profession.PRIEST, false),
	SMITH_VILLAGER("smith", EntityType.VILLAGER, Villager.Profession.BLACKSMITH, true),
	BUTCHER_VILLAGER("butcher", EntityType.VILLAGER, Villager.Profession.BUTCHER, true),
	SIZE_SLIME("", "<1-100>", EntityType.SLIME.getEntityClass(), Data.SIZE, true),
	NUM_EXPERIENCE_ORB("", "<1-2000000000>", EntityType.EXPERIENCE_ORB, Data.EXP, true);


	public enum Data
	{
		ADULT,
		BABY,
		CHEST,
		ADULTZOMBIE,
		BABYZOMBIE,
		VILLAGER,
		HORSESADDLE,
		PIGSADDLE,
		ELECTRIFIED,
		WITHER,
		ANGRY,
		TAMED,
		COLORABLE,
		EXP,
		SIZE;
	}
	public static final Logger logger = Logger.getLogger("Essentials");

	private MobData(String n, Object type, Object value, boolean isPublic)
	{
		this.nickname = n;
		this.matched = n;
		this.helpMessage = n;
		this.type = type;
		this.value = value;
		this.isPublic = isPublic;
	}

	private MobData(String n, String h, Object type, Object value, boolean isPublic)
	{
		this.nickname = n;
		this.matched = n;
		this.helpMessage = h;
		this.type = type;
		this.value = value;
		this.isPublic = isPublic;
	}
	final private String nickname;
	final private String helpMessage;
	final private Object type;
	final private Object value;
	final private boolean isPublic;
	private String matched;

	public static LinkedHashMap<String, MobData> getPossibleData(final Entity spawned, boolean publicOnly)
	{
		LinkedHashMap<String, MobData> mobList = new LinkedHashMap<String, MobData>();
		for (MobData data : MobData.values())
		{
			if (data.type instanceof EntityType && spawned.getType().equals(data.type) && ((publicOnly && data.isPublic) || !publicOnly))
			{
				mobList.put(data.nickname.toLowerCase(Locale.ENGLISH), data);
			}
			else if (data.type instanceof Class && ((Class)data.type).isAssignableFrom(spawned.getClass()) && ((publicOnly && data.isPublic) || !publicOnly))
			{
				mobList.put(data.nickname.toLowerCase(Locale.ENGLISH), data);
			}
		}

		return mobList;
	}

	public static List<String> getValidHelp(final Entity spawned)
	{
		List<String> output = new ArrayList<String>();
		LinkedHashMap<String, MobData> posData = getPossibleData(spawned, true);

		for (MobData data : posData.values())
		{
			output.add(data.helpMessage);
		}
		return output;
	}

	public static MobData fromData(final Entity spawned, final String name)
	{
		if (name.isEmpty())
		{
			return null;
		}

		LinkedHashMap<String, MobData> posData = getPossibleData(spawned, false);
		for (String data : posData.keySet())
		{
			if (name.contains(data))
			{
				return posData.get(data);
			}
		}
		return null;
	}

	public String getMatched()
	{
		return this.matched;
	}

	public void setData(final Entity spawned, final Player target, final String rawData) throws Exception
	{
		if (this.value.equals(Data.ANGRY))
		{
			((Wolf)spawned).setAngry(true);
		}
		else if (this.value.equals(Data.ADULT))
		{
			((Ageable)spawned).setAdult();
		}
		else if (this.value.equals(Data.BABY))
		{
			((Ageable)spawned).setBaby();
		}
		else if (this.value.equals(Data.ADULTZOMBIE))
		{
			((Zombie)spawned).setBaby(false);
		}
		else if (this.value.equals(Data.BABYZOMBIE))
		{
			((Zombie)spawned).setBaby(true);
		}
		else if (this.value.equals(Data.CHEST))
		{
			((Horse)spawned).setTamed(true);
			((Horse)spawned).setCarryingChest(true);
		}
		else if (this.value.equals(Data.ELECTRIFIED))
		{
			((Creeper)spawned).setPowered(true);
		}
		else if (this.value.equals(Data.HORSESADDLE))
		{
			final Horse horse = ((Horse)spawned);
			horse.setTamed(true);
			horse.setOwner(target);
			horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
		}
		else if (this.value.equals(Data.PIGSADDLE))
		{
			((Pig)spawned).setSaddle(true);
		}
		else if (this.value.equals(Data.TAMED))
		{
			final Tameable tameable = ((Tameable)spawned);
			tameable.setTamed(true);
			tameable.setOwner(target);
		}
		else if (this.value.equals(Data.VILLAGER))
		{
			((Zombie)spawned).setVillager(this.value.equals(Data.VILLAGER));
		}
		else if (this.value.equals(Data.WITHER))
		{
			((Skeleton)spawned).setSkeletonType(Skeleton.SkeletonType.WITHER);
		}
		else if (this.value.equals(Data.COLORABLE))
		{
			final String color = rawData.toUpperCase(Locale.ENGLISH);
			try
			{
				if (color.equals("RANDOM"))
				{
					final Random rand = new Random();
					((Colorable)spawned).setColor(DyeColor.values()[rand.nextInt(DyeColor.values().length)]);
				}
				else if (!color.isEmpty())
				{
					((Colorable)spawned).setColor(DyeColor.valueOf(color));
				}
				this.matched = rawData;
			}
			catch (Exception e)
			{
				throw new Exception(tl("sheepMalformedColor"), e);
			}
		}
		else if (this.value.equals(Data.EXP))
		{
			try
			{
				((ExperienceOrb)spawned).setExperience(Integer.parseInt(rawData));
				this.matched = rawData;
			}
			catch (NumberFormatException e)
			{
				throw new Exception(tl("invalidNumber"), e);
			}
		}
		else if (this.value.equals(Data.SIZE))
		{
			try
			{
				((Slime)spawned).setSize(Integer.parseInt(rawData));
				this.matched = rawData;
			}
			catch (NumberFormatException e)
			{
				throw new Exception(tl("slimeMalformedSize"), e);
			}
		}
		else if (this.value instanceof Horse.Color)
		{
			((Horse)spawned).setColor((Horse.Color)this.value);
		}
		else if (this.value instanceof Horse.Style)
		{
			((Horse)spawned).setStyle((Horse.Style)this.value);
		}
		else if (this.value instanceof Horse.Variant)
		{
			((Horse)spawned).setVariant((Horse.Variant)this.value);
		}
		else if (this.value instanceof Ocelot.Type)
		{
			((Ocelot)spawned).setCatType((Ocelot.Type)this.value);
		}
		else if (this.value instanceof Villager.Profession)
		{
			((Villager)spawned).setProfession((Villager.Profession)this.value);
		}
		else if (this.value instanceof Material)
		{
			if (this.type.equals(EntityType.HORSE))
			{
				((Horse)spawned).setTamed(true);
				((Horse)spawned).getInventory().setArmor(new ItemStack((Material)this.value, 1));
			}
			else if (this.type.equals(EntityType.ZOMBIE.getEntityClass()) || this.type.equals(EntityType.SKELETON))
			{
				final EntityEquipment invent = ((LivingEntity)spawned).getEquipment();
				invent.setItemInHand(new ItemStack((Material)this.value, 1));
				invent.setItemInHandDropChance(0.1f);
			}
		}
	}
}
