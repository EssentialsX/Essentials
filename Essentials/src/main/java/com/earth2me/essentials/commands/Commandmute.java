package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.OfflinePlayerStub;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import net.ess3.api.TranslatableException;
import net.ess3.api.events.MuteStatusChangeEvent;
import org.bukkit.Server;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public class Commandmute extends EssentialsCommand {
    public Commandmute() {
        super("mute");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        boolean nomatch = false;
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        User user;
        try {
            user = getPlayer(server, args, 0, true, true);
        } catch (final PlayerNotFoundException e) {
            nomatch = true;
            user = ess.getUser(new OfflinePlayerStub(args[0], ess.getServer()));
        }
        if (!user.getBase().isOnline() && sender.isPlayer()) {
            if (!sender.isAuthorized("essentials.mute.offline")) {
                throw new TranslatableException("muteExemptOffline");
            }
        } else if (user.isAuthorized("essentials.mute.exempt")) {
            throw new TranslatableException("muteExempt");
        }

        long muteTimestamp = 0;
        final String time;
        String muteReason = null;

        if (args.length > 1) {
            time = args[1];
            try {
                muteTimestamp = DateUtil.parseDateDiff(time, true);
                muteReason = getFinalArg(args, 2);
            } catch (final Exception e) {
                muteReason = getFinalArg(args, 1);
            }
            final long maxMuteLength = ess.getSettings().getMaxMute() * 1000;
            if (maxMuteLength > 0 && ((muteTimestamp - GregorianCalendar.getInstance().getTimeInMillis()) > maxMuteLength) && sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.mute.unlimited")) {
                sender.sendTl("oversizedMute");
                throw new NoChargeException();
            }
        }

        final boolean willMute = (args.length > 1) || !user.getMuted();
        final User controller = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;
        final MuteStatusChangeEvent event = new MuteStatusChangeEvent(user, controller, willMute, muteTimestamp, muteReason);
        ess.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (muteReason != null) {
                user.setMuteReason(muteReason.isEmpty() ? null : muteReason);
                user.setMuted(true);
            } else {
                user.setMuted(!user.getMuted());
                if (!user.getMuted()) {
                    user.setMuteReason(null);
                }
            }
            user.setMuteTimeout(muteTimestamp);
            final boolean muted = user.getMuted();
            final String muteTime = DateUtil.formatDateDiff(muteTimestamp);

            if (nomatch) {
                sender.sendTl("userUnknown", user.getName());
            }

            if (muted) {
                if (muteTimestamp > 0) {
                    if (!user.hasMuteReason()) {
                        sender.sendTl("mutedPlayerFor", user.getDisplayName(), muteTime);
                        user.sendTl("playerMutedFor", muteTime);
                    } else {
                        sender.sendTl("mutedPlayerForReason", user.getDisplayName(), muteTime, user.getMuteReason());
                        user.sendTl("playerMutedForReason", muteTime, user.getMuteReason());
                    }
                } else {
                    if (!user.hasMuteReason()) {
                        sender.sendTl("mutedPlayer", user.getDisplayName());
                        user.sendTl("playerMuted");
                    } else {
                        sender.sendTl("mutedPlayerReason", user.getDisplayName(), user.getMuteReason());
                        user.sendTl("playerMutedReason", user.getMuteReason());
                    }
                }

                final String tlKey;
                final Object[] objects;
                if (user.hasMuteReason()) {
                    if (muteTimestamp > 0) {
                        tlKey = "muteNotifyForReason";
                        objects = new Object[]{sender.getSender().getName(), user.getName(), muteTime, user.getMuteReason()};
                    } else {
                        tlKey = "muteNotify";
                        objects = new Object[]{sender.getSender().getName(), user.getName(), user.getMuteReason()};
                    }
                } else {
                    tlKey = muteTimestamp > 0 ? "muteNotifyFor" : "muteNotify";
                    objects = new Object[]{sender.getSender().getName(), user.getName(), muteTime};
                }

                ess.getLogger().log(Level.INFO, tlLiteral(tlKey, objects));
                ess.broadcastTl(null, "essentials.mute.notify", tlKey, objects);
            } else {
                sender.sendTl("unmutedPlayer", user.getDisplayName());
                user.sendTl("playerUnmuted");
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return COMMON_DATE_DIFFS; // Date diff can span multiple words
        }
    }
}
