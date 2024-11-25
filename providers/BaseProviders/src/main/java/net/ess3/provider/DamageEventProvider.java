package net.ess3.provider;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public interface DamageEventProvider extends Provider {
    EntityDamageEvent callDamageEvent(Player player, EntityDamageEvent.DamageCause cause, double damage);
}
