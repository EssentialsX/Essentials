package com.earth2me.essentials.protect;

import net.ess3.api.IEssentials;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EssentialsProtectEntityListener_1_14_R1 implements Listener {
    private final IProtect prot;
    private final IEssentials ess;

    EssentialsProtectEntityListener_1_14_R1(final IProtect prot) {
        this.prot = prot;
        this.ess = prot.getEssentialsConnect().getEssentials();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.RAVAGER && prot.getSettingBool(ProtectConfig.prevent_ravager_thief)) {
            event.setCancelled(true);
        }
    }
}
