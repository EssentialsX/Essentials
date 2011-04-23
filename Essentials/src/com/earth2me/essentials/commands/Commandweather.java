package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import org.bukkit.Server;

public class Commandweather extends EssentialsCommand
{
        public Commandweather()
	{
		super("weather");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
                switch (args.length)
                {
                case 0:
                        if (user.isAuthorized("essentials.weather"))
                        {
                             user.sendMessage("§cUsage: /" + commandLabel + " <storm/sun> [duration]");
                             break;
                        }
                        user.sendMessage("§cYou are not allowed to change the weather");
                        break;
                case 1:
                        if (user.isAuthorized("essentials.weather"))
                        {
                            if(args[0].equalsIgnoreCase("storm"))
                            {
                                user.getWorld().setStorm(true);
                                user.sendMessage("§7You set the weather in your world to storm");
                                break;
                            }
                            if(args[0].equalsIgnoreCase("sun"))
                            {
                                user.getWorld().setStorm(false);
                                user.sendMessage("§7You set the weather in your world to sun");
                                break;
                            }
                            user.sendMessage("§cUsage: /" + commandLabel + " <storm/sun> [duration]");
                        }
                        user.sendMessage("§cYou are not allowed to change the weather");
                        break;
                case 2:
                        if (user.isAuthorized("essentials.weather"))
                        {
                            if(args[0].equalsIgnoreCase("storm"))
                            {
                                user.getWorld().setStorm(true);
                                user.getWorld().setWeatherDuration(Integer.parseInt(args[1]) * 20);
                                user.sendMessage("§7You set the weather in your world to storm for " +  args[1] + " seconds");
                                break;
                            }
                            if(args[0].equalsIgnoreCase("sun"))
                            {
                                user.getWorld().setStorm(false);
                                user.getWorld().setWeatherDuration(Integer.parseInt(args[1]) * 20);
                                user.sendMessage("§7You set the weather in your world to sun for " +  args[1] + " seconds");
                                break;
                            }
                            user.sendMessage("§cUsage: /" + commandLabel + " <storm/sun> [duration]");
                        }
                        user.sendMessage("§cYou are not allowed to change the weather");
                        break;
                }
        }
}
