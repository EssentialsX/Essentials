package com.earth2me.essentials.protect;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import java.util.Locale;


public class EssentialsProtectEntityListener implements Listener {
    private final IProtect prot;
    private final IEssentials ess;

    EssentialsProtectEntityListener(final IProtect prot) {
        this.prot = prot;
        this.ess = prot.getEssentialsConnect().getEssentials();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        final Entity target = event.getEntity();

        if (target instanceof Villager && prot.getSettingBool(ProtectConfig.prevent_villager_death)) {
            event.setCancelled(true);
            return;
        }

        User user = null;
        if (target instanceof Player) {
            user = ess.getUser((Player) target);
        }

        final DamageCause cause = event.getCause();

        if (event instanceof EntityDamageByBlockEvent) {
            if (prot.getSettingBool(ProtectConfig.disable_contactdmg) && cause == DamageCause.CONTACT && !(target instanceof Player && shouldBeDamaged(user, "contact"))) {
                event.setCancelled(true);
                return;
            }
            if (prot.getSettingBool(ProtectConfig.disable_lavadmg) && cause == DamageCause.LAVA && !(target instanceof Player && shouldBeDamaged(user, "lava"))) {
                event.setCancelled(true);
                return;
            }
            if (prot.getSettingBool(ProtectConfig.prevent_tnt_explosion) && cause == DamageCause.BLOCK_EXPLOSION && !(target instanceof Player && shouldBeDamaged(user, "tnt"))) {
                event.setCancelled(true);
                return;
            }
        }

        if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent edEvent = (EntityDamageByEntityEvent) event;
            final Entity eAttack = edEvent.getDamager();

            User attacker = null;
            if (eAttack instanceof Player) {
                attacker = ess.getUser((Player) eAttack);
            }

            //Creeper explode prevention
            if (eAttack instanceof Creeper && (prot.getSettingBool(ProtectConfig.prevent_creeper_explosion) || prot.getSettingBool(ProtectConfig.prevent_creeper_playerdmg)) && !(target instanceof Player && shouldBeDamaged(user, "creeper"))) {
                event.setCancelled(true);
                return;
            }

            if ((event.getEntity() instanceof Fireball || event.getEntity() instanceof SmallFireball) && prot.getSettingBool(ProtectConfig.prevent_fireball_playerdmg) && !(target instanceof Player && shouldBeDamaged(user, "fireball"))) {
                event.setCancelled(true);
                return;
            }

            if (event.getEntity() instanceof WitherSkull && prot.getSettingBool(ProtectConfig.prevent_witherskull_playerdmg) && !(target instanceof Player && shouldBeDamaged(user, "witherskull"))) {
                event.setCancelled(true);
                return;
            }

            if (eAttack instanceof TNTPrimed && prot.getSettingBool(ProtectConfig.prevent_tnt_playerdmg) && !(target instanceof Player && shouldBeDamaged(user, "tnt"))) {
                event.setCancelled(true);
                return;
            }

            if (eAttack instanceof ExplosiveMinecart && prot.getSettingBool(ProtectConfig.prevent_tntminecart_playerdmg) && !(target instanceof Player && shouldBeDamaged(user, "tnt-minecart"))) {
                event.setCancelled(true);
                return;
            }

            // PVP Settings
            if (target instanceof Player && eAttack instanceof Player && prot.getSettingBool(ProtectConfig.disable_pvp) && !user.getName().equalsIgnoreCase(attacker.getName()) && (!user.isAuthorized("essentials.protect.pvp") || !attacker.isAuthorized("essentials.protect.pvp"))) {
                event.setCancelled(true);
                return;
            }

            if (edEvent.getDamager() instanceof Projectile && target instanceof Player && ((prot.getSettingBool(ProtectConfig.disable_projectiles) && !shouldBeDamaged(user, "projectiles")) || (((Projectile) edEvent.getDamager()).getShooter() instanceof Player && prot.getSettingBool(ProtectConfig.disable_pvp) && (!user.isAuthorized("essentials.protect.pvp") || !ess.getUser((Player) ((Projectile) edEvent.getDamager()).getShooter()).isAuthorized("essentials.protect.pvp"))))) {
                event.setCancelled(true);
                return;
            }
        }

