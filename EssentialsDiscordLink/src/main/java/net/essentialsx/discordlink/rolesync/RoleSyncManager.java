package net.essentialsx.discordlink.rolesync;

import com.earth2me.essentials.UUIDPlayer;
import com.earth2me.essentials.utils.AdventureUtil;
import com.google.common.collect.BiMap;
import net.essentialsx.api.v2.events.discordlink.DiscordLinkStatusChangeEvent;
import net.essentialsx.api.v2.services.discord.InteractionMember;
import net.essentialsx.api.v2.services.discord.InteractionRole;
import net.essentialsx.discordlink.EssentialsDiscordLink;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.earth2me.essentials.I18n.tlLiteral;

public class RoleSyncManager implements Listener {
    private final EssentialsDiscordLink ess;
    private final Map<String, InteractionRole> groupToRoleMap = new HashMap<>();
    private final Map<String, String> roleIdToGroupMap = new HashMap<>();

    public RoleSyncManager(final EssentialsDiscordLink ess) {
        this.ess = ess;
        Bukkit.getPluginManager().registerEvents(this, ess);
        onReload();
        this.ess.getEss().runTaskTimerAsynchronously(() -> {
            if (groupToRoleMap.isEmpty() && roleIdToGroupMap.isEmpty()) {
                return;
            }

            final BiMap<String, String> uuidToDiscordCopy = ess.getAccountStorage().getRawStorageMap();
            final Map<String, InteractionRole> groupToRoleMapCopy = new HashMap<>(groupToRoleMap);
            final Map<String, String> roleIdToGroupMapCopy = new HashMap<>(roleIdToGroupMap);
            final boolean primaryOnly = ess.getSettings().isRoleSyncPrimaryGroupOnly();
            final boolean removeGroups = ess.getSettings().isRoleSyncRemoveGroups();
            final boolean removeRoles = ess.getSettings().isRoleSyncRemoveRoles();
            for (final Map.Entry<String, String> entry : uuidToDiscordCopy.entrySet()) {
                sync(new UUIDPlayer(UUID.fromString(entry.getKey())), entry.getValue(), groupToRoleMapCopy, roleIdToGroupMapCopy, primaryOnly, removeGroups, removeRoles);
            }
        }, 0, ess.getSettings().getRoleSyncResyncDelay() * 1200L);
    }

    public void sync(final UUID uuid, final String discordId) {
        final Map<String, InteractionRole> groupToRoleMapCopy = new HashMap<>(groupToRoleMap);
        final Map<String, String> roleIdToGroupMapCopy = new HashMap<>(roleIdToGroupMap);
        final boolean primaryOnly = ess.getSettings().isRoleSyncPrimaryGroupOnly();
        final boolean removeGroups = ess.getSettings().isRoleSyncRemoveGroups();
        final boolean removeRoles = ess.getSettings().isRoleSyncRemoveRoles();
        sync(new UUIDPlayer(uuid), discordId, groupToRoleMapCopy, roleIdToGroupMapCopy, primaryOnly, removeGroups, removeRoles);
    }

    public void sync(final Player player, final String discordId, final Map<String, InteractionRole> groupToRoleMap, final Map<String, String> roleIdToGroupMap,
                     final boolean primaryOnly, final boolean removeGroups, final boolean removeRoles) {
        final List<String> groups = primaryOnly ?
                Collections.singletonList(ess.getEss().getPermissionsHandler().getGroup(player)) : ess.getEss().getPermissionsHandler().getGroups(player);
        final InteractionMember member = ess.getApi().getMemberById(discordId).join();

        if (member == null) {
            if (ess.getSettings().isUnlinkOnLeave()) {
                ess.getLinkManager().removeAccount(ess.getEss().getUser(player), DiscordLinkStatusChangeEvent.Cause.UNSYNC_LEAVE);
            } else {
                unSync(player.getUniqueId(), discordId);
            }
            return;
        }

        final List<InteractionRole> toAdd = new ArrayList<>();
        final List<InteractionRole> toRemove = new ArrayList<>();

        for (final Map.Entry<String, InteractionRole> entry : groupToRoleMap.entrySet()) {
            if (groups.contains(entry.getKey()) && !member.hasRole(entry.getValue())) {
                toAdd.add(entry.getValue());
            } else if (removeRoles && !groups.contains(entry.getKey()) && member.hasRole(entry.getValue())) {
                toRemove.add(entry.getValue());
            }
        }

        for (final Map.Entry<String, String> entry : roleIdToGroupMap.entrySet()) {
            if (member.hasRole(entry.getKey()) && !groups.contains(entry.getValue())) {
                ess.getEss().getPermissionsHandler().addToGroup(player, entry.getValue());
            } else if (removeGroups && !member.hasRole(entry.getKey()) && groups.contains(entry.getValue())) {
                ess.getEss().getPermissionsHandler().removeFromGroup(player, entry.getValue());
            }
        }

        if (toAdd.isEmpty() && toRemove.isEmpty()) {
            return;
        }

        ess.getApi().modifyMemberRoles(member, toAdd, toRemove);
    }

