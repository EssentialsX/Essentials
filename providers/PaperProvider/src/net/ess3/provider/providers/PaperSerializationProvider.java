package net.ess3.provider.providers;

import net.ess3.provider.SerializationProvider;
import org.bukkit.inventory.ItemStack;

public class PaperSerializationProvider implements SerializationProvider {

    @Override
    public byte[] serializeItem(ItemStack stack) {
        return stack.serializeAsBytes();
    }

    @Override
    public ItemStack deserializeItem(byte[] bytes) {
        return ItemStack.deserializeBytes(bytes);
    }

    @Override
    public String getDescription() {
        return "Paper Serialization Provider";
    }
}
