package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Offence;
import com.earth2me.essentials.User;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

public class Commandoffences extends EssentialsCommand {

    public Commandoffences() {
        super("offences");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        final String targetName;
        final boolean isOwnOffences;

        if (args.length >= 1) {
            if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.offences.others")) {
                throw new TranslatableException("noPermission");
            }

            String resolvedName;
            try {
                final User target = getPlayer(server, args, 0, true, true);
                resolvedName = target.getName();
            } catch (final PlayerNotFoundException e) {
                resolvedName = args[0];
            }

            targetName = resolvedName;
            isOwnOffences = sender.isPlayer() && ess.getUser(sender.getPlayer()).getName().equalsIgnoreCase(targetName);

        } else {
            if (!sender.isPlayer()) {
                throw new NotEnoughArgumentsException();
            }
            targetName = ess.getUser(sender.getPlayer()).getName();
            isOwnOffences = true;
        }

        final List<Offence> offenceList = ess.getOffenceRegistry().getOffences(targetName);

        if (offenceList.isEmpty()) {
            sender.sendTl("offencesNone", targetName);
            return;
        }

        final long totalNoKicks = offenceList.stream()
                .filter(o -> o.getType() != Offence.Type.KICK)
                .count();

        final int page = 1;
        final int totalPages = 1;

        if (isOwnOffences) {
            sender.sendTl("offencesHeader", page, totalPages, offenceList.size());
        } else {
            sender.sendTl("offencesHeaderOther", targetName, page, totalPages, offenceList.size());
        }

        sender.sendTl("offencesNoKicks", totalNoKicks);

        for (final Offence o : offenceList) {
            final String timeAgo = formatTimeAgo(System.currentTimeMillis() - o.getTimestamp());
            final String typeLabel = formatType(o.getType());
            final String reason = (o.getReason() != null && !o.getReason().isEmpty()) ? o.getReason() : "No reason given";
            final String staff = o.getStaffName() != null ? o.getStaffName() : "Unknown";

            sender.sendTl("offencesEntry", timeAgo, typeLabel, reason, staff);
        }
    }

    private String formatTimeAgo(final long millis) {
        long seconds = millis / 1000;
        final long days = seconds / 86400;
        seconds %= 86400;
        final long hours = seconds / 3600;
        seconds %= 3600;
        final long minutes = seconds / 60;
        seconds %= 60;

        final StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            sb.append(minutes).append("m ");
        }
        sb.append(seconds).append("s");
        return sb.toString().trim();
    }

    private String formatType(final Offence.Type type) {
        switch (type) {
            case BAN:
                return "ban";
            case TEMPBAN:
                return "tempban";
            case BANIP:
                return "ban-ip";
            case TEMPBANIP:
                return "tempban-ip";
            case MUTE:
                return "mute";
            case TEMPMUTE:
                return "tempmute";
            case KICK:
                return "kick";
            default:
                return "unknown";
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && sender.isPlayer() && ess.getUser(sender.getPlayer()).isAuthorized("essentials.offences.others")) {
            return getPlayers(sender);
        }
        return Collections.emptyList();
    }
}

