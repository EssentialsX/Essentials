package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandspeed extends EssentialsCommand
{
	public Commandspeed()
	{
		super("speed");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		final boolean isFly = isFlyMode(args[0]);
		final float speed = isMoveSpeed(args[1]);
		speedOtherPlayers(server, sender, isFly, speed, args[2]);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		boolean isFly;
		float speed;
		if (args.length == 1)
		{
			isFly = user.isFlying();
			speed = isMoveSpeed(args[0]);
		}
		else if (args.length == 2)
		{
			isFly = isFlyMode(args[0]);
			speed = isMoveSpeed(args[1]);
		}
		else
		{
			isFly = isFlyMode(args[0]);
			speed = isMoveSpeed(args[1]);
			speedOtherPlayers(server, user, isFly, speed, args[2]);
			return;
		}

		if (isFly)
		{
			user.setFlySpeed(speed);
		}
		else
		{
			user.setWalkSpeed(speed);
		}
	}

	private void speedOtherPlayers(final Server server, final CommandSender sender, final boolean isFly, final float speed, final String target)
	{
		for (Player matchPlayer : server.matchPlayer(target))
		{
			if (isFly)
			{
				matchPlayer.setFlySpeed(speed);
			}
			else
			{
				matchPlayer.setWalkSpeed(speed);
			}
		}
	}

	private boolean isFlyMode(String modeString) throws NotEnoughArgumentsException
	{
		boolean isFlyMode;
		if (modeString.contains("fly") || modeString.equalsIgnoreCase("f"))
		{
			isFlyMode = true;
		}
		else if (modeString.contains("walk") || modeString.contains("run")
				 || modeString.equalsIgnoreCase("w") || modeString.equalsIgnoreCase("r"))
		{
			isFlyMode = false;
		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
		return isFlyMode;
	}

	private float isMoveSpeed(String moveSpeed) throws NotEnoughArgumentsException
	{
		float speed;
		try
		{
			speed = Float.parseFloat(moveSpeed);
		}
		catch (NumberFormatException e)
		{
			throw new NotEnoughArgumentsException();
		}
		return speed;
	}
}
