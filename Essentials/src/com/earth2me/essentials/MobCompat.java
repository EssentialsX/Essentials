package com.earth2me.essentials;

import com.earth2me.essentials.utils.EnumUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Villager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MobCompat {

    public static final EntityType CAT = EnumUtil.getEntityType("CAT", "OCELOT");

    private static Class catClass = null;
    private static Class catTypeClass = null;
    private static Method catSetTypeMethod = null;
    private static Boolean isNewCat = null;

    public enum CatType {
        SIAMESE("SIAMESE", "SIAMESE_CAT"),
        WHITE("WHITE", "SIAMESE_CAT"),
        RED("RED", "RED_CAT"),
        TABBY("TABBY", "RED_CAT"),
        TUXEDO("BLACK", "BLACK_CAT"),
        BRITISH_SHORTHAIR("BRITISH_SHORTHAIR", null),
        CALICO("CALICO", null),
        PERSIAN("PERSIAN", null),
        RAGDOLL("RAGDOLL", null),
        JELLIE("JELLIE", null),
        BLACK("ALL_BLACK", "BLACK"),
        ;

        private final String catTypeName;
        private final String ocelotTypeName;

        CatType(final String catTypeName, final String ocelotTypeName) {
            this.catTypeName = catTypeName;
            this.ocelotTypeName = ocelotTypeName;
        }
    }

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

    private static Villager.Profession getVillagerProfession(String... names) {
        // Add nitwit as a default in case we're on older versions
        names = Arrays.asList(names, "NITWIT").toArray(new String[0]);
        return EnumUtil.valueOf(Villager.Profession.class, names);
    }

}
