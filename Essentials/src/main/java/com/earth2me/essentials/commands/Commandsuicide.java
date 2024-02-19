package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import net.ess3.provider.DamageEventProvider;
import org.bukkit.Server;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collections;
import java.util.List;

public class Commandsuicide extends EssentialsCommand {
    public Commandsuicide() {
        super("suicide");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final DamageEventProvider provider = ess.provider(DamageEventProvider.class);

        final EntityDamageEvent ede = provider.callDamageEvent(user.getBase(), EntityDamageEvent.DamageCause.SUICIDE, Float.MAX_VALUE);
        ede.getEntity().setLastDamageCause(ede);
        user.getBase().setHealth(0);
        user.sendTl("suicideMessage");
        user.setDisplayNick();
        ess.broadcastTl(user, "suicideSuccess", new Object[]{user.getDisplayName()});
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
