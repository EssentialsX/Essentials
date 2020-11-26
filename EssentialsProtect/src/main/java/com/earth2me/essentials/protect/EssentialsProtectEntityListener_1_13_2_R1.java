package com.earth2me.essentials.protect;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;

public class EssentialsProtectEntityListener_1_13_2_R1 implements Listener {
    private final IProtect prot;

    EssentialsProtectEntityListener_1_13_2_R1(final IProtect prot) {
        this.prot = prot;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTransform(final EntityTransformEvent event) {
        final Entity entity = event.getEntity();
        final EntityTransformEvent.TransformReason reason = event.getTransformReason();
        if (reason == EntityTransformEvent.TransformReason.INFECTION && prot.getSettingBool(ProtectConfig.prevent_villager_infection)) {
            event.setCancelled(true);
        } else if (reason == EntityTransformEvent.TransformReason.CURED && prot.getSettingBool(ProtectConfig.prevent_villager_cure)) {
            event.setCancelled(true);
        } else if (reason == EntityTransformEvent.TransformReason.LIGHTNING) {
            if (entity instanceof Villager && prot.getSettingBool(ProtectConfig.prevent_villager_to_witch)) {
                event.setCancelled(true);
            } else if (entity instanceof Pig && prot.getSettingBool(ProtectConfig.prevent_pig_transformation)) {
                event.setCancelled(true);
            } else if (entity instanceof Creeper && prot.getSettingBool(ProtectConfig.prevent_creeper_charge)) {
                event.setCancelled(true);
            } else if (entity instanceof MushroomCow && prot.getSettingBool(ProtectConfig.prevent_mooshroom_switching)) {
                event.setCancelled(true);
            }
        } else if (reason == EntityTransformEvent.TransformReason.DROWNED && prot.getSettingBool(ProtectConfig.prevent_zombie_drowning)) {
            event.setCancelled(true);
        }
    }
}