        if (target instanceof Player) {
            if (cause == DamageCause.FALL && prot.getSettingBool(ProtectConfig.disable_fall) && !shouldBeDamaged(user, "fall")) {
                event.setCancelled(true);
                return;
            }

            if (cause == DamageCause.SUFFOCATION && prot.getSettingBool(ProtectConfig.disable_suffocate) && !shouldBeDamaged(user, "suffocation")) {
                event.setCancelled(true);
                return;
            }
            if ((cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK) && prot.getSettingBool(ProtectConfig.disable_firedmg) && !shouldBeDamaged(user, "fire")) {
                event.setCancelled(true);
                return;
            }
            if (cause == DamageCause.DROWNING && prot.getSettingBool(ProtectConfig.disable_drown) && !shouldBeDamaged(user, "drowning")) {
                event.setCancelled(true);
                return;
            }
            if (cause == DamageCause.LIGHTNING && prot.getSettingBool(ProtectConfig.disable_lightning) && !shouldBeDamaged(user, "lightning")) {
                event.setCancelled(true);
                return;
            }
            if (cause == DamageCause.WITHER && prot.getSettingBool(ProtectConfig.disable_wither) && !shouldBeDamaged(user, "wither")) {
                event.setCancelled(true);
            }
        }
    }

    private boolean shouldBeDamaged(final User user, final String type) {
        return (user.isAuthorized("essentials.protect.damage.".concat(type)) && !user.isAuthorized("essentials.protect.damage.disable"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        Entity entity = event.getEntity();
        final int maxHeight = ess.getSettings().getProtectCreeperMaxHeight();

        if (entity instanceof EnderDragon && prot.getSettingBool(ProtectConfig.prevent_enderdragon_blockdmg)) {
            event.setCancelled(true);
            if (prot.getSettingBool(ProtectConfig.enderdragon_fakeexplosions)) {
                event.getLocation().getWorld().createExplosion(event.getLocation(), 0F);
            }
            return;
        }
        if (entity instanceof Wither && prot.getSettingBool(ProtectConfig.prevent_wither_spawnexplosion)) {
            event.setCancelled(true);
        } else if (entity instanceof Creeper && (prot.getSettingBool(ProtectConfig.prevent_creeper_explosion) || prot.getSettingBool(ProtectConfig.prevent_creeper_blockdmg) || (maxHeight >= 0 && event.getLocation().getBlockY() > maxHeight))) {
            //Nicccccccccce plaaacccccccccce..
            event.setCancelled(true);
            event.getLocation().getWorld().createExplosion(event.getLocation(), 0F);
        } else if (entity instanceof TNTPrimed && prot.getSettingBool(ProtectConfig.prevent_tnt_explosion)) {
            event.setCancelled(true);

        } else if (entity instanceof Fireball && prot.getSettingBool(ProtectConfig.prevent_fireball_explosion)) {
            event.setCancelled(true);

        } else if ((entity instanceof WitherSkull) && prot.getSettingBool(ProtectConfig.prevent_witherskull_explosion)) {
            event.setCancelled(true);
        } else if ((entity instanceof ExplosiveMinecart) && prot.getSettingBool(ProtectConfig.prevent_tntminecart_explosion)) {
            event.setCancelled(true);
        }

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        final EntityType creature = event.getEntityType();
        if (creature == null) {
            return;
        }
        final String creatureName = creature.toString().toLowerCase(Locale.ENGLISH);
        if (creatureName.isEmpty()) {
            return;
        }
        if (ess.getSettings().getProtectPreventSpawn(creatureName)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(final EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        final User user = ess.getUser((Player) event.getTarget());
        if ((event.getReason() == TargetReason.CLOSEST_PLAYER || event.getReason() == TargetReason.TARGET_ATTACKED_ENTITY || event.getReason() == TargetReason.TARGET_ATTACKED_NEARBY_ENTITY || event.getReason() == TargetReason.RANDOM_TARGET || event.getReason() == TargetReason.DEFEND_VILLAGE || event.getReason() == TargetReason.TARGET_ATTACKED_OWNER || event.getReason() == TargetReason.OWNER_ATTACKED_TARGET) && prot.getSettingBool(ProtectConfig.prevent_entitytarget) && !user.isAuthorized("essentials.protect.entitytarget.bypass")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if ((event.getEntity() instanceof Fireball || event.getEntity() instanceof SmallFireball) && prot.getSettingBool(ProtectConfig.prevent_fireball_fire)) {
            event.setFire(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.ENDERMAN && prot.getSettingBool(ProtectConfig.prevent_enderman_pickup)) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntityType() == EntityType.WITHER && prot.getSettingBool(ProtectConfig.prevent_wither_blockreplace)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPaintingBreak(HangingBreakByEntityEvent event) {
        if ((event.getCause() == HangingBreakEvent.RemoveCause.ENTITY) && ((event.getRemover() instanceof Creeper) && prot.getSettingBool(ProtectConfig.prevent_creeper_explosion) || (((event.getRemover() instanceof Fireball) || (event.getRemover() instanceof SmallFireball)) && prot.getSettingBool(ProtectConfig.prevent_fireball_explosion)) || ((event.getRemover() instanceof TNTPrimed) && prot.getSettingBool(ProtectConfig.prevent_tnt_explosion)) || ((event.getRemover() instanceof WitherSkull) && prot.getSettingBool(ProtectConfig.prevent_witherskull_explosion)))) {
            event.setCancelled(true);
        }
    }
}
