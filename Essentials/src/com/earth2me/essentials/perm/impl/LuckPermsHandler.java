package com.earth2me.essentials.perm.impl;

import com.earth2me.essentials.OfflinePlayer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
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
    public void registerContext(String context, Function<Player, Iterable<String>> calculator, Supplier<Iterable<String>> suggestions) {
        ContextCalculator<Player> contextCalculator = new ContextCalculator<Player>() {
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
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            contextCalculators = new HashSet<>();
        }
        return luckPerms != null && super.tryProvider();
    }

    @Override
    public boolean hasOfflineSupport() {
        return true;
    }

    @Override
    public boolean hasPermission(Player base, String node) {
        // Do offline perm checking.
        if (base instanceof OfflinePlayer) {
            User user = luckPerms.getUserManager().loadUser(base.getUniqueId()).join();
            return hasPermission(user, node);
        }
        
        return super.hasPermission(base, node);
    }

    private boolean hasPermission(User user, String permission) {
        ContextManager contextManager = luckPerms.getContextManager();
        ImmutableContextSet contextSet = contextManager.getContext(user).orElseGet(contextManager::getStaticContext);

        CachedPermissionData permissionData = user.getCachedData().getPermissionData(QueryOptions.contextual(contextSet));
        return permissionData.checkPermission(permission).asBoolean();
    }
}
