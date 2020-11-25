package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class EssentialsTimer implements Runnable {
    private final transient IEssentials ess;
    private final transient Set<UUID> onlineUsers = new HashSet<>(); // Field is necessary for hidden users
    private final LinkedList<Double> history = new LinkedList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private final long maxTime = 10 * 1000000;
    @SuppressWarnings("FieldCanBeLocal")
    private final long tickInterval = 50;
    private transient long lastPoll = System.nanoTime();
    private int skip1 = 0;
    private int skip2 = 0;

    EssentialsTimer(final IEssentials ess) {
        this.ess = ess;
        history.add(20d);
    }

    @Override
    public void run() {
        final long startTime = System.nanoTime();
        final long currentTime = System.currentTimeMillis();
        long timeSpent = (startTime - lastPoll) / 1000;
        if (timeSpent == 0) {
            timeSpent = 1;
        }
        if (history.size() > 10) {
            history.remove();
        }
        final double tps = tickInterval * 1000000.0 / timeSpent;
        if (tps <= 21) {
            history.add(tps);
        }
        lastPoll = startTime;
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

    public double getAverageTPS() {
        double avg = 0;
        for (final Double f : history) {
            if (f != null) {
                avg += f;
            }
        }
        return avg / history.size();
    }
}