    public void unSync(final UUID uuid, final String discordId) {
        final boolean removeGroups = ess.getSettings().isRoleSyncRemoveGroups();
        final boolean removeRoles = ess.getSettings().isRoleSyncRemoveRoles();
        if (!removeGroups && !removeRoles) {
            return;
        }

        final Map<String, InteractionRole> groupToRoleMapCopy = new HashMap<>(groupToRoleMap);
        final Map<String, String> roleIdToGroupMapCopy = new HashMap<>(roleIdToGroupMap);

        final Player player = new UUIDPlayer(uuid);
        final InteractionMember member = ess.getApi().getMemberById(discordId).join();

        if (removeGroups) {
            for (final String group : roleIdToGroupMapCopy.values()) {
                ess.getEss().getPermissionsHandler().removeFromGroup(player, group);
            }
        }

        // Check if the member is no longer in the guild (null), they don't have any roles anyway.
        if (removeRoles && member != null) {
            ess.getApi().modifyMemberRoles(member, null, groupToRoleMapCopy.values());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ess.getEss().runTaskAsynchronously(() -> {
            if (ess.getLinkManager().isLinked(event.getPlayer().getUniqueId())) {
                sync(event.getPlayer().getUniqueId(), ess.getLinkManager().getDiscordId(event.getPlayer().getUniqueId()));
            }
        });
    }

    public void onReload() {
        groupToRoleMap.clear();
        roleIdToGroupMap.clear();

        final List<String> groups = ess.getEss().getPermissionsHandler().getGroups();

        for (final Map.Entry<String, String> entry : ess.getSettings().getRoleSyncGroups().entrySet()) {
            if (isExampleRole(entry.getValue())) {
                continue;
            }

            final String group = entry.getKey();
            final InteractionRole role = ess.getApi().getRole(entry.getValue());
            if (!groups.contains(group)) {
                ess.getLogger().warning(AdventureUtil.miniToLegacy(tlLiteral("discordLinkInvalidGroup", group, entry.getValue(), groups)));
                continue;
            }
            if (role == null) {
                ess.getLogger().warning(AdventureUtil.miniToLegacy(tlLiteral("discordLinkInvalidRole", entry.getValue(), group)));
                continue;
            }

            if (role.isManaged() || role.isPublicRole()) {
                ess.getLogger().warning(AdventureUtil.miniToLegacy(tlLiteral("discordLinkInvalidRoleManaged", role.getName(), role.getId())));
                continue;
            }

            if (!role.canInteract()) {
                ess.getLogger().warning(AdventureUtil.miniToLegacy(tlLiteral("discordLinkInvalidRoleInteract", role.getName(), role.getId())));
                continue;
            }

            groupToRoleMap.put(group, role);
        }

        for (final Map.Entry<String, String> entry : ess.getSettings().getRoleSyncRoles().entrySet()) {
            if (isExampleRole(entry.getKey())) {
                continue;
            }

            final InteractionRole role = ess.getApi().getRole(entry.getKey());
            final String group = entry.getValue();
            if (role == null) {
                ess.getLogger().warning(AdventureUtil.miniToLegacy(tlLiteral("discordLinkInvalidRole", entry.getKey(), group)));
                continue;
            }
            if (!groups.contains(group)) {
                ess.getLogger().warning(AdventureUtil.miniToLegacy(tlLiteral("discordLinkInvalidGroup", group, entry.getKey(), groups)));
                continue;
            }

            roleIdToGroupMap.put(role.getId(), group);
        }
    }

    private boolean isExampleRole(final String role) {
        return role.equals("0") || role.equals("11111111111111111") || role.equals("22222222222222222") || role.equals("33333333333333333");
    }
}
