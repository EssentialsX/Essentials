package net.essentialsx.discordlink;

import com.earth2me.essentials.IEssentialsModule;
import com.google.common.base.Preconditions;
import net.ess3.api.IUser;
import net.essentialsx.api.v2.events.discordlink.DiscordLinkStatusChangeEvent;
import net.essentialsx.api.v2.services.discord.InteractionMember;
import net.essentialsx.api.v2.services.discordlink.DiscordLinkService;
import net.essentialsx.discordlink.rolesync.RoleSyncManager;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class AccountLinkManager implements IEssentialsModule, DiscordLinkService {
    private static final char[] CODE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private final EssentialsDiscordLink ess;
    private final AccountStorage storage;
    private final RoleSyncManager roleSyncManager;

    private final Map<String, UUID> codeToUuidMap = new ConcurrentHashMap<>();

    public AccountLinkManager(EssentialsDiscordLink ess, AccountStorage storage, RoleSyncManager roleSyncManager) {
        this.ess = ess;
        this.storage = storage;
        this.roleSyncManager = roleSyncManager;
    }

    public String createCode(final UUID uuid) throws IllegalArgumentException {
        final Optional<Map.Entry<String, UUID>> prevCode = codeToUuidMap.entrySet().stream().filter(stringUUIDEntry -> stringUUIDEntry.getValue().equals(uuid)).findFirst();
        if (prevCode.isPresent()) {
            throw new IllegalArgumentException(prevCode.get().getKey());
        }

        final String code = generateCode();

        codeToUuidMap.put(code, uuid);
        return code;
    }

    public UUID getPendingUUID(final String code) {
        return codeToUuidMap.remove(code);
    }

    @Override
    public String getDiscordId(final UUID uuid) {
        return storage.getDiscordId(uuid);
    }

    public IUser getUser(final String discordId) {
        final UUID uuid = getUUID(discordId);
        if (uuid == null) {
            return null;
        }
        return ess.getEss().getUser(uuid);
    }

    @Override
    public UUID getUUID(final String discordId) {
        return storage.getUUID(discordId);
    }

    @Override
    public boolean unlinkAccount(InteractionMember member) {
        Preconditions.checkNotNull(member, "member cannot be null");

        return isLinked(member.getId()) && removeAccount(member, DiscordLinkStatusChangeEvent.Cause.UNSYNC_API);
    }

    public boolean removeAccount(final InteractionMember member, final DiscordLinkStatusChangeEvent.Cause cause) {
        final UUID uuid = getUUID(member.getId());
        if (storage.remove(member.getId())) {
            ensureAsync(() -> {
                final IUser user = ess.getEss().getUser(uuid);
                ensureSync(() -> ess.getServer().getPluginManager().callEvent(new DiscordLinkStatusChangeEvent(user, member, member.getId(), false, cause)));

                roleSyncManager.unSync(uuid, member.getId());
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean unlinkAccount(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid cannot be null");

        if (!isLinked(uuid)) {
            return false;
        }

        ensureAsync(() -> removeAccount(ess.getEss().getUser(uuid), DiscordLinkStatusChangeEvent.Cause.UNSYNC_API));
        return true;
    }

    public boolean removeAccount(final IUser user, final DiscordLinkStatusChangeEvent.Cause cause) {
        final String id = getDiscordId(user.getBase().getUniqueId());
        if (storage.remove(user.getBase().getUniqueId())) {
            ess.getApi().getMemberById(id).thenAccept(member -> ensureSync(() ->
                    ess.getServer().getPluginManager().callEvent(new DiscordLinkStatusChangeEvent(user, member, id, false, cause))));

            ensureAsync(() -> roleSyncManager.unSync(user.getBase().getUniqueId(), id));
            return true;
        }
        return false;
    }

    @Override
    public boolean linkAccount(UUID uuid, InteractionMember member) {
        Preconditions.checkNotNull(uuid, "uuid cannot be null");
        Preconditions.checkNotNull(member, "member cannot be null");

        if (isLinked(uuid) || isLinked(member.getId())) {
            return false;
        }

        registerAccount(uuid, member, DiscordLinkStatusChangeEvent.Cause.SYNC_API);
        return true;
    }

    public void registerAccount(final UUID uuid, final InteractionMember member, final DiscordLinkStatusChangeEvent.Cause cause) {
        storage.add(uuid, member.getId());
        ensureAsync(() -> roleSyncManager.sync(uuid, member.getId()));
        ensureAsync(() -> {
            final IUser user = ess.getEss().getUser(uuid);
            ensureSync(() -> ess.getServer().getPluginManager().callEvent(new DiscordLinkStatusChangeEvent(user, member, member.getId(), true, cause)));
        });
    }

    private void ensureSync(final Runnable runnable) {
        if (ess.getServer().isPrimaryThread()) {
            runnable.run();
            return;
        }
        ess.getEss().scheduleGlobalDelayedTask(runnable);
    }

    private void ensureAsync(final Runnable runnable) {
        if (!ess.getServer().isPrimaryThread()) {
            runnable.run();
            return;
        }
        ess.getEss().runTaskAsynchronously(runnable);
    }

    private String generateCode() {
        final char[] code = new char[8];
        final Random random = ThreadLocalRandom.current();

        for (int i = 0; i < 8; i++) {
            code[i] = CODE_CHARACTERS[random.nextInt(CODE_CHARACTERS.length)];
        }
        final String result = new String(code);

        if (codeToUuidMap.containsKey(result)) {
            // If this happens, buy a lottery ticket.
            return generateCode();
        }
        return result;
    }
}
