package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Statistic;

import java.util.Collections;
import java.util.List;

public class Commandplaytime extends EssentialsCommand {
    // For some reason, in 1.13 PLAY_ONE_MINUTE = ticks played = what used to be PLAY_ONE_TICK
    // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/commits/b848d8ce633871b52115247b089029749c02f579
    private static final Statistic PLAY_ONE_TICK = EnumUtil.getStatistic("PLAY_ONE_MINUTE", "PLAY_ONE_TICK");

    public Commandplaytime() {
        super("playtime");
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        String displayName;
        long playtime;
        final String key;
        
        if (args.length > 0 && sender.isAuthorized("essentials.playtime.others")) {
            try {
                final IUser user = getPlayer(server, sender, args, 0);
                displayName = user.getDisplayName();
                playtime = user.getBase().getStatistic(PLAY_ONE_TICK);
            } catch (PlayerNotFoundException e) {
                // The ability to get the statistics of offline players is only available in 1.15.2+
                if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_15_2_R01)) {
                    throw e;
                }
                final User user = getPlayer(server, args, 0, true, true);
                displayName = user.getName(); // Vanished players will have their name as their display name
                playtime = Bukkit.getOfflinePlayer(user.getBase().getUniqueId()).getStatistic(PLAY_ONE_TICK);
                if (user.getBase().isOnline() && user.isVanished()) {
                    playtime = playtime - ((System.currentTimeMillis() - user.getLastVanishTime()) / 50L);
                }
            }
            key = "playtimeOther";
        } else if (sender.isPlayer()) {
            //noinspection ConstantConditions
            displayName = sender.getPlayer().getDisplayName();
            playtime = sender.getPlayer().getStatistic(PLAY_ONE_TICK);
            key = "playtime";
        } else {
            throw new NotEnoughArgumentsException();
        }

        final long playtimeMs = System.currentTimeMillis() - (playtime * 50L);
        sender.sendTl(key, DateUtil.formatDateDiff(playtimeMs), AdventureUtil.parsed(AdventureUtil.legacyToMini(displayName)));
    }
    
    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.playtime.others")) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
    
}
