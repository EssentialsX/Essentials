package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.Server;
import org.bukkit.Statistic;

import java.util.Locale;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;


public class Commandwhois extends EssentialsCommand {
    public Commandwhois() {
        super("whois");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        User user = getPlayer(server, sender, args, 0);

        sender.sendMessage(tl("whoisTop", user.getName()));
        user.setDisplayNick();
        sender.sendMessage(tl("whoisNick", user.getDisplayName()));
        sender.sendMessage(tl("whoisUuid", user.getBase().getUniqueId().toString()));
        sender.sendMessage(tl("whoisHealth", user.getBase().getHealth()));
        sender.sendMessage(tl("whoisHunger", user.getBase().getFoodLevel(), user.getBase().getSaturation()));
        sender.sendMessage(tl("whoisExp", SetExpFix.getTotalExperience(user.getBase()), user.getBase().getLevel()));
        sender.sendMessage(tl("whoisLocation", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ()));
        long playtimeMs = System.currentTimeMillis() - (user.getBase().getStatistic(Statistic.PLAY_ONE_TICK) * 50);
        sender.sendMessage(tl("whoisPlaytime", DateUtil.formatDateDiff(playtimeMs)));
        if (!ess.getSettings().isEcoDisabled()) {
            sender.sendMessage(tl("whoisMoney", NumberUtil.displayCurrency(user.getMoney(), ess)));
        }
        if (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.whois.ip")) {
            sender.sendMessage(tl("whoisIPAddress", user.getBase().getAddress().getAddress().toString()));
        }
        final String location = user.getGeoLocation();
        if (location != null && (!sender.isPlayer() || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show"))) {
            sender.sendMessage(tl("whoisGeoLocation", location));
        }
        sender.sendMessage(tl("whoisGamemode", tl(user.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH))));
        sender.sendMessage(tl("whoisGod", (user.isGodModeEnabled() ? tl("true") : tl("false"))));
        sender.sendMessage(tl("whoisOp", (user.getBase().isOp() ? tl("true") : tl("false"))));
        sender.sendMessage(tl("whoisFly", user.getBase().getAllowFlight() ? tl("true") : tl("false"), user.getBase().isFlying() ? tl("flying") : tl("notFlying")));
        if (user.isAfk()) {
            sender.sendMessage(tl("whoisAFKSince", tl("true"), DateUtil.formatDateDiff(user.getAfkSince())));
        } else {
            sender.sendMessage(tl("whoisAFK", tl("false")));
        }
        sender.sendMessage(tl("whoisJail", (user.isJailed() ? user.getJailTimeout() > 0 ? DateUtil.formatDateDiff(user.getJailTimeout()) : tl("true") : tl("false"))));
        sender.sendMessage(tl("whoisMuted", (user.isMuted() ? user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : tl("true") : tl("false"))));

    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
