package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.register.payment.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class User extends UserData implements Comparable<User>, IReplyTo, IUser
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private boolean justPortaled = false;
	private CommandSender replyTo = null;
	private User teleportRequester;
	private boolean teleportRequestHere;
	private final Teleport teleport;
	private long lastActivity;

	User(Player base, IEssentials ess)
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
		return isAuthorized(cmd, "essentials.");
	}

	public boolean isAuthorized(IEssentialsCommand cmd, String permissionPrefix)
	{
		return isAuthorized(permissionPrefix + (cmd.getName().equals("r") ? "msg" : cmd.getName()));
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

		return ess.getPermissionsHandler().hasPermission(this, node);
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
		giveMoney(value, null);
	}

	public void giveMoney(double value, CommandSender initiator)
	{
		if (value == 0)
		{
			return;
		}
		setMoney(getMoney() + value);
		sendMessage(Util.format("addedToAccount", Util.formatCurrency(value, ess)));
		if (initiator != null)
		{
			initiator.sendMessage((Util.format("addedToOthersAccount", Util.formatCurrency(value, ess), this.getDisplayName())));
		}
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
			sendMessage(Util.format("moneySentTo", Util.formatCurrency(value, ess), reciever.getDisplayName()));
			reciever.sendMessage(Util.format("moneyRecievedFrom", Util.formatCurrency(value, ess), getDisplayName()));
		}
	}

	public void takeMoney(double value)
	{
		takeMoney(value, null);
	}

	public void takeMoney(double value, CommandSender initiator)
	{
		if (value == 0)
		{
			return;
		}
		setMoney(getMoney() - value);
		sendMessage(Util.format("takenFromAccount", Util.formatCurrency(value, ess)));
		if (initiator != null)
		{
			initiator.sendMessage((Util.format("takenFromOthersAccount", Util.formatCurrency(value, ess), this.getDisplayName())));
		}
	}

	public boolean canAfford(double cost)
	{
		double mon = getMoney();
		return mon >= cost || isAuthorized("essentials.eco.loan");
	}

	public void dispose()
	{
		this.base = new OfflinePlayer(getName(), ess);
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

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof User))
		{
			return false;
		}
		return ChatColor.stripColor(this.getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(((User)o).getDisplayName()));

	}

	@Override
	public int hashCode()
	{
		return ChatColor.stripColor(this.getDisplayName()).hashCode();
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
		final StringBuilder nickname = new StringBuilder();
		final String nick = getNickname();
		if (ess.getSettings().isCommandDisabled("nick") || nick == null || nick.isEmpty() || nick.equals(getName()))
		{
			nickname.append(getName());
		}
		else
		{
			nickname.append(ess.getSettings().getNicknamePrefix()).append(nick);
		}
		if (isOp())
		{
			try
			{
				nickname.insert(0, ess.getSettings().getOperatorColor().toString());
				nickname.append("§f");
			}
			catch (Exception e)
			{
			}
		}

		final String prefix = ess.getPermissionsHandler().getPrefix(this).replace('&', '§').replace("{WORLDNAME}", this.getWorld().getName());
		final String suffix = ess.getPermissionsHandler().getSuffix(this).replace('&', '§').replace("{WORLDNAME}", this.getWorld().getName());

		nickname.insert(0, prefix);
		nickname.append(suffix);
		if (suffix.length() > 1 && suffix.substring(suffix.length() - 2, suffix.length() - 1).equals("§"))
		{
			nickname.append("§f");
		}

		return nickname.toString();
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
		if (ess.getPaymentMethod().hasMethod())
		{
			try
			{
				Method method = ess.getPaymentMethod().getMethod();
				if (!method.hasAccount(this.getName()))
				{
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
		if (ess.getPaymentMethod().hasMethod())
		{
			try
			{
				Method method = ess.getPaymentMethod().getMethod();
				if (!method.hasAccount(this.getName()))
				{
					throw new Exception();
				}
				Method.MethodAccount account = ess.getPaymentMethod().getMethod().getAccount(this.getName());
				account.set(value);
			}
			catch (Throwable ex)
			{
			}
		}
		super.setMoney(value);
	}

	@Override
	public void setAfk(boolean set)
	{
		this.setSleepingIgnored(this.isAuthorized("essentials.sleepingignored") ? true : set);
		super.setAfk(set);
	}

	@Override
	public boolean toggleAfk()
	{
		boolean now = super.toggleAfk();
		this.setSleepingIgnored(this.isAuthorized("essentials.sleepingignored") ? true : now);
		return now;
	}
}
