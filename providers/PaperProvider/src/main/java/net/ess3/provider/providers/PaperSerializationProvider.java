package net.ess3.provider.providers;

import net.ess3.provider.SerializationProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.inventory.ItemStack;

@ProviderData(description = "Paper Serialization Provider")
public class PaperSerializationProvider implements SerializationProvider {

    @Override
    public byte[] serializeItem(ItemStack stack) {
        return stack.serializeAsBytes();
    }

    @Override
    public ItemStack deserializeItem(byte[] bytes) {
        return ItemStack.deserializeBytes(bytes);
    }

    @ProviderTest
    public static boolean test() {
        try {
            ItemStack.class.getDeclaredMethod("serializeAsBytes");
            return true;
        } catch (final NoSuchMethodException ignored) {
            return false;
        }
    }
}
