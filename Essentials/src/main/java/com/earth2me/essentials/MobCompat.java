package com.earth2me.essentials;

import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.nms.refl.ReflUtil;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Villager;

import java.lang.reflect.Method;

import static com.earth2me.essentials.utils.EnumUtil.getEntityType;

public final class MobCompat {

    // Constants for mob interfaces added in later versions
    @SuppressWarnings("rawtypes")
    public static final Class RAIDER = ReflUtil.getClassCached("org.bukkit.entity.Raider");

    // Constants for mobs added in later versions
    public static final EntityType LLAMA = getEntityType("LLAMA");
    public static final EntityType PARROT = getEntityType("PARROT");
    public static final EntityType TROPICAL_FISH = getEntityType("TROPICAL_FISH");
    public static final EntityType PANDA = getEntityType("PANDA");
    public static final EntityType TRADER_LLAMA = getEntityType("TRADER_LLAMA");
    public static final EntityType SHULKER = getEntityType("SHULKER");
    public static final EntityType STRAY = getEntityType("STRAY");
    public static final EntityType FOX = getEntityType("FOX");
    public static final EntityType PHANTOM = getEntityType("PHANTOM");
    public static final EntityType AXOLOTL = getEntityType("AXOLOTL");
    public static final EntityType GOAT = getEntityType("GOAT");

    // Constants for mobs that have changed since earlier versions
    public static final EntityType CAT = getEntityType("CAT", "OCELOT");
    public static final EntityType ZOMBIFIED_PIGLIN = getEntityType("ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");

    private MobCompat() {
    }

