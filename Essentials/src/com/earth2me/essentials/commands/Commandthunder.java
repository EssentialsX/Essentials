package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import org.bukkit.Server;

public class Commandthunder extends EssentialsCommand
{
        public Commandthunder()
	{
		super("thunder");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
                switch (args.length)
                {
                case 0:
                        if (user.isAuthorized("essentials.thunder"))
                        {
                             user.sendMessage("§cUsage: /" + commandLabel + " <true/false> [duration]");
                             break;
                        }
                        user.sendMessage("§cYou are not allowed to change the thunder");
                        break;
                case 1:
                        if (user.isAuthorized("essentials.thunder"))
                        {
                            if(args[0].equalsIgnoreCase("true"))
                            {
                                user.getWorld().setThundering(true);
                                user.sendMessage("§7You enabled thunder in your world");
                                break;
                            }
                            if(args[0].equalsIgnoreCase("false"))
                            {
                                user.getWorld().setThundering(false);
                                user.sendMessage("§7You disabled thunder in your world");
                                break;
                            }
                             user.sendMessage("§cUsage: /" + commandLabel + " <true/false> [duration]");
                        }
                        user.sendMessage("§cYou are not allowed to change the thunder");
                        break;
                case 2:
                        if (user.isAuthorized("essentials.thunder"))
                        {
                            if(args[0].equalsIgnoreCase("true"))
                            {
                                user.getWorld().setThundering(true);
                                user.getWorld().setWeatherDuration(Integer.parseInt(args[1]) * 20);
                                user.sendMessage("§7You enabled thunder in your world for " + args[1] + " seconds");
                                break;
                            }
                            if(args[0].equalsIgnoreCase("false"))
                            {
                                user.getWorld().setThundering(false);
                                user.getWorld().setWeatherDuration(Integer.parseInt(args[1]) * 20);
                                user.sendMessage("§7You disabled thunder in your world for " + args[1] + " seconds");
                                break;
                            }
                             user.sendMessage("§cUsage: /" + commandLabel + " <true/false> [duration]");
                        }
                        user.sendMessage("§cYou are not allowed to change the thunder");
                        break;
                }
    }
}
