package com.earth2me.essentials;

import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.EnumUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

// Suffixes can be appended on the end of a mob name to make it plural
// Entities without a suffix, will default to 's'
public enum Mob {
    CHICKEN("Chicken", Enemies.FRIENDLY, EntityType.CHICKEN),
    COW("Cow", Enemies.FRIENDLY, EntityType.COW),
    CREEPER("Creeper", Enemies.ENEMY, EntityType.CREEPER),
    GHAST("Ghast", Enemies.ENEMY, EntityType.GHAST),
    GIANT("Giant", Enemies.ENEMY, EntityType.GIANT),
    HORSE("Horse", Enemies.FRIENDLY, EntityType.HORSE),
    PIG("Pig", Enemies.FRIENDLY, EntityType.PIG),
    PIGZOMB("PigZombie", Enemies.NEUTRAL, MobCompat.ZOMBIFIED_PIGLIN),
    ZOMBIFIED_PIGLIN("ZombifiedPiglin", Enemies.NEUTRAL, MobCompat.ZOMBIFIED_PIGLIN),
    SHEEP("Sheep", Enemies.FRIENDLY, "", EntityType.SHEEP),
    SKELETON("Skeleton", Enemies.ENEMY, EntityType.SKELETON),
    SLIME("Slime", Enemies.ENEMY, EntityType.SLIME),
    SPIDER("Spider", Enemies.ENEMY, EntityType.SPIDER),
    SQUID("Squid", Enemies.FRIENDLY, EntityType.SQUID),
    ZOMBIE("Zombie", Enemies.ENEMY, EntityType.ZOMBIE),
    WOLF("Wolf", Enemies.NEUTRAL, "", EntityType.WOLF),
    CAVESPIDER("CaveSpider", Enemies.ENEMY, EntityType.CAVE_SPIDER),
    ENDERMAN("Enderman", Enemies.ENEMY, "", EntityType.ENDERMAN),
    SILVERFISH("Silverfish", Enemies.ENEMY, "", EntityType.SILVERFISH),
    ENDERDRAGON("EnderDragon", Enemies.ENEMY, EntityType.ENDER_DRAGON),
    VILLAGER("Villager", Enemies.FRIENDLY, EntityType.VILLAGER),
    BLAZE("Blaze", Enemies.ENEMY, EntityType.BLAZE),
    MUSHROOMCOW("MushroomCow", Enemies.FRIENDLY, MobCompat.MOOSHROOM),
    MAGMACUBE("MagmaCube", Enemies.ENEMY, EntityType.MAGMA_CUBE),
    SNOWMAN("Snowman", Enemies.FRIENDLY, "", MobCompat.SNOW_GOLEM),
    OCELOT("Ocelot", Enemies.NEUTRAL, EntityType.OCELOT),
    IRONGOLEM("IronGolem", Enemies.NEUTRAL, EntityType.IRON_GOLEM),
    WITHER("Wither", Enemies.ENEMY, EntityType.WITHER),
    BAT("Bat", Enemies.FRIENDLY, EntityType.BAT),
    WITCH("Witch", Enemies.ENEMY, EntityType.WITCH),
    BOAT("Boat", Enemies.NEUTRAL, EntityType.BOAT),
    MINECART("Minecart", Enemies.NEUTRAL, EntityType.MINECART),
    MINECART_CHEST("ChestMinecart", Enemies.NEUTRAL, MobCompat.CHEST_MINECART),
    MINECART_FURNACE("FurnaceMinecart", Enemies.NEUTRAL, MobCompat.FURNACE_MINECART),
    MINECART_TNT("TNTMinecart", Enemies.NEUTRAL, MobCompat.TNT_MINECART),
    MINECART_HOPPER("HopperMinecart", Enemies.NEUTRAL, MobCompat.HOPPER_MINECART),
    MINECART_MOB_SPAWNER("SpawnerMinecart", Enemies.NEUTRAL, MobCompat.SPAWNER_MINECART),
    ENDERCRYSTAL("EnderCrystal", Enemies.NEUTRAL, MobCompat.END_CRYSTAL),
    EXPERIENCEORB("ExperienceOrb", Enemies.NEUTRAL, "EXPERIENCE_ORB"),
    ARMOR_STAND("ArmorStand", Enemies.NEUTRAL, "ARMOR_STAND"),
    ENDERMITE("Endermite", Enemies.ENEMY, "ENDERMITE"),
    GUARDIAN("Guardian", Enemies.ENEMY, "GUARDIAN"),
    ELDER_GUARDIAN("ElderGuardian", Enemies.ENEMY, "ELDER_GUARDIAN"),
    RABBIT("Rabbit", Enemies.FRIENDLY, "RABBIT"),
    SHULKER("Shulker", Enemies.ENEMY, "SHULKER"),
    POLAR_BEAR("PolarBear", Enemies.NEUTRAL, "POLAR_BEAR"),
    WITHER_SKELETON("WitherSkeleton", Enemies.ENEMY, "WITHER_SKELETON"),
    STRAY_SKELETON("StraySkeleton", Enemies.ENEMY, "STRAY"),
    ZOMBIE_VILLAGER("ZombieVillager", Enemies.FRIENDLY, "ZOMBIE_VILLAGER"),
    SKELETON_HORSE("SkeletonHorse", Enemies.FRIENDLY, "SKELETON_HORSE"),
    ZOMBIE_HORSE("ZombieHorse", Enemies.FRIENDLY, "ZOMBIE_HORSE"),
    DONKEY("Donkey", Enemies.FRIENDLY, "DONKEY"),
    MULE("Mule", Enemies.FRIENDLY, "MULE"),
    EVOKER("Evoker", Enemies.ENEMY, "EVOKER"),
    VEX("Vex", Enemies.ENEMY, "VEX"),
    VINDICATOR("Vindicator", Enemies.ENEMY, "VINDICATOR"),
    LLAMA("Llama", Enemies.NEUTRAL, "LLAMA"),
    HUSK("Husk", Enemies.ENEMY, "HUSK"),
    ILLUSIONER("Illusioner", Enemies.ENEMY, "ILLUSIONER"),
    PARROT("Parrot", Enemies.NEUTRAL, "PARROT"),
    TURTLE("Turtle", Enemies.NEUTRAL, "TURTLE"),
    PHANTOM("Phantom", Enemies.ENEMY, "PHANTOM"),
    COD("Cod", Enemies.NEUTRAL, "", "COD"),
    SALMON("Salmon", Enemies.NEUTRAL, "", "SALMON"),
    PUFFERFISH("Pufferfish", Enemies.NEUTRAL, "", "PUFFERFISH"),
    TROPICAL_FISH("TropicalFish", Enemies.NEUTRAL, "", "TROPICAL_FISH"),
    DROWNED("Drowned", Enemies.ENEMY, "DROWNED"),
    DOLPHIN("Dolphin", Enemies.NEUTRAL, "DOLPHIN"),
    CAT("Cat", Enemies.FRIENDLY, "CAT"),
    FOX("Fox", Enemies.FRIENDLY, "es", "FOX"),
    PANDA("Panda", Enemies.NEUTRAL, "PANDA"),
    PILLAGER("Pillager", Enemies.ENEMY, "PILLAGER"),
    RAVAGER("Ravager", Enemies.ENEMY, "RAVAGER"),
    TRADER_LLAMA("TraderLlama", Enemies.FRIENDLY, "TRADER_LLAMA"),
    WANDERING_TRADER("WanderingTrader", Enemies.FRIENDLY, "WANDERING_TRADER"),
    BEE("Bee", Enemies.NEUTRAL, "BEE"),
    STRAY("Stray", Enemies.ENEMY, "STRAY"),
    HOGLIN("Hoglin", Enemies.ADULT_ENEMY, "HOGLIN"),
    PIGLIN("Piglin", Enemies.ADULT_ENEMY, "PIGLIN"),
    STRIDER("Strider", Enemies.FRIENDLY, "STRIDER"),
    ZOGLIN("Zoglin", Enemies.ENEMY, "ZOGLIN"),
    PIGLIN_BRUTE("PiglinBrute", Enemies.ADULT_ENEMY, "PIGLIN_BRUTE"),
    AXOLOTL("Axolotl", Enemies.FRIENDLY, "AXOLOTL"),
    GOAT("Goat", Enemies.NEUTRAL, "GOAT"),
    GLOW_SQUID("GlowSquid", Enemies.FRIENDLY, "GLOW_SQUID"),
    ALLAY("Allay", Enemies.FRIENDLY, "ALLAY"),
    FROG("Frog", Enemies.FRIENDLY, "FROG"),
    TADPOLE("Tadpole", Enemies.FRIENDLY, "TADPOLE"),
    WARDEN("Warden", Enemies.ENEMY, "WARDEN"),
    CHEST_BOAT("ChestBoat", Enemies.NEUTRAL, "CHEST_BOAT"),
    CAMEL("Camel", Enemies.FRIENDLY, "CAMEL"),
    SNIFFER("Sniffer", Enemies.FRIENDLY, "SNIFFER"),
    ARMADILLO("Armadillo", Enemies.FRIENDLY, "ARMADILLO"),
    ;

