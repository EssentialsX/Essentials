package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.Server;
import org.bukkit.Statistic;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Commandwhois extends EssentialsCommand {
    // For some reason, in 1.13 PLAY_ONE_MINUTE = ticks played = what used to be PLAY_ONE_TICK
    // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/commits/b848d8ce633871b52115247b089029749c02f579
    private static final Statistic PLAY_ONE_TICK = EnumUtil.getStatistic("PLAY_ONE_MINUTE", "PLAY_ONE_TICK");

    public Commandwhois() {
        super("whois");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final User user = getPlayer(server, sender, args, 0);

        sender.sendTl("whoisTop", user.getName());
        user.setDisplayNick();
        sender.sendTl("whoisNick", user.getDisplayName());
        sender.sendTl("whoisUuid", user.getBase().getUniqueId().toString());
        sender.sendTl("whoisHealth", user.getBase().getHealth());
        sender.sendTl("whoisHunger", user.getBase().getFoodLevel(), user.getBase().getSaturation());
        sender.sendTl("whoisExp", SetExpFix.getTotalExperience(user.getBase()), user.getBase().getLevel());
        sender.sendTl("whoisLocation", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ());
        final long playtimeMs = System.currentTimeMillis() - (user.getBase().getStatistic(PLAY_ONE_TICK) * 50L);
        sender.sendTl("whoisPlaytime", DateUtil.formatDateDiff(playtimeMs));
        if (!ess.getSettings().isEcoDisabled()) {
            sender.sendTl("whoisMoney", NumberUtil.displayCurrency(user.getMoney(), ess));
        }
        if (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.whois.ip")) {
            sender.sendTl("whoisIPAddress", user.getBase().getAddress().getAddress().toString());
        }
        final String location = user.getGeoLocation();
        if (location != null && (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendTl("whoisGeoLocation", location);
        }
        sender.sendTl("whoisGamemode", sender.tl(user.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH)));
        sender.sendTl("whoisGod", AdventureUtil.parsed(user.isGodModeEnabled() ? sender.tl("true") : sender.tl("false")));
        sender.sendTl("whoisOp", AdventureUtil.parsed(user.getBase().isOp() ? sender.tl("true") : sender.tl("false")));
        sender.sendTl("whoisFly", AdventureUtil.parsed(user.getBase().getAllowFlight() ? sender.tl("true") : sender.tl("false")), AdventureUtil.parsed(user.getBase().isFlying() ? sender.tl("flying") : sender.tl("notFlying")));
        sender.sendTl("whoisSpeed", user.getBase().isFlying() ? user.getBase().getFlySpeed() : user.getBase().getWalkSpeed());
        if (user.isAfk()) {
            sender.sendTl("whoisAFKSince", AdventureUtil.parsed(sender.tl("true")), DateUtil.formatDateDiff(user.getAfkSince()));
        } else {
            sender.sendTl("whoisAFK", AdventureUtil.parsed(sender.tl("false")));
        }
        sender.sendTl("whoisJail", AdventureUtil.parsed(user.isJailed() ? user.getJailTimeout() > 0 ? user.getFormattedJailTime() : sender.tl("true") : sender.tl("false")));

        final long muteTimeout = user.getMuteTimeout();
        if (!user.hasMuteReason()) {
            sender.sendTl("whoisMuted", AdventureUtil.parsed(user.isMuted() ? muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : sender.tl("true") : sender.tl("false")));
        } else {
            sender.sendTl("whoisMutedReason", AdventureUtil.parsed(user.isMuted() ? muteTimeout > 0 ? DateUtil.formatDateDiff(muteTimeout) : sender.tl("true") : sender.tl("false")),
                user.getMuteReason());
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
