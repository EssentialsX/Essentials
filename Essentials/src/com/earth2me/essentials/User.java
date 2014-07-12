package com.earth2me.essentials;

import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.register.payment.Method;
import com.earth2me.essentials.register.payment.Methods;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class User extends UserData implements Comparable<User>, IReplyTo, net.ess3.api.IUser
{
	private static final Logger logger = Logger.getLogger("Essentials");
	private CommandSource replyTo = null;
	private transient UUID teleportRequester;
	private transient boolean teleportRequestHere;
	private transient Location teleportLocation;
	private transient boolean vanished;
	private transient final Teleport teleport;
	private transient long teleportRequestTime;
	private transient long lastOnlineActivity;
	private transient long lastThrottledAction;
	private transient long lastActivity = System.currentTimeMillis();
	private boolean hidden = false;
	private boolean rightClickJump = false;
	private transient Location afkPosition = null;
	private boolean invSee = false;
	private boolean recipeSee = false;
	private boolean enderSee = false;
	private transient long teleportInvulnerabilityTimestamp = 0;

	public User(final Player base, final IEssentials ess)
	{
		super(base, ess);
		teleport = new Teleport(this, ess);
		if (isAfk())
		{
			afkPosition = this.getLocation();
		}
		if (this.getBase().isOnline())
		{
			lastOnlineActivity = System.currentTimeMillis();
		}
	}

	User update(final Player base)
	{
		setBase(base);
		return this;
	}

	@Override
	public boolean isAuthorized(final IEssentialsCommand cmd)
	{
		return isAuthorized(cmd, "essentials.");
	}

	@Override
	public boolean isAuthorized(final IEssentialsCommand cmd, final String permissionPrefix)
	{
		return isAuthorized(permissionPrefix + (cmd.getName().equals("r") ? "msg" : cmd.getName()));
	}

	@Override
	public boolean isAuthorized(final String node)
	{
		final boolean result = isAuthorizedCheck(node);
		if (ess.getSettings().isDebug())
		{
			ess.getLogger().log(Level.INFO, "checking if " + base.getName() + " has " + node + " - " + result);
		}
		return result;
	}

	private boolean isAuthorizedCheck(final String node)
	{

		if (base instanceof OfflinePlayer)
		{
			return false;
		}

		try
		{
			return ess.getPermissionsHandler().hasPermission(base, node);
		}
		catch (Exception ex)
		{
			if (ess.getSettings().isDebug())
			{
				ess.getLogger().log(Level.SEVERE, "Permission System Error: " + ess.getPermissionsHandler().getName() + " returned: " + ex.getMessage(), ex);
			}
			else
			{
				ess.getLogger().log(Level.SEVERE, "Permission System Error: " + ess.getPermissionsHandler().getName() + " returned: " + ex.getMessage());
			}

			return false;
		}
	}

	@Override
	public void healCooldown() throws Exception
	{
		final Calendar now = new GregorianCalendar();
		if (getLastHealTimestamp() > 0)
		{
			final double cooldown = ess.getSettings().getHealCooldown();
			final Calendar cooldownTime = new GregorianCalendar();
			cooldownTime.setTimeInMillis(getLastHealTimestamp());
			cooldownTime.add(Calendar.SECOND, (int)cooldown);
			cooldownTime.add(Calendar.MILLISECOND, (int)((cooldown * 1000.0) % 1000.0));
			if (cooldownTime.after(now) && !isAuthorized("essentials.heal.cooldown.bypass"))
			{
				throw new Exception(tl("timeBeforeHeal", DateUtil.formatDateDiff(cooldownTime.getTimeInMillis())));
			}
		}
		setLastHealTimestamp(now.getTimeInMillis());
	}

	@Override
	public void giveMoney(final BigDecimal value) throws MaxMoneyException
	{
		giveMoney(value, (CommandSource)null);
	}

	@Override
	public void giveMoney(final BigDecimal value, final CommandSource initiator) throws MaxMoneyException
	{
		if (value.signum() == 0)
		{
			return;
		}
		setMoney(getMoney().add(value));
		sendMessage(tl("addedToAccount", NumberUtil.displayCurrency(value, ess)));
		if (initiator != null)
		{
			initiator.sendMessage(tl("addedToOthersAccount", NumberUtil.displayCurrency(value, ess), this.getDisplayName(), NumberUtil.displayCurrency(getMoney(), ess)));
		}
	}

	@Override
	public void payUser(final User reciever, final BigDecimal value) throws ChargeException, MaxMoneyException
	{
		if (value.signum() == 0)
		{
			return;
		}
		if (canAfford(value))
		{
			setMoney(getMoney().subtract(value));
			reciever.setMoney(reciever.getMoney().add(value));
			sendMessage(tl("moneySentTo", NumberUtil.displayCurrency(value, ess), reciever.getDisplayName()));
			reciever.sendMessage(tl("moneyRecievedFrom", NumberUtil.displayCurrency(value, ess), getDisplayName()));
		}
		else
		{
			throw new ChargeException(tl("notEnoughMoney"));
		}
	}

	@Override
	public void takeMoney(final BigDecimal value)
	{
		takeMoney(value, (CommandSource)null);
	}

	@Override
	public void takeMoney(final BigDecimal value, final CommandSource initiator)
	{
		if (value.signum() == 0)
		{
			return;
		}
		try
		{
			setMoney(getMoney().subtract(value));
		}
		catch (MaxMoneyException ex)
		{
			ess.getLogger().log(Level.WARNING, "Invalid call to takeMoney, total balance can't be more than the max-money limit.", ex);
		}
		sendMessage(tl("takenFromAccount", NumberUtil.displayCurrency(value, ess)));
		if (initiator != null)
		{
			initiator.sendMessage(tl("takenFromOthersAccount", NumberUtil.displayCurrency(value, ess), this.getDisplayName(), NumberUtil.displayCurrency(getMoney(), ess)));
		}
	}

	@Override
	public boolean canAfford(final BigDecimal cost)
	{
		return canAfford(cost, true);
	}

	public boolean canAfford(final BigDecimal cost, final boolean permcheck)
	{
		if (cost.signum() <= 0)
		{
			return true;
		}
		final BigDecimal remainingBalance = getMoney().subtract(cost);
		if (!permcheck || isAuthorized("essentials.eco.loan"))
		{
			return (remainingBalance.compareTo(ess.getSettings().getMinMoney()) >= 0);
		}
		return (remainingBalance.signum() >= 0);
	}

	public void dispose()
	{
		ess.runTaskAsynchronously(new Runnable()
		{
			@Override
			public void run()
			{
				_dispose();
			}
		});
	}

	private void _dispose()
	{
		if (!base.isOnline())
		{
			this.base = new OfflinePlayer(getConfigUUID(), ess.getServer());
		}
		cleanup();
	}

	@Override
	public Boolean canSpawnItem(final int itemId)
	{
		return !ess.getSettings().itemSpawnBlacklist().contains(itemId);
	}

	@Override
	public void setLastLocation()
	{
		setLastLocation(this.getLocation());
	}

	@Override
	public void setLogoutLocation()
	{
		setLogoutLocation(this.getLocation());
	}

	@Override
	public void requestTeleport(final User player, final boolean here)
	{
		teleportRequestTime = System.currentTimeMillis();
		teleportRequester = player == null ? null : player.getBase().getUniqueId();
		teleportRequestHere = here;
		if (player == null)
		{
			teleportLocation = null;
		}
		else
		{
			teleportLocation = here ? player.getLocation() : this.getLocation();
		}
	}

	public UUID getTeleportRequest()
	{
		return teleportRequester;
	}

	public boolean isTpRequestHere()
	{
		return teleportRequestHere;
	}

	public Location getTpRequestLocation()
	{
		return teleportLocation;
	}

	public String getNick(final boolean longnick)
	{
		final StringBuilder prefix = new StringBuilder();
		String nickname;
		String suffix = "";
		final String nick = getNickname();
		if (ess.getSettings().isCommandDisabled("nick") || nick == null || nick.isEmpty() || nick.equals(getName()))
		{
			nickname = getName();
		}
		else if (nick.equalsIgnoreCase(getName())) {
			nickname = nick;
		}
		else
		{
			nickname = ess.getSettings().getNicknamePrefix() + nick;
			suffix = "§r";
		}

		if (this.getBase().isOp())
		{
			try
			{
				final ChatColor opPrefix = ess.getSettings().getOperatorColor();
				if (opPrefix != null && opPrefix.toString().length() > 0)
				{
					prefix.insert(0, opPrefix.toString());
					suffix = "§r";
				}
			}
			catch (Exception e)
			{
			}
		}

		if (ess.getSettings().addPrefixSuffix())
		{
			//These two extra toggles are not documented, because they are mostly redundant #EasterEgg
			if (!ess.getSettings().disablePrefix())
			{
				final String ptext = ess.getPermissionsHandler().getPrefix(base).replace('&', '§');
				prefix.insert(0, ptext);
				suffix = "§r";
			}
			if (!ess.getSettings().disableSuffix())
			{
				final String stext = ess.getPermissionsHandler().getSuffix(base).replace('&', '§');
				suffix = stext + "§r";
				suffix = suffix.replace("§f§f", "§f").replace("§f§r", "§r").replace("§r§r", "§r");
			}
		}
		final String strPrefix = prefix.toString();
		String output = strPrefix + nickname + suffix;
		if (!longnick && output.length() > 16)
		{
			output = strPrefix + nickname;
		}
		if (!longnick && output.length() > 16)
		{
			output = FormatUtil.lastCode(strPrefix) + nickname;
		}
		if (!longnick && output.length() > 16)
		{
			output = FormatUtil.lastCode(strPrefix) + nickname.substring(0, 14);
		}
		if (output.charAt(output.length() - 1) == '§')
		{
			output = output.substring(0, output.length() - 1);
		}
		return output;
	}

	public void setDisplayNick()
	{
		if (base.isOnline() && ess.getSettings().changeDisplayName())
		{
			this.getBase().setDisplayName(getNick(true));
			if (ess.getSettings().changePlayerListName())
			{
				String name = getNick(false);
				try
				{
					this.getBase().setPlayerListName(name);
				}
				catch (IllegalArgumentException e)
				{
					if (ess.getSettings().isDebug())
					{
						logger.log(Level.INFO, "Playerlist for " + name + " was not updated. Name clashed with another online player.");
					}
				}
			}
		}
	}

	public String getDisplayName()
	{
		return super.getBase().getDisplayName() == null ? super.getBase().getName() : super.getBase().getDisplayName();
	}

	@Override
	public Teleport getTeleport()
	{
		return teleport;
	}

	public long getLastOnlineActivity()
	{
		return lastOnlineActivity;
	}

	public void setLastOnlineActivity(final long timestamp)
	{
		lastOnlineActivity = timestamp;
	}

	@Override
	public BigDecimal getMoney()
	{
		final long start = System.nanoTime();
		final BigDecimal value = _getMoney();
		final long elapsed = System.nanoTime() - start;
		if (elapsed > ess.getSettings().getEconomyLagWarning())
		{
			ess.getLogger().log(Level.INFO, "Lag Notice - Slow Economy Response - Request took over {0}ms!", elapsed / 1000000.0);
		}
		return value;
	}

	private BigDecimal _getMoney()
	{
		if (ess.getSettings().isEcoDisabled())
		{
			if (ess.getSettings().isDebug())
			{
				ess.getLogger().info("Internal economy functions disabled, aborting balance check.");
			}
			return BigDecimal.ZERO;
		}
		if (Methods.hasMethod())
		{
			try
			{
				final Method method = Methods.getMethod();
				if (!method.hasAccount(this.getName()))
				{
					throw new Exception();
				}
				final Method.MethodAccount account = Methods.getMethod().getAccount(this.getName());
				return BigDecimal.valueOf(account.balance());
			}
			catch (Exception ex)
			{
			}
		}
		return super.getMoney();
	}

	@Override
	public void setMoney(final BigDecimal value) throws MaxMoneyException
	{
		if (ess.getSettings().isEcoDisabled())
		{
			if (ess.getSettings().isDebug())
			{
				ess.getLogger().info("Internal economy functions disabled, aborting balance change.");
			}
			return;
		}
		final BigDecimal oldBalance = _getMoney();
		if (Methods.hasMethod())
		{
			try
			{
				final Method method = Methods.getMethod();
				if (!method.hasAccount(this.getName()))
				{
					throw new Exception();
				}
				final Method.MethodAccount account = Methods.getMethod().getAccount(this.getName());
				account.set(value.doubleValue());
			}
			catch (Exception ex)
			{
			}
		}
		super.setMoney(value, true);
		ess.getServer().getPluginManager().callEvent(new UserBalanceUpdateEvent(this.getBase(), oldBalance, value));
		Trade.log("Update", "Set", "API", getName(), new Trade(value, ess), null, null, null, ess);
	}

	public void updateMoneyCache(final BigDecimal value)
	{
		if (ess.getSettings().isEcoDisabled())
		{
			return;
		}
		if (Methods.hasMethod() && super.getMoney() != value)
		{
			try
			{
				super.setMoney(value, false);
			}
			catch (MaxMoneyException ex)
			{
				// We don't want to throw any errors here, just updating a cache
			}
		}
	}

	@Override
	public void setAfk(final boolean set)
	{
		final AfkStatusChangeEvent afkEvent = new AfkStatusChangeEvent(this, set);
		ess.getServer().getPluginManager().callEvent(afkEvent);
		if (afkEvent.isCancelled())
		{
			return;
		}

		this.getBase().setSleepingIgnored(this.isAuthorized("essentials.sleepingignored") ? true : set);
		if (set && !isAfk())
		{
			afkPosition = this.getLocation();
		}
		else if (!set && isAfk())
		{
			afkPosition = null;
		}
		_setAfk(set);
	}

	public boolean toggleAfk()
	{
		setAfk(!isAfk());
		return isAfk();
	}

	@Override
	public boolean isHidden()
	{
		return hidden;
	}
	
	public boolean isHidden(final Player player)
	{
		return hidden || !player.canSee(getBase());
	}

	@Override
	public void setHidden(final boolean hidden)
	{
		this.hidden = hidden;
		if (hidden == true)
		{
			setLastLogout(getLastOnlineActivity());
		}
	}

	//Returns true if status expired during this check
	public boolean checkJailTimeout(final long currentTime)
	{
		if (getJailTimeout() > 0 && getJailTimeout() < currentTime && isJailed())
		{
			setJailTimeout(0);
			setJailed(false);
			sendMessage(tl("haveBeenReleased"));
			setJail(null);
			try
			{
				getTeleport().back();
			}
			catch (Exception ex)
			{
				try
				{
					getTeleport().respawn(null, TeleportCause.PLUGIN);
				}
				catch (Exception ex1)
				{
				}
			}
			return true;
		}
		return false;
	}

	//Returns true if status expired during this check
	public boolean checkMuteTimeout(final long currentTime)
	{
		if (getMuteTimeout() > 0 && getMuteTimeout() < currentTime && isMuted())
		{
			setMuteTimeout(0);
			sendMessage(tl("canTalkAgain"));
			setMuted(false);
			return true;
		}
		return false;
	}

	public void updateActivity(final boolean broadcast)
	{
		if (isAfk() && ess.getSettings().cancelAfkOnInteract())
		{
			setAfk(false);
			if (broadcast && !isHidden())
			{
				setDisplayNick();
				final String msg = tl("userIsNotAway", getDisplayName());
				if (!msg.isEmpty())
				{
					ess.broadcastMessage(this, msg);
				}
			}
		}
		lastActivity = System.currentTimeMillis();
	}

	public void checkActivity()
	{
		final long autoafkkick = ess.getSettings().getAutoAfkKick();
		if (autoafkkick > 0 && lastActivity > 0 && (lastActivity + (autoafkkick * 1000)) < System.currentTimeMillis()
			&& !isHidden() && !isAuthorized("essentials.kick.exempt") && !isAuthorized("essentials.afk.kickexempt"))
		{
			final String kickReason = tl("autoAfkKickReason", autoafkkick / 60.0);
			lastActivity = 0;
			this.getBase().kickPlayer(kickReason);


			for (Player player : ess.getServer().getOnlinePlayers())
			{
				final User user = ess.getUser(player);
				if (user.isAuthorized("essentials.kick.notify"))
				{
					user.sendMessage(tl("playerKicked", Console.NAME, getName(), kickReason));
				}
			}
		}
		final long autoafk = ess.getSettings().getAutoAfk();
		if (!isAfk() && autoafk > 0 && lastActivity + autoafk * 1000 < System.currentTimeMillis() && isAuthorized("essentials.afk.auto"))
		{
			setAfk(true);
			if (!isHidden())
			{
				setDisplayNick();
				final String msg = tl("userIsAway", getDisplayName());
				if (!msg.isEmpty())
				{
					ess.broadcastMessage(this, msg);
				}
			}
		}
	}

	public Location getAfkPosition()
	{
		return afkPosition;
	}

	@Override
	public boolean isGodModeEnabled()
	{
		return (super.isGodModeEnabled() && !ess.getSettings().getNoGodWorlds().contains(this.getLocation().getWorld().getName()))
			   || (isAfk() && ess.getSettings().getFreezeAfkPlayers());
	}

	public boolean isGodModeEnabledRaw()
	{
		return super.isGodModeEnabled();
	}

	@Override
	public String getGroup()
	{
		final String result = ess.getPermissionsHandler().getGroup(base);
		if (ess.getSettings().isDebug())
		{
			ess.getLogger().log(Level.INFO, "looking up groupname of " + base.getName() + " - " + result);
		}
		return result;
	}

	@Override
	public boolean inGroup(final String group)
	{
		final boolean result = ess.getPermissionsHandler().inGroup(base, group);
		if (ess.getSettings().isDebug())
		{
			ess.getLogger().log(Level.INFO, "checking if " + base.getName() + " is in group " + group + " - " + result);
		}
		return result;
	}

	@Override
	public boolean canBuild()
	{
		if (this.getBase().isOp())
		{
			return true;
		}
		return ess.getPermissionsHandler().canBuild(base, getGroup());
	}

	public long getTeleportRequestTime()
	{
		return teleportRequestTime;
	}

	public boolean isInvSee()
	{
		return invSee;
	}

	public void setInvSee(final boolean set)
	{
		invSee = set;
	}

	public boolean isEnderSee()
	{
		return enderSee;
	}

	public void setEnderSee(final boolean set)
	{
		enderSee = set;
	}

	@Override
	public void enableInvulnerabilityAfterTeleport()
	{
		final long time = ess.getSettings().getTeleportInvulnerability();
		if (time > 0)
		{
			teleportInvulnerabilityTimestamp = System.currentTimeMillis() + time;
		}
	}

	@Override
	public void resetInvulnerabilityAfterTeleport()
	{
		if (teleportInvulnerabilityTimestamp != 0
			&& teleportInvulnerabilityTimestamp < System.currentTimeMillis())
		{
			teleportInvulnerabilityTimestamp = 0;
		}
	}

	@Override
	public boolean hasInvulnerabilityAfterTeleport()
	{
		return teleportInvulnerabilityTimestamp != 0 && teleportInvulnerabilityTimestamp >= System.currentTimeMillis();
	}
	
	public boolean canInteractVanished()
	{
		return isAuthorized("essentials.vanish.interact");
	}

	@Override
	public boolean isVanished()
	{
		return vanished;
	}

	@Override
	public void setVanished(final boolean set)
	{
		vanished = set;
		if (set)
		{
			for (Player p : ess.getServer().getOnlinePlayers())
			{
				if (!ess.getUser(p).isAuthorized("essentials.vanish.see"))
				{
					p.hidePlayer(getBase());
				}
			}
			setHidden(true);
			ess.getVanishedPlayers().add(getName());
			if (isAuthorized("essentials.vanish.effect"))
			{
				this.getBase().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false));
			}
		}
		else
		{
			for (Player p : ess.getServer().getOnlinePlayers())
			{
				p.showPlayer(getBase());
			}
			setHidden(false);
			ess.getVanishedPlayers().remove(getName());
			if (isAuthorized("essentials.vanish.effect"))
			{
				this.getBase().removePotionEffect(PotionEffectType.INVISIBILITY);
			}
		}
	}

	public boolean checkSignThrottle()
	{
		if (isSignThrottled())
		{
			return true;
		}
		updateThrottle();
		return false;
	}

	public boolean isSignThrottled()
	{
		final long minTime = lastThrottledAction + (1000 / ess.getSettings().getSignUsePerSecond());
		return (System.currentTimeMillis() < minTime);
	}

	public void updateThrottle()
	{
		lastThrottledAction = System.currentTimeMillis();
	}

	public boolean isFlyClickJump()
	{
		return rightClickJump;
	}

	public void setRightClickJump(boolean rightClickJump)
	{
		this.rightClickJump = rightClickJump;
	}

	@Override
	public boolean isIgnoreExempt()
	{
		return this.isAuthorized("essentials.chat.ignoreexempt");
	}

	public boolean isRecipeSee()
	{
		return recipeSee;
	}

	public void setRecipeSee(boolean recipeSee)
	{
		this.recipeSee = recipeSee;
	}

	@Override
	public void sendMessage(String message)
	{
		if (!message.isEmpty())
		{
			base.sendMessage(message);
		}
	}

	@Override
	public void setReplyTo(final CommandSource user)
	{
		replyTo = user;
	}

	@Override
	public CommandSource getReplyTo()
	{
		return replyTo;
	}

	@Override
	public int compareTo(final User other)
	{
		return FormatUtil.stripFormat(getDisplayName()).compareToIgnoreCase(FormatUtil.stripFormat(other.getDisplayName()));
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof User))
		{
			return false;
		}
		return this.getName().equalsIgnoreCase(((User)object).getName());

	}

	@Override
	public int hashCode()
	{
		return this.getName().hashCode();
	}

	@Override
	public CommandSource getSource()
	{
		return new CommandSource(getBase());
	}

	@Override
	public String getName()
	{
		return this.getBase().getName();
	}
}
