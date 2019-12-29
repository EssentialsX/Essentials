package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Mob;
import com.earth2me.essentials.SpawnMob;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.collect.Lists;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;


public class Commandspawnmob extends EssentialsCommand {
    public Commandspawnmob() {
        super("spawnmob");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            final String mobList = SpawnMob.mobList(user);
            throw new NotEnoughArgumentsException(tl("mobsAvailable", mobList));
        }

        List<String> mobParts = SpawnMob.mobParts(args[0]);
        List<String> mobData = SpawnMob.mobData(args[0]);

        int mobCount = 1;
        if (args.length >= 2) {
            mobCount = Integer.parseInt(args[1]);
        }

        if (mobParts.size() > 1 && !user.isAuthorized("essentials.spawnmob.stack")) {
            throw new Exception(tl("cannotStackMob"));
        }

        if (args.length >= 3) {
            final User target = getPlayer(ess.getServer(), user, args, 2);
            SpawnMob.spawnmob(ess, server, user.getSource(), target, mobParts, mobData, mobCount);
            return;
        }

        SpawnMob.spawnmob(ess, server, user, mobParts, mobData, mobCount);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 3) {
            final String mobList = StringUtil.joinList(Mob.getMobList());
            throw new NotEnoughArgumentsException(tl("mobsAvailable", mobList));
        }

        List<String> mobParts = SpawnMob.mobParts(args[0]);
        List<String> mobData = SpawnMob.mobData(args[0]);
        int mobCount = Integer.parseInt(args[1]);

        final User target = getPlayer(ess.getServer(), args, 2, true, false);
        SpawnMob.spawnmob(ess, server, sender, target, mobParts, mobData, mobCount);
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList(SpawnMob.mobParts(args[0]));
        } else {
            return Collections.emptyList();
        }
    }
}
