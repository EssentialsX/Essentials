package com.earth2me.essentials.perm.context.luckperms;

import com.earth2me.essentials.Essentials;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;

public class VanishContextCalculator implements ContextCalculator<Player> {
    private final Essentials ess;

    private static final String KEY = "vanished";

    public VanishContextCalculator(Essentials ess) {
        this.ess = ess;
    }

    @Override
    public void calculate(Player target, ContextConsumer consumer) {
        consumer.accept(KEY, Boolean.toString(ess.getUser(target).isVanished()));
    }

    @Override
    public ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        builder.add(KEY, "true");
        builder.add(KEY, "false");
        return builder.build();
    }
}
