package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;

public class Commandburn extends EssentialsCommand
{

    public Commandburn()
    {
        super("burn");
    }

    @Override
    public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
    {
        if (args.length < 2)
        {
            user.sendMessage("§cUsage: /burn [player] [seconds]");
            return;
        }

        User.charge(user, this);
        for (Player p : server.matchPlayer(args[0]))
        {
            p.setFireTicks(Integer.parseInt(args[1]) * 20);
            user.sendMessage("§cYou set " + p.getDisplayName() + " on fire for " + Integer.parseInt(args[1]) + "seconds.");
        }
    }
}
