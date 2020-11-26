package com.earth2me.essentials.perm.impl;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class LuckPermsHandler extends ModernVaultHandler {
    private LuckPerms luckPerms;
    private Set<ContextCalculator<Player>> contextCalculators;

    @Override
    public void registerContext(final String context, final Function<Player, Iterable<String>> calculator, final Supplier<Iterable<String>> suggestions) {
        final ContextCalculator<Player> contextCalculator = new ContextCalculator<Player>() {
            @Override
            public void calculate(final Player target, final ContextConsumer consumer) {
                calculator.apply(target).forEach(value -> consumer.accept(context, value));
            }

            @Override
            public ContextSet estimatePotentialContexts() {
                final ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
                suggestions.get().forEach(value -> builder.add(context, value));
                return builder.build();
            }
        };
        luckPerms.getContextManager().registerCalculator(contextCalculator);
        contextCalculators.add(contextCalculator);
    }

    @Override
    public void unregisterContexts() {
        contextCalculators.forEach(contextCalculator -> luckPerms.getContextManager().unregisterCalculator(contextCalculator));
        contextCalculators.clear();
    }

    @Override
    public boolean tryProvider() {
        final RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            contextCalculators = new HashSet<>();
        }
        return luckPerms != null && super.tryProvider();
    }
}
