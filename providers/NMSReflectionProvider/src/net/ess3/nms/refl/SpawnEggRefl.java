/*
 ******************************************************************************
 * This file is part of ASkyBlock.
 * <p>
 * ASkyBlock is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * ASkyBlock is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with ASkyBlock.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************
 */
package net.ess3.nms.refl;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Represents a spawn egg that can be used to spawn mobs.
 *
 * @author tastybento
 */
public class SpawnEggRefl {
    private EntityType type;

    public SpawnEggRefl(final EntityType type) {
        this.type = type;
    }

    /**
     * Converts from an item stack to a spawn egg
     *
     * @param item - ItemStack, quantity is disregarded
     * @return SpawnEgg
     */
    public static SpawnEggRefl fromItemStack(final ItemStack item) throws Exception {
        if (item == null)
            throw new IllegalArgumentException("Item cannot be null");
        if (item.getType() != Material.MONSTER_EGG)
            throw new IllegalArgumentException("Item is not a monster egg");

        final Class<?> NMSItemStackClass = ReflUtil.getNMSClass("ItemStack");
        final Class<?> craftItemStackClass = ReflUtil.getOBCClass("inventory.CraftItemStack");
        final Method asNMSCopyMethod = ReflUtil.getMethodCached(craftItemStackClass, "asNMSCopy", ItemStack.class);

        final Object stack = asNMSCopyMethod.invoke(null, item);
        final Object tagCompound = ReflUtil.getMethodCached(NMSItemStackClass, "getTag").invoke(stack);
        if (tagCompound != null) {
            final Method tagGetCompound = ReflUtil.getMethodCached(tagCompound.getClass(), "getCompound", String.class);
            final Object entityTag = tagGetCompound.invoke(tagCompound, "EntityTag");

            final Method tagGetString = ReflUtil.getMethodCached(entityTag.getClass(), "getString", String.class);
            String idString = (String) tagGetString.invoke(entityTag, "id");
            if (ReflUtil.getNmsVersionObject().isHigherThanOrEqualTo(ReflUtil.V1_11_R1)) {
                idString = idString.split("minecraft:")[1];
            }
            //noinspection deprecation
            final EntityType type = EntityType.fromName(idString);
            if (type != null) {
                return new SpawnEggRefl(type);
            } else {
                throw new IllegalArgumentException("Unable to parse type from item");
            }
        } else {
            throw new IllegalArgumentException("Item is lacking tag compound");
        }
    }

    /**
     * Get the type of entity this egg will spawn.
     *
     * @return The entity type.
     */
    public EntityType getSpawnedType() {
        return type;
    }

    /**
     * Set the type of entity this egg will spawn.
     *
     * @param type The entity type.
     */
    public void setSpawnedType(final EntityType type) {
        if (type.isAlive()) {
            this.type = type;
        }
    }

    @Override
    public String toString() {
        return "SPAWN EGG{" + getSpawnedType() + "}";
    }

    /**
     * Get an ItemStack of one spawn egg
     *
     * @return ItemStack
     */
    public ItemStack toItemStack() throws Exception {
        return toItemStack(1);
    }

    /**
     * Get an itemstack of spawn eggs
     *
     * @param amount
     * @return ItemStack of spawn eggs
     */
    @SuppressWarnings("deprecation")
    public ItemStack toItemStack(final int amount) throws Exception {
        final ItemStack item = new ItemStack(Material.MONSTER_EGG, amount);

        final Class<?> craftItemStackClass = ReflUtil.getOBCClass("inventory.CraftItemStack");
        final Method asNMSCopyMethod = ReflUtil.getMethodCached(craftItemStackClass, "asNMSCopy", ItemStack.class);

        final Class<?> NMSItemStackClass = ReflUtil.getNMSClass("ItemStack");
        final Object stack = asNMSCopyMethod.invoke(null, item);
        Object tagCompound = ReflUtil.getMethodCached(NMSItemStackClass, "getTag").invoke(stack);

        final Class<?> NBTTagCompoundClass = ReflUtil.getNMSClass("NBTTagCompound");
        final Constructor<?> NBTTagCompoundConstructor = ReflUtil.getConstructorCached(NBTTagCompoundClass);
        if (tagCompound == null) {
            tagCompound = NBTTagCompoundConstructor.newInstance();
        }
        final Object id = NBTTagCompoundConstructor.newInstance();
        final Method tagSetString = ReflUtil.getMethodCached(NBTTagCompoundClass, "setString", String.class, String.class);

        String idString = type.getName();
        if (ReflUtil.getNmsVersionObject().isHigherThanOrEqualTo(ReflUtil.V1_11_R1)) {
            // 1.11 requires domain prefix of minecraft.
            idString = "minecraft:" + idString;
        }
        tagSetString.invoke(id, "id", idString);

        final Method tagSetTag = ReflUtil.getMethodCached(NBTTagCompoundClass, "set", String.class, NBTTagCompoundClass.getSuperclass());
        tagSetTag.invoke(tagCompound, "EntityTag", id);

        final Method stackSetTag = ReflUtil.getMethodCached(NMSItemStackClass, "setTag", NBTTagCompoundClass);
        stackSetTag.invoke(stack, tagCompound);

        final Method asBukkitCopyMethod = ReflUtil.getMethodCached(craftItemStackClass, "asBukkitCopy", NMSItemStackClass);
        return (ItemStack) asBukkitCopyMethod.invoke(null, stack);
    }
}
