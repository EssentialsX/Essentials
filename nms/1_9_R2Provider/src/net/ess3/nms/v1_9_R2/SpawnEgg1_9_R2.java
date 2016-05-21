/*******************************************************************************
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
 *******************************************************************************/
package net.ess3.nms.v1_9_R2;

import net.minecraft.server.v1_9_R2.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a spawn egg that can be used to spawn mobs. Only for V1.9 servers
 *
 * @author tastybento
 */
public class SpawnEgg1_9_R2 {
    private EntityType type;

    public SpawnEgg1_9_R2(EntityType type) {
        this.type = type;
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
    public void setSpawnedType(EntityType type) {
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
     * @return ItemStack
     */
    public ItemStack toItemStack() {
        return toItemStack(1);
    }

    /**
     * Get an itemstack of spawn eggs
     * @param amount
     * @return ItemStack of spawn eggs
     */
    @SuppressWarnings("deprecation")
    public ItemStack toItemStack(int amount) {
        ItemStack item = new ItemStack(Material.MONSTER_EGG, amount);
        net.minecraft.server.v1_9_R2.ItemStack stack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tagCompound = stack.getTag();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        NBTTagCompound id = new NBTTagCompound();
        id.setString("id", type.getName());
        tagCompound.set("EntityTag", id);
        stack.setTag(tagCompound);
        return CraftItemStack.asBukkitCopy(stack);
    }

    /**
     * Converts from an item stack to a spawn egg 1.9
     * @param item - ItemStack, quantity is disregarded
     * @return SpawnEgg 1.9
     */
    public static SpawnEgg1_9_R2 fromItemStack(ItemStack item) throws IllegalArgumentException {
        if (item == null)
            throw new IllegalArgumentException("Item cannot be null");
        if (item.getType() != Material.MONSTER_EGG)
            throw new IllegalArgumentException("Item is not a monster egg");
        net.minecraft.server.v1_9_R2.ItemStack stack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tagCompound = stack.getTag();
        if (tagCompound != null) {
            @SuppressWarnings("deprecation")
            EntityType type = EntityType.fromName(tagCompound.getCompound("EntityTag").getString("id"));
            if (type != null) {
                return new SpawnEgg1_9_R2(type);
            } else {
                throw new IllegalArgumentException("Unable to parse type from item");
            }
        } else {
            throw new IllegalArgumentException("Item is lacking tag compound");
        }
    }
}

