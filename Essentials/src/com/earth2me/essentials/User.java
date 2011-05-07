package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class User extends UserData implements Comparable<User>, IReplyTo
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private boolean justPortaled = false;
	private CommandSender replyTo = null;
	private User teleportRequester;
	private boolean teleportRequestHere;
	private Teleport teleport;
	private long lastActivity;

	User(Player base, Essentials ess)
	{
		super(base, ess);
		teleport = new Teleport(this, ess);
	}

	User update(Player base)
	{
		setBase(base);
		return this;
	}

	public boolean isAuthorized(IEssentialsCommand cmd)
	{
		return isAuthorized("essentials." + (cmd.getName().equals("r") ? "msg" : cmd.getName()));
	}

	public boolean isAuthorized(String node)
	{
		if (isOp())
		{
			return true;
		}

		if (isJailed())
		{
			return false;
		}

		try
		{
			return com.nijikokun.bukkit.Permissions.Permissions.Security.permission(base, node);
		}
		catch (Throwable ex)
		{
			String[] cmds = node.split("\\.", 2);
			return !ess.getSettings().isCommandRestricted(cmds[cmds.length - 1]);
		}
	}

	public void healCooldown() throws Exception
	{
		Calendar now = new GregorianCalendar();
		if (getLastHealTimestamp() > 0)
		{
			double cooldown = ess.getSettings().getHealCooldown();
			Calendar cooldownTime = new GregorianCalendar();
			cooldownTime.setTimeInMillis(getLastHealTimestamp());
			cooldownTime.add(Calendar.SECOND, (int)cooldown);
			cooldownTime.add(Calendar.MILLISECOND, (int)((cooldown * 1000.0) % 1000.0));
			if (cooldownTime.after(now) && !isAuthorized("essentials.heal.cooldown.bypass"))
			{
				throw new Exception("Time before next heal: " + Util.formatDateDiff(cooldownTime.getTimeInMillis()));
			}
		}
		setLastHealTimestamp(now.getTimeInMillis());
	}

	public void giveMoney(double value)
	{
		if (value == 0)
		{
			return;
		}
		setMoney(getMoney() + value);
		sendMessage("§a" + Util.formatCurrency(value) + " has been added to your account.");
	}

	public void payUser(User reciever, double value) throws Exception
	{
		if (value == 0)
		{
			return;
		}
		if (!canAfford(value))
		{
			throw new Exception("You do not have sufficient funds.");
		}
		else
		{
			setMoney(getMoney() - value);
			reciever.setMoney(reciever.getMoney() + value);
			sendMessage("§a" + Util.formatCurrency(value) + " has been sent to " + reciever.getDisplayName());                        
			reciever.sendMessage("§a" + Util.formatCurrency(value) + " has been recieved from " + getDisplayName());
		}
	}

	public void takeMoney(double value)
	{
		if (value == 0)
		{
			return;
		}
		setMoney(getMoney() - value);
		sendMessage("§c" + Util.formatCurrency(value) + " has been taken from your account.");
	}

	public void charge(String cmd) throws Exception
	{
		if (isAuthorized("essentials.nocommandcost.all")
			|| isAuthorized("essentials.nocommandcost." + cmd))
		{
			return;
		}
		double mon = getMoney();
		double cost = ess.getSettings().getCommandCost(cmd.startsWith("/") ? cmd.substring(1) : cmd);
		if (mon < cost && !isAuthorized("essentials.eco.loan"))
		{
			throw new Exception("You do not have sufficient funds.");
		}
		takeMoney(cost);
	}

	public void canAfford(String cmd) throws Exception
	{
		double mon = getMoney();
		double cost = ess.getSettings().getCommandCost(cmd.startsWith("/") ? cmd.substring(1) : cmd);
		if (mon < cost && !isAuthorized("essentials.eco.loan"))
		{
			throw new Exception("You do not have sufficient funds.");
		}
	}

	public boolean canAfford(double cost)
	{
		double mon = getMoney();
		if (mon < cost && !isAuthorized("essentials.eco.loan"))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public void canAfford(IEssentialsCommand cmd) throws Exception
	{
		canAfford(cmd.getName());
	}

	public void dispose()
	{
		this.base = new OfflinePlayer(getName());
	}

	public void charge(IEssentialsCommand cmd) throws Exception
	{
		charge(cmd.getName());
	}

	public boolean getJustPortaled()
	{
		return justPortaled;
	}

	public void setJustPortaled(boolean value)
	{
		justPortaled = value;
	}

	public void setReplyTo(CommandSender user)
	{
		replyTo = user;
	}

	public CommandSender getReplyTo()
	{
		return replyTo;
	}

	public int compareTo(User t)
	{
		return ChatColor.stripColor(this.getDisplayName()).compareToIgnoreCase(ChatColor.stripColor(t.getDisplayName()));
	}

	public Boolean canSpawnItem(int itemId)
	{
		return !ess.getSettings().itemSpawnBlacklist().contains(itemId);
	}

	public void setHome()
	{
		setHome(getLocation(), true);
	}

	public void setHome(boolean defaultHome)
	{
		setHome(getLocation(), defaultHome);
	}

	public void setLastLocation()
	{
		setLastLocation(getLocation());
	}

	public void requestTeleport(User player, boolean here)
	{
		teleportRequester = player;
		teleportRequestHere = here;
	}

	public User getTeleportRequest()
	{
		return teleportRequester;
	}

	public boolean isTeleportRequestHere()
	{
		return teleportRequestHere;
	}

	public String getNick()
	{
		String nickname = getNickname();
		if (ess.getSettings().isCommandDisabled("nick") || nickname == null || nickname.isEmpty() || nickname.equals(getName()))
		{
			nickname = getName();
		}
		else
		{
			nickname = ess.getSettings().getNicknamePrefix() + nickname;
		}
		if (isOp())
		{
			nickname = ess.getSettings().getOperatorColor().toString() + nickname + "§f";
		}
		return nickname;
	}

	public Teleport getTeleport()
	{
		return teleport;
	}

	public long getLastActivity()
	{
		return lastActivity;
	}

	public void setLastActivity(long timestamp)
	{
		lastActivity = timestamp;
	}
}