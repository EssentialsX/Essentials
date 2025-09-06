package com.earth2me.essentials.protect;

import org.bukkit.entity.Entity;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EssentialsProtectEntityListener_1_21_3_R1 implements Listener {
    private final IProtect prot;

    EssentialsProtectEntityListener_1_21_3_R1(final IProtect prot) {
        this.prot = prot;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof WindCharge && prot.getSettingBool(ProtectConfig.prevent_windcharge_explosion)) {
            event.setCancelled(true);
        }
    }
}
