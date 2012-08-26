package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
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
		final float speed = getMoveSpeed(args[1]);
		speedOtherPlayers(server, sender, isFly, true, speed, args[2]);
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
		boolean isBypass = user.isAuthorized("essentials.speed.bypass");
		if (args.length == 1)
		{
			isFly = user.isFlying();
			speed = getMoveSpeed(args[0]);
		}
		else
		{
			isFly = isFlyMode(args[0]);
			speed = getMoveSpeed(args[1]);
			if (args.length > 2 && user.isAuthorized("essentials.speed.others"))
			{
				speedOtherPlayers(server, user, isFly, isBypass, speed, args[2]);
				return;
			}
		}

		if (isFly)
		{
			user.setFlySpeed(getRealMoveSpeed(speed, isFly, isBypass));
			user.sendMessage(_("moveSpeed", _("flying"), speed, user.getDisplayName()));
		}
		else
		{
			user.setWalkSpeed(getRealMoveSpeed(speed, isFly, isBypass));
			user.sendMessage(_("moveSpeed", _("walking"), speed, user.getDisplayName()));
		}
	}

	private void speedOtherPlayers(final Server server, final CommandSender sender, final boolean isFly, final boolean isBypass, final float speed, final String target)
	{
		for (Player matchPlayer : server.matchPlayer(target))
		{
			if (isFly)
			{
				matchPlayer.setFlySpeed(getRealMoveSpeed(speed, isFly, isBypass));
				sender.sendMessage(_("moveSpeed", _("flying"), speed, matchPlayer.getDisplayName()));
			}
			else
			{
				matchPlayer.setWalkSpeed(getRealMoveSpeed(speed, isFly, isBypass));
				sender.sendMessage(_("moveSpeed", _("walking"), speed, matchPlayer.getDisplayName()));
			}
		}
	}

	private boolean isFlyMode(final String modeString) throws NotEnoughArgumentsException
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

	private float getMoveSpeed(final String moveSpeed) throws NotEnoughArgumentsException
	{
		float userSpeed;
		try
		{
			userSpeed = Float.parseFloat(moveSpeed);
			if (userSpeed > 10f)
			{
				userSpeed = 10f;
			}
			else if (userSpeed < 0f)
			{
				userSpeed = 0f;
			}
		}
		catch (NumberFormatException e)
		{
			throw new NotEnoughArgumentsException();
		}
		return userSpeed;
	}

	private float getRealMoveSpeed(final float userSpeed, final boolean isFly, final boolean isBypass)
	{
		final float defaultSpeed = isFly ? 0.1f : 0.2f;
		float maxSpeed = 1f;
		if (!isBypass)
		{
			maxSpeed = (float)(isFly ? ess.getSettings().getMaxFlySpeed() : ess.getSettings().getMaxWalkSpeed());
		}

		if (userSpeed < 1f)
		{
			return defaultSpeed * userSpeed;
		}
		else
		{
			float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
			return ratio + defaultSpeed;
		}
	}
}