    // Older cats are Ocelots, whereas 1.14+ cats are Cats
    public static void setCatType(final Entity entity, final CatType type) {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_14_R01)) {
            ((Ocelot) entity).setCatType(Ocelot.Type.valueOf(type.ocelotTypeName));
        } else {
            final Class cat = ReflUtil.getClassCached("org.bukkit.entity.Cat");
            final Class catType = ReflUtil.getClassCached("org.bukkit.entity.Cat$Type");
            final Method setCatType = ReflUtil.getMethodCached(cat, "setCatType", catType);
            try {
                setCatType.invoke(entity, EnumUtil.valueOf(catType, type.catTypeName));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Older villagers have professions and careers, 1.14+ villagers only have professions
    public static void setVillagerProfession(final Entity entity, final VillagerProfession profession) {
        if (!(entity instanceof Villager)) {
            return;
        }
        final Villager villager = (Villager) entity;
        villager.setProfession(profession.asEnum());
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_14_R01)) {
            final Class villagerCareer = ReflUtil.getClassCached("org.bukkit.entity.Villager$Career");
            final Method setCareer = ReflUtil.getMethodCached(Villager.class, "setCareer", villagerCareer);
            try {
                setCareer.invoke(entity, EnumUtil.valueOf(villagerCareer, profession.oldCareer));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Only 1.14+ villagers have biome variants
    public static void setVillagerType(final Entity entity, final String type) {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_14_R01)) {
            return;
        }
        if (entity instanceof Villager) {
            ((Villager) entity).setVillagerType(Villager.Type.valueOf(type));
        }
    }

    // Llamas only exist in 1.11+
    public static void setLlamaColor(final Entity entity, final String color) {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_11_R01)) {
            return;
        }
        if (entity instanceof Llama) {
            ((Llama) entity).setColor(Llama.Color.valueOf(color));
        }
    }

    // Parrots only exist in 1.12+
    public static void setParrotVariant(final Entity entity, final String variant) {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_12_0_R01)) {
            return;
        }
        if (entity instanceof Parrot) {
            ((Parrot) entity).setVariant(Parrot.Variant.valueOf(variant));
        }
    }

    // Tropical fish only exist in 1.13+
    public static void setTropicalFishPattern(final Entity entity, final String pattern) {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_12_0_R01)) {
            return;
        }
        if (entity instanceof TropicalFish) {
            ((TropicalFish) entity).setPattern(TropicalFish.Pattern.valueOf(pattern));
        }
    }

    // Mushroom cow variant API only exists in 1.14+
    public static void setMooshroomVariant(final Entity entity, final String variant) {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_14_R01)) {
            return;
        }
        if (entity instanceof MushroomCow) {
            ((MushroomCow) entity).setVariant(MushroomCow.Variant.valueOf(variant));
        }
    }

    // Pandas only exists in 1.14+
    public static void setPandaGene(final Entity entity, final String gene, final boolean mainGene) {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_14_R01)) {
            return;
        }
        if (entity instanceof Panda) {
            final Panda panda = (Panda) entity;
            final Panda.Gene pandaGene = Panda.Gene.valueOf(gene);
            if (mainGene) {
                panda.setMainGene(pandaGene);
            } else {
                panda.setHiddenGene(pandaGene);
            }
        }
    }

    // Foxes only exist in 1.14+
    public static void setFoxType(final Entity entity, final String type) {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_14_R01)) {
            return;
        }
        if (entity instanceof Fox) {
            ((Fox) entity).setFoxType(Fox.Type.valueOf(type));
        }
    }

    public static void setAxolotlVariant(final Entity entity, final String variant) {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_17_R01)) {
            return;
        }
        if (entity instanceof Axolotl) {
            ((Axolotl) entity).setVariant(Axolotl.Variant.valueOf(variant));
        }
    }

    public enum CatType {
        // These are (loosely) Mojang names for the cats
        SIAMESE("SIAMESE", "SIAMESE_CAT"),
        WHITE("WHITE", "SIAMESE_CAT"),
        RED("RED", "RED_CAT"),
        TABBY("TABBY", "RED_CAT"),
        TUXEDO("BLACK", "BLACK_CAT"),
        BRITISH_SHORTHAIR("BRITISH_SHORTHAIR", "SIAMESE_CAT"),
        CALICO("CALICO", "RED_CAT"),
        PERSIAN("PERSIAN", "RED_CAT"),
        RAGDOLL("RAGDOLL", "SIAMESE_CAT"),
        JELLIE("JELLIE", "SIAMESE_CAT"),
        BLACK("ALL_BLACK", "BLACK_CAT"),
        ;

        private final String catTypeName;
        private final String ocelotTypeName;

        CatType(final String catTypeName, final String ocelotTypeName) {
            this.catTypeName = catTypeName;
            this.ocelotTypeName = ocelotTypeName;
        }
    }

    public enum VillagerProfession {
        // These are 1.14+ villager professions mapped to their respective pre-V&P profession and career
        NONE("FARMER", "FARMER", "NONE"),
        ARMORER("BLACKSMITH", "ARMORER"),
        BUTCHER("FARMER", "BUTCHER"),
        CARTOGRAPHER("LIBRARIAN", "CARTOGRAPHER"),
        CLERIC("PRIEST", "CLERIC"),
        FARMER("FARMER", "FARMER"),
        FISHERMAN("FARMER", "FISHERMAN"),
        FLETCHER("FARMER", "FLETCHER"),
        LEATHERWORKER("BUTCHER", "LEATHERWORKER"),
        LIBRARIAN("LIBRARIAN", "LIBRARIAN"),
        MASON(null, null, "MASON"),
        NITWIT("NITWIT", "NITWIT"),
        SHEPHERD("FARMER", "SHEPHERD"),
        TOOLSMITH("BLACKSMITH", "TOOL_SMITH", "TOOLSMITH"),
        WEAPONSMITH("BLACKSMITH", "WEAPON_SMITH", "WEAPONSMITH"),
        ;

        private final String oldProfession;
        private final String oldCareer;
        private final String newProfession;

        VillagerProfession(final String oldProfession, final String career) {
            this.oldProfession = oldProfession;
            this.oldCareer = career;
            this.newProfession = career;
        }

        VillagerProfession(final String oldProfession, final String oldCareer, final String newProfession) {
            this.oldProfession = oldProfession;
            this.oldCareer = oldCareer;
            this.newProfession = newProfession;
        }

        private Villager.Profession asEnum() {
            return EnumUtil.valueOf(Villager.Profession.class, newProfession, oldProfession);
        }
    }

}
