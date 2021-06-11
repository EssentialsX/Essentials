package com.earth2me.essentials.perm;

import com.earth2me.essentials.utils.TriState;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IPermissionsHandler {
    String getGroup(Player base);

    List<String> getGroups(Player base);

    boolean canBuild(Player base, String group);

    boolean inGroup(Player base, String group);

    boolean hasPermission(Player base, String node);

    // Does not check for * permissions
    boolean isPermissionSet(Player base, String node);

    TriState isPermissionSetExact(Player base, String node);

    String getPrefix(Player base);

    String getSuffix(Player base);

    void registerContext(String context, Function<Player, Iterable<String>> calculator, Supplier<Iterable<String>> suggestions);

    void unregisterContexts();

    String getBackendName();

    boolean tryProvider();
}
