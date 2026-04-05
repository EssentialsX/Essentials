package com.earth2me.essentials.perm.impl;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class LuckPermsHandler extends ModernVaultHandler {
    private LuckPerms luckPerms;
    private Essentials ess;
    private CombinedCalculator calculator;
    private final Map<UUID, Map<String, Boolean>> permissionCache = new ConcurrentHashMap<>();
    private EventSubscription<UserDataRecalculateEvent> recalculateSubscription;

    @Override
    public void registerContext(final String context, final Function<User, Iterable<String>> calculator, final Supplier<Iterable<String>> suggestions) {
        if (this.calculator == null) {
            this.calculator = new CombinedCalculator();
            this.luckPerms.getContextManager().registerCalculator(this.calculator);
        }
        this.calculator.calculators.add(new Calculator(context, calculator, suggestions));
    }

    @Override
    public void unregisterContexts() {
        if (this.calculator != null) {
            this.luckPerms.getContextManager().unregisterCalculator(this.calculator);
            this.calculator = null;
        }
        if (this.recalculateSubscription != null) {
            this.recalculateSubscription.close();
            this.recalculateSubscription = null;
        }
        this.permissionCache.clear();
    }

    @Override
    public boolean hasPermissionCached(final Player base, final String node) {
        final UUID uuid = base.getUniqueId();
        final Map<String, Boolean> userCache = permissionCache.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        return userCache.computeIfAbsent(node, k -> hasPermission(base, node));
    }

    @Override
    public void invalidatePermissionCache(final UUID uuid) {
        invalidateCache(uuid);
    }

    public void invalidateCache(final UUID uuid) {
        if (ess.getSettings().isDebug()) {
            ess.getLogger().log(Level.INFO, "Invalidating permission cache for " + uuid);
        }
        permissionCache.remove(uuid);
    }

    @Override
    public List<String> getGroups() {
        final List<String> groups = new ArrayList<>();
        for (final Group group : luckPerms.getGroupManager().getLoadedGroups()) {
            groups.add(group.getName());
        }

        // Also add the vault group names for backwards compatibility
        for (final String group : super.getGroups()) {
            if (!groups.contains(group)) {
                groups.add(group);
            }
        }

        return groups;
    }

    @Override
    public boolean isOfflinePermissionSet(UUID uuid, String node) {
        final net.luckperms.api.model.user.User user = this.luckPerms.getUserManager().loadUser(uuid).join();
        if (user == null) {
            return false;
        }

        final QueryOptions options = luckPerms.getContextManager().getStaticQueryOptions();
        return user.getCachedData().getPermissionData(options).checkPermission(node).asBoolean();
    }

    @Override
    public boolean tryProvider(Essentials ess) {
        final RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.luckPerms = provider.getProvider();
            this.ess = ess;
            this.recalculateSubscription = this.luckPerms.getEventBus().subscribe(
                ess,
                UserDataRecalculateEvent.class,
                event -> invalidateCache(event.getUser().getUniqueId())
            );
        }
        return luckPerms != null && super.tryProvider(ess);
    }

    private static final class Calculator {
        private final String id;
        private final Function<User, Iterable<String>> function;
        private final Supplier<Iterable<String>> suggestions;

        private Calculator(String id, Function<User, Iterable<String>> function, Supplier<Iterable<String>> suggestions) {
            this.id = id;
            this.function = function;
            this.suggestions = suggestions;
        }
    }

    // By combining all calculators into one, we only need to make one call to ess.getUser().
    private final class CombinedCalculator implements ContextCalculator<Player> {
        private final Set<Calculator> calculators = new HashSet<>();

        @Override
        public void calculate(final Player target, final ContextConsumer consumer) {
            // If the player doesn't exist in the UserMap, just skip
            // Ess will cause performance problems for permissions checks if it attempts to
            // perform i/o to load the user data otherwise.
            if (!ess.getUsers().getAllUserUUIDs().contains(target.getUniqueId())) {
                return;
            }

            final User user = ess.getUsers().loadUncachedUser(target.getUniqueId());

            // This will occur for first time players during join,
            // None of our contexts would apply to that kind of person anyway,
            // lets just skip :O
            if (user == null) {
                return;
            }

            for (Calculator calculator : this.calculators) {
                calculator.function.apply(user).forEach(value -> consumer.accept(calculator.id, value));
            }
        }

        @Override
        public ContextSet estimatePotentialContexts() {
            final ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
            for (Calculator calculator : this.calculators) {
                calculator.suggestions.get().forEach(value -> builder.add(calculator.id, value));
            }

            return builder.build();
        }
    }
}
