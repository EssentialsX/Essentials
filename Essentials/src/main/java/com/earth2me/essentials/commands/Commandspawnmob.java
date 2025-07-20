package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Mob;
import com.earth2me.essentials.SpawnMob;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.collect.Lists;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

public class Commandspawnmob extends EssentialsCommand {
    public Commandspawnmob() {
        super("spawnmob");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException(user.playerTl("mobsAvailable", StringUtil.joinList(Mob.getMobList())));
        }

        final List<String> mobParts = SpawnMob.mobParts(args[0]);
        final List<String> mobData = SpawnMob.mobData(args[0]);

        int mobCount = 1;
        if (args.length >= 2) {
            mobCount = Integer.parseInt(args[1]);
        }

        if (mobParts.size() > 1 && !user.isAuthorized("essentials.spawnmob.stack")) {
            throw new TranslatableException("cannotStackMob");
        }

        if (args.length >= 3) {
            SpawnMob.spawnmob(ess, server, user.getSource(), getPlayer(ess.getServer(), user, args, 2), mobParts, mobData, mobCount);
            return;
        }

        SpawnMob.spawnmob(ess, server, user, mobParts, mobData, mobCount);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 3) {
            throw new NotEnoughArgumentsException(sender.tl("mobsAvailable", StringUtil.joinList(Mob.getMobList())));
        }

        final List<String> mobParts = SpawnMob.mobParts(args[0]);
        final List<String> mobData = SpawnMob.mobData(args[0]);

        SpawnMob.spawnmob(ess, server, sender, getPlayer(ess.getServer(), args, 2, true, false), mobParts, mobData, Integer.parseInt(args[1]));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList(Mob.getMobList());
        } else {
            return Collections.emptyList();
        }
    }
}
