package com.earth2me.essentials;

import com.earth2me.essentials.utils.EnumUtil;
import net.ess3.nms.refl.ReflUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Villager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.earth2me.essentials.utils.EnumUtil.getEntityType;

public class MobCompat {

    // Constants for mobs added in later versions
    public static final EntityType LLAMA = getEntityType("LLAMA");
    public static final EntityType PARROT = getEntityType("PARROT");
    public static final EntityType TROPICAL_FISH = getEntityType("TROPICAL_FISH");
    public static final EntityType PANDA = getEntityType("PANDA");
    public static final EntityType TRADER_LLAMA = getEntityType("TRADER_LLAMA");

    // Constants for mobs that have changed since earlier versions
    public static final EntityType CAT = getEntityType("CAT", "OCELOT");

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
        WEAPONSMITH("BLACKSMITH", "WEAPON_SMITH", "WEAPONSMITH")
        ;

        private String oldProfession;
        private String oldCareer;
        private String newProfession;

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

    // Older cats are Ocelots, whereas 1.14+ cats are Cats
    private static Class catClass = ReflUtil.getClassCached("org.bukkit.entity.Cat");
    private static Class catTypeClass = ReflUtil.getClassCached("org.bukkit.entity.Cat.Type");
    private static Method catSetTypeMethod = (catClass == null || catTypeClass == null) ? null : ReflUtil.getMethodCached(catClass, "setCatType", catTypeClass);

    private static boolean isNewCat() {
        return (catClass != null && catTypeClass != null && catSetTypeMethod != null);
    }

    public static void setCatType(final Entity entity, final CatType type) {
        if (isNewCat()) {
            try {
                catSetTypeMethod.invoke(entity, EnumUtil.valueOf(catTypeClass, type.catTypeName));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            ((Ocelot) entity).setCatType(Ocelot.Type.valueOf(type.ocelotTypeName));
        }
    }

    // Older villagers have professions and careers, 1.14+ villagers only have professions
    private static Class villagerCareerClass = ReflUtil.getClassCached("org.bukkit.entity.Villager.Career");
    private static Method villagerSetCareerMethod = (villagerCareerClass == null) ? null : ReflUtil.getMethodCached(Villager.class, "setCareer", villagerCareerClass);

    private static boolean isCareerVillager() {
        return (villagerCareerClass != null && villagerSetCareerMethod != null);
    }

    public static void setVillagerProfession(final Entity entity, final VillagerProfession profession) {
        if (!isCareerVillager()) {
            ((Villager) entity).setProfession(profession.asEnum());
        } else {
            ((Villager) entity).setProfession(profession.asEnum());
            try {
                villagerSetCareerMethod.invoke(entity, EnumUtil.valueOf(villagerCareerClass, profession.oldCareer));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Only 1.14+ villagers have biome variants
    public static void setVillagerType(final Entity entity, final String type) {
        Class typeEnum = ReflUtil.getClassCached("org.bukkit.entity.Villager.Type");
        if (typeEnum == null) return;

        Method villagerSetTypeMethod = ReflUtil.getMethodCached(Villager.class, "setVillagerType", typeEnum);
        try {
            villagerSetTypeMethod.invoke(entity, EnumUtil.valueOf(typeEnum, type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Llamas only exist in 1.11+
    public static void setLlamaColor(final Entity entity, final String color) {
        Class llamaClass = ReflUtil.getClassCached("org.bukkit.entity.Llama");
        if (llamaClass == null) return;

        Class colorEnum = ReflUtil.getClassCached("org.bukkit.entity.Llama.Color");
        Method setVariantMethod = ReflUtil.getMethodCached(llamaClass, "setColor");

        try {
            setVariantMethod.invoke(entity, EnumUtil.valueOf(colorEnum, color));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parrots only exist in 1.12+
    public static void setParrotVariant(final Entity entity, final String variant) {
        Class parrotClass = ReflUtil.getClassCached("org.bukkit.entity.Parrot");
        if (parrotClass == null) return;

        Class variantEnum = ReflUtil.getClassCached("org.bukkit.entity.Parrot.Variant");
        Method setVariantMethod = ReflUtil.getMethodCached(parrotClass, "setVariant");
        try {
            setVariantMethod.invoke(entity, EnumUtil.valueOf(variantEnum, variant));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tropical fish only exist in 1.13+
    public static void setTropicalFishPattern(final Entity entity, final String pattern) {
        Class tropicalFishClass = ReflUtil.getClassCached("org.bukkit.entity.TropicalFish");
        if (tropicalFishClass == null) return;

        Class patternEnum = ReflUtil.getClassCached("org.bukkit.entity.TropicalFish.Pattern");
        Method setPatternMethod = ReflUtil.getMethodCached(tropicalFishClass, "setPattern");
        try {
            setPatternMethod.invoke(entity, EnumUtil.valueOf(patternEnum, pattern));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mushroom cow variant API only exists in 1.14+
    public static void setMooshroomVariant(final Entity entity, final String variant) {
        Class mushroomCowClass = ReflUtil.getClassCached("org.bukkit.entity.MushroomCow");
        Class variantEnum = ReflUtil.getClassCached("org.bukkit.entity.MushroomCow.Variant");
        if (mushroomCowClass == null || variantEnum == null) return;

        Method setVariantMethod = ReflUtil.getMethodCached(mushroomCowClass, "setVariant");
        try {
            setVariantMethod.invoke(entity, EnumUtil.valueOf(variantEnum, variant));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Pandas only exists in 1.14+
    public static void setPandaGene(final Entity entity, final String gene, final boolean mainGene) {
        Class pandaClass = ReflUtil.getClassCached("org.bukkit.entity.Panda");
        if (pandaClass == null) return;

        Class geneEnum = ReflUtil.getClassCached("org.bukkit.entity.Panda.Gene");
        Method setGeneMethod;

        if (mainGene) {
            setGeneMethod = ReflUtil.getMethodCached(pandaClass, "setMainGene");
        } else {
            setGeneMethod = ReflUtil.getMethodCached(pandaClass, "setHiddenGene");
        }

        try {
            setGeneMethod.invoke(entity, EnumUtil.valueOf(geneEnum, gene));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