    private static final Map<String, Mob> hashMap = new HashMap<>();
    private static final Map<EntityType, Mob> bukkitMap = new HashMap<>();

    static {
        for (final Mob mob : Mob.values()) {
            hashMap.put(mob.name.toLowerCase(Locale.ENGLISH), mob);
            if (mob.bukkitType != null) {
                bukkitMap.put(mob.bukkitType, mob);
            }
        }
    }

    final public String name;
    final public Enemies type;
    final private EntityType bukkitType;
    public String suffix = "s";

    Mob(final String n, final Enemies en, final String s, final EntityType type) {
        this.suffix = s;
        this.name = n;
        this.type = en;
        this.bukkitType = type;
    }

    Mob(final String n, final Enemies en, final EntityType type) {
        this.name = n;
        this.type = en;
        this.bukkitType = type;
    }

    Mob(final String n, final Enemies en, final String s, final String typeName) {
        this.suffix = s;
        this.name = n;
        this.type = en;
        bukkitType = EnumUtil.getEntityType(typeName);
    }

    Mob(final String n, final Enemies en, final String typeName) {
        this.name = n;
        this.type = en;
        bukkitType = EnumUtil.getEntityType(typeName);
    }

    public static Set<String> getMobList() {
        return Collections.unmodifiableSet(hashMap.keySet());
    }

    public static Mob fromName(final String name) {
        return hashMap.get(name.toLowerCase(Locale.ENGLISH));
    }

    public static Mob fromBukkitType(final EntityType type) {
        return bukkitMap.get(type);
    }

    public Entity spawn(final World world, final Server server, final Location loc) throws MobException {
        final Entity entity = world.spawn(loc, this.bukkitType.getEntityClass());
        if (entity == null) {
            Essentials.getWrappedLogger().log(Level.WARNING, AdventureUtil.miniToLegacy(tlLiteral("unableToSpawnMob")));
            throw new MobException();
        }
        return entity;
    }

    public EntityType getType() {
        return bukkitType;
    }

    public enum Enemies {
        FRIENDLY("friendly"),
        NEUTRAL("neutral"),
        ENEMY("enemy"),
        ADULT_ENEMY("adult_enemy");

        final protected String type;

        Enemies(final String type) {
            this.type = type;
        }
    }

    public static class MobException extends Exception {
        private static final long serialVersionUID = 1L;
    }
}
