package com.earth2me.essentials.signs;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>SignFree class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class SignFree extends EssentialsSign {
    /**
     * <p>Constructor for SignFree.</p>
     */
    public SignFree() {
        super("Free");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        try {
            ItemStack item = getItemStack(sign.getLine(1), 1, ess);
            item = getItemMeta(item, sign.getLine(2), ess);
            item = getItemMeta(item, sign.getLine(3), ess);
        } catch (SignException ex) {
            sign.setLine(1, "§c<item>");
            throw new SignException(ex.getMessage(), ex);
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        ItemStack itemStack = getItemStack(sign.getLine(1), 1, ess);
        itemStack = getItemMeta(itemStack, sign.getLine(2), ess);
        final ItemStack item = getItemMeta(itemStack, sign.getLine(3), ess);

        if (item.getType() == Material.AIR) {
            throw new SignException(tl("cantSpawnItem", "Air"));
        }

        item.setAmount(item.getType().getMaxStackSize());

        ItemMeta meta = item.getItemMeta();

        final String displayName = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();

        Inventory invent = ess.getServer().createInventory(player.getBase(), 36, displayName);
        for (int i = 0; i < 36; i++) {
            invent.addItem(item);
        }
        player.getBase().openInventory(invent);
        Trade.log("Sign", "Free", "Interact", username, null, username, new Trade(item, ess), sign.getBlock().getLocation(), ess);
        return true;
    }
}
