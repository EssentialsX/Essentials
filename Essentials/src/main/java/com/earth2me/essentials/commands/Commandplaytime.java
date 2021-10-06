package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

import java.util.Collections;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.Statistic;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.EnumUtil;

public class Commandplaytime extends EssentialsCommand {
    // For some reason, in 1.13 PLAY_ONE_MINUTE = ticks played = what used to be PLAY_ONE_TICK
    // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/commits/b848d8ce633871b52115247b089029749c02f579
    private static final Statistic PLAY_ONE_TICK = EnumUtil.getStatistic("PLAY_ONE_MINUTE", "PLAY_ONE_TICK");

    public Commandplaytime() {
        super("playtime");
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        final IUser target;
        final String key;
        
        if (args.length > 0 && sender.isAuthorized("essentials.playtime.others", ess)) {
            target = getPlayer(server, sender, args, 0);
            key = "playtimeOther";
        } else if (sender.isPlayer()) {
            target = sender.getUser(ess);
            key = "playtime";
        } else {
            throw new NotEnoughArgumentsException();
        }

        final long playtimeMs = System.currentTimeMillis() - (target.getBase().getStatistic(PLAY_ONE_TICK) * 50);
        sender.sendMessage(tl(key, DateUtil.formatDateDiff(playtimeMs), target.getDisplayName()));
    }
    
    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.playtime.others", ess)) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
    
}
