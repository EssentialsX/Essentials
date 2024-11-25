package net.ess3.provider.providers;

import net.ess3.provider.DamageEventProvider;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

@SuppressWarnings("UnstableApiUsage")
public class ModernDamageEventProvider implements DamageEventProvider {
    private final DamageSource MAGIC_SOURCE = DamageSource.builder(DamageType.MAGIC).build();

    @Override
    public EntityDamageEvent callDamageEvent(Player player, EntityDamageEvent.DamageCause cause, double damage) {
        final EntityDamageEvent ede = new EntityDamageEvent(player, cause, MAGIC_SOURCE, damage);
        player.getServer().getPluginManager().callEvent(ede);
        return ede;
    }

    @Override
    public String getDescription() {
        return "1.20.4+ Damage Event Provider";
    }
}
