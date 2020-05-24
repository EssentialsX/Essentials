package com.earth2me.essentials.perm.impl;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.function.Function;
import java.util.function.Supplier;

public class LuckPermsHandler extends ModernVaultHandler {
    private LuckPerms luckPerms;

    @Override
    public void registerContext(String context, Function<Player, Iterable<String>> calculator, Supplier<Iterable<String>> suggestions) {
        luckPerms.getContextManager().registerCalculator(new ContextCalculator<Player>() {
            @Override
            public void calculate(Player target, ContextConsumer consumer) {
                calculator.apply(target).forEach(value -> consumer.accept(context, value));
            }

            @Override
            public ContextSet estimatePotentialContexts() {
                ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
                suggestions.get().forEach(value -> builder.add(context, value));
                return builder.build();
            }
        });
    }

    @Override
    public boolean tryProvider() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
        return luckPerms != null && super.tryProvider();
    }
}
