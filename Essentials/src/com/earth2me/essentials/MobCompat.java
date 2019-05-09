package com.earth2me.essentials;

import com.earth2me.essentials.utils.EnumUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Villager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MobCompat {

    public static final EntityType CAT = EnumUtil.getEntityType("CAT", "OCELOT");

    public enum CatType {
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

    public enum VillagerType {
        DESERT,
        JUNGLE,
        PLAINS,
        SAVANNA,
        SNOWY,
        SWAMP,
        TAIGA
    }

    private static Class catClass = null;
    private static Class catTypeClass = null;
    private static Method catSetTypeMethod = null;
    private static Boolean isNewCat = null;

    public static void setCatType(final Entity entity, final CatType type) {
        if (isNewCat == null) {
            try {
                catClass = Class.forName("org.bukkit.entity.Cat");
                catTypeClass = Class.forName("org.bukkit.entity.Cat.Type");
                catSetTypeMethod = catClass.getDeclaredMethod("setCatType", catTypeClass);
                isNewCat = true;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                isNewCat = false;
            }
        }

        if (isNewCat) {
            try {
                catSetTypeMethod.invoke(entity, EnumUtil.valueOf(catTypeClass, type.catTypeName));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            ((Ocelot) entity).setCatType(Ocelot.Type.valueOf(type.ocelotTypeName));
        }
    }

    private static Boolean isNewVillager = null;
    private static Class villagerCareerClass = null;
    private static Method villagerSetCareerMethod = null;
    private static Class villagerTypeClass = null;
    private static Method villagerSetTypeMethod = null;


    private static void checkVillagerEnums() {
        try {
            villagerCareerClass = Class.forName("org.bukkit.entity.Villager.Career");
            villagerSetCareerMethod = Villager.class.getDeclaredMethod("setCareer", villagerCareerClass);
            isNewVillager = false;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            try {
                villagerTypeClass = Class.forName("org.bukkit.entity.Villager.Type");
                villagerSetTypeMethod = Villager.class.getDeclaredMethod("setVillagerType", villagerTypeClass);
                isNewVillager = true;
            } catch (ClassNotFoundException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void setVillagerProfession(final Entity entity, final VillagerProfession profession) {
        if (isNewVillager == null) {
            checkVillagerEnums();
        }

        if (isNewVillager) {
            ((Villager) entity).setProfession(profession.asEnum());
        } else {
            ((Villager) entity).setProfession(profession.asEnum());
            try {
                villagerSetCareerMethod.invoke(entity, EnumUtil.valueOf(villagerCareerClass, profession.oldCareer));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setVillagerType(final Entity entity, final VillagerType type) {
        if (isNewVillager == null) {
            checkVillagerEnums();
        }

        if (!isNewVillager) {
            return;
        }

        try {
            villagerSetTypeMethod.invoke(entity, EnumUtil.valueOf(villagerTypeClass, type.name()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
