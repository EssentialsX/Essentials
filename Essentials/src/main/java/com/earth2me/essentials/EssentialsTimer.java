package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class EssentialsTimer implements Runnable {
    private final transient IEssentials ess;
    private final transient Set<UUID> onlineUsers = new HashSet<>(); // Field is necessary for hidden users
    private static final long maxTime = 10 * 1000000;
    private int skip1 = 0;
    private int skip2 = 0;

    EssentialsTimer(final IEssentials ess) {
        this.ess = ess;
    }

    @Override
    public void run() {
        final long startTime = System.nanoTime();
        final long currentTime = System.currentTimeMillis();

        int count = 0;
        onlineUsers.clear();
        for (final Player player : ess.getOnlinePlayers()) {
            count++;
            if (skip1 > 0) {
                skip1--;
                continue;
            }
            if (count % 10 == 0) {
                if (System.nanoTime() - startTime > maxTime / 2) {
                    skip1 = count - 1;
                    break;
                }
            }
            try {
                final User user = ess.getUser(player);
                onlineUsers.add(user.getBase().getUniqueId());
                user.setLastOnlineActivity(currentTime);
                user.checkActivity();
            } catch (final Exception e) {
                ess.getLogger().log(Level.WARNING, "EssentialsTimer Error:", e);
            }
        }

        count = 0;
        final Iterator<UUID> iterator = onlineUsers.iterator();
        while (iterator.hasNext()) {
            count++;
            if (skip2 > 0) {
                skip2--;
                continue;
            }
            if (count % 10 == 0) {
                if (System.nanoTime() - startTime > maxTime) {
                    skip2 = count - 1;
                    break;
                }
            }
            final User user = ess.getUser(iterator.next());
            // Not sure why this would happen, but it does
            if (user == null) {
                iterator.remove();
                continue;
            }
            if (user.getLastOnlineActivity() < currentTime && user.getLastOnlineActivity() > user.getLastLogout()) {
                if (!user.isHidden()) {
                    user.setLastLogout(user.getLastOnlineActivity());
                }
                iterator.remove();
                continue;
            }
            user.checkMuteTimeout(currentTime);
            user.checkJailTimeout(currentTime);
            user.resetInvulnerabilityAfterTeleport();
        }
    }
}
