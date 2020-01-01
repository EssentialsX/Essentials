package net.ess3.nms.v1_8_R2;

import net.ess3.nms.SpawnerProvider;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class v1_8_R2SpawnerProvider extends SpawnerProvider {
    @Override
    public ItemStack setEntityType(ItemStack is, EntityType type) {
        net.minecraft.server.v1_8_R2.ItemStack itemStack;
        CraftItemStack craftStack = CraftItemStack.asCraftCopy(is);
        itemStack = CraftItemStack.asNMSCopy(craftStack);
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTag(tag);
        }
        if (!tag.hasKey("BlockEntityTag")) {
            tag.set("BlockEntityTag", new NBTTagCompound());
        }
        tag = itemStack.getTag().getCompound("BlockEntityTag");
        tag.setString("EntityId", type.getName());
        ItemStack bukkitItemStack = CraftItemStack.asCraftMirror(itemStack).clone();
        return setDisplayName(bukkitItemStack, type);
    }

    @Override
    public EntityType getEntityType(ItemStack is) {
        net.minecraft.server.v1_8_R2.ItemStack itemStack;
        CraftItemStack craftStack = CraftItemStack.asCraftCopy(is);
        itemStack = CraftItemStack.asNMSCopy(craftStack);
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null || !tag.hasKey("BlockEntityTag")) {
            throw new IllegalArgumentException();
        }
        String name = tag.getCompound("BlockEntityTag").getString("EntityId");
        return EntityType.fromName(name);
    }

    @Override
    public String getHumanName() {
        return "CraftBukkit 1.8.3 NMS-based provider";
    }
}
