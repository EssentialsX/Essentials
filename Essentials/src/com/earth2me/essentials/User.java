package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.register.payment.Method;
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
				throw new Exception(Util.format("timeBeforeHeal", Util.formatDateDiff(cooldownTime.getTimeInMillis())));
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
		sendMessage(Util.format("addedToAccount", Util.formatCurrency(value)));
	}

	public void payUser(User reciever, double value) throws Exception
	{
		if (value == 0)
		{
			return;
		}
		if (!canAfford(value))
		{
			throw new Exception(Util.i18n("notEnoughMoney"));
		}
		else
		{
			setMoney(getMoney() - value);
			reciever.setMoney(reciever.getMoney() + value);
			sendMessage(Util.format("moneySentTo", Util.formatCurrency(value), reciever.getDisplayName()));                        
			reciever.sendMessage(Util.format("moneyRecievedFrom", Util.formatCurrency(value), getDisplayName()));
		}
	}

	public void takeMoney(double value)
	{
		if (value == 0)
		{
			return;
		}
		setMoney(getMoney() - value);
		sendMessage(Util.format("takenFromAccount", Util.formatCurrency(value)));
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
			throw new Exception(Util.i18n("notEnoughMoney"));
		}
		takeMoney(cost);
	}

	public void canAfford(String cmd) throws Exception
	{
		double mon = getMoney();
		double cost = ess.getSettings().getCommandCost(cmd.startsWith("/") ? cmd.substring(1) : cmd);
		if (mon < cost && !isAuthorized("essentials.eco.loan"))
		{
			throw new Exception(Util.i18n("notEnoughMoney"));
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
			try
			{
				nickname = ess.getSettings().getOperatorColor().toString() + nickname + "§f";
			}
			catch(Exception e)
			{
			}
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

	@Override
	public double getMoney()
	{
		if (ess.isRegisterFallbackEnabled() && ess.getPaymentMethod().hasMethod())
		{
			try
			{
				Method method = ess.getPaymentMethod().getMethod();
				if (!method.hasAccount(this.getName())) {
					throw new Exception();
				}
				Method.MethodAccount account = ess.getPaymentMethod().getMethod().getAccount(this.getName());
				return account.balance();
			}
			catch (Throwable ex)
			{	
			}
		}
		return super.getMoney();
	}

	@Override
	public void setMoney(double value)
	{
		if (ess.isRegisterFallbackEnabled() && ess.getPaymentMethod().hasMethod())
		{
			try
			{
				Method method = ess.getPaymentMethod().getMethod();
				if (!method.hasAccount(this.getName())) {
					throw new Exception();
				}
				Method.MethodAccount account = ess.getPaymentMethod().getMethod().getAccount(this.getName());
				double amount = value - account.balance();
				account.add(amount);
			}
			catch (Throwable ex)
			{
			}
		}
		super.setMoney(value);
	}
}