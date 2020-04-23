package com.earth2me.essentials.signs;

import com.earth2me.essentials.utils.MaterialUtil;
import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;
import java.util.logging.Level;


public class SignPlayerListener implements Listener {
    private final transient IEssentials ess;

    public SignPlayerListener(final IEssentials ess) {
        this.ess = ess;
    }

    //This following code below listens to cancelled events to fix a bukkit issue
    //Right clicking signs with a block in hand, can now fire cancelled events.
    //This is because when the block place is cancelled (for example not enough space for the block to be placed),
    //the event will be marked as cancelled, thus preventing 30% of sign purchases.
    @EventHandler(priority = EventPriority.LOW)
    public void onSignPlayerInteract(final PlayerInteractEvent event) {
        if (ess.getSettings().areSignsDisabled()) {
            event.getHandlers().unregister(this);
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        final Block block;
        if (event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_AIR) {
            Block targetBlock = null;
            try {
                targetBlock = event.getPlayer().getTargetBlock(null, 5);
            } catch (IllegalStateException ex) {
                if (ess.getSettings().isDebug()) {
                    ess.getLogger().log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            block = targetBlock;
        } else {
            block = event.getClickedBlock();
        }
        if (block == null) {
            return;
        }

        final Material mat = block.getType();
        if (MaterialUtil.isSign(mat)) {
            final String csign = ((Sign) block.getState()).getLine(0);
            for (EssentialsSign sign : ess.getSettings().enabledSigns()) {
                if (csign.equalsIgnoreCase(sign.getSuccessName(ess))) {
                    sign.onSignInteract(block, event.getPlayer(), ess);
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            for (EssentialsSign sign : ess.getSettings().enabledSigns()) {
                if (sign.areHeavyEventRequired() && sign.getBlocks().contains(block.getType()) && !sign.onBlockInteract(block, event.getPlayer(), ess)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
