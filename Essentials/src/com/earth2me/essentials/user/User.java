package com.earth2me.essentials.user;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.*;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.register.payment.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;


public class User extends UserBase implements IUser
{
	private CommandSender replyTo = null;
	@Getter
	private transient IUser teleportRequester;
	@Getter
	private transient boolean teleportRequestHere;
	@Getter
	private transient final ITeleport teleport;
	@Getter
	private transient long teleportRequestTime;
	@Getter
	@Setter
	private transient long lastOnlineActivity;
	private transient long lastActivity = System.currentTimeMillis();
	@Getter
	@Setter
	private boolean hidden = false;
	private transient Location afkPosition;
	private static final Logger logger = Bukkit.getLogger();
	private AtomicBoolean gotMailInfo = new AtomicBoolean(false);

	public User(final Player base, final IEssentials ess)
	{
		super(base, ess);
		teleport = new Teleport(this, ess);
	}

	public User(final OfflinePlayer offlinePlayer, final IEssentials ess)
	{
		super(offlinePlayer, ess);
		teleport = new Teleport(this, ess);
	}

	public void example()
	{
		// Cleanup will call close at the end of the function
		@Cleanup
		final User user = this;

		// read lock allows to read data from the user
		user.acquireReadLock();
		final double money = user.getData().getMoney();

		// write lock allows only one thread to modify the data
		user.acquireWriteLock();
		user.getData().setMoney(10 + money);
	}

	@Override
	public boolean isAuthorized(String node)
	{
		if (!isOnlineUser())
		{
			return false;
		}

		if (getData().isJailed())
		{
			return false;
		}
		//TODO: switch to Superperms only
		return ess.getPermissionsHandler().hasPermission(base, node);
	}

	@Override
	public boolean isAuthorized(IPermission permission)
	{
		return isAuthorized(permission.getPermission());
	}
	
	/*@Override
	public boolean isAuthorized(IEssentialsCommand cmd)
	{
		return isAuthorized(cmd, "essentials.");
	}

	@Override
	public boolean isAuthorized(IEssentialsCommand cmd, String permissionPrefix)
	{
		return isAuthorized(permissionPrefix + (cmd.getName().equals("r") ? "msg" : cmd.getName()));
	}*/
	
	@Override
	public void checkCooldown(final UserData.TimestampType cooldownType, final double cooldown, final boolean set, final String bypassPermission) throws CooldownException
	{
		final Calendar now = new GregorianCalendar();
		if (getTimestamp(cooldownType) > 0)
		{
			final Calendar cooldownTime = new GregorianCalendar();
			cooldownTime.setTimeInMillis(getTimestamp(cooldownType));
			cooldownTime.add(Calendar.SECOND, (int)cooldown);
			cooldownTime.add(Calendar.MILLISECOND, (int)((cooldown * 1000.0) % 1000.0));
			if (cooldownTime.after(now) && !isAuthorized(bypassPermission))
			{
				throw new CooldownException(Util.formatDateDiff(cooldownTime.getTimeInMillis()));
			}
		}
		if (set)
		{
			setTimestamp(cooldownType, now.getTimeInMillis());
		}
	}

	@Override
	public void giveMoney(final double value)
	{
		giveMoney(value, null);
	}

	@Override
	public void giveMoney(final double value, final CommandSender initiator)
	{

		if (value == 0)
		{
			return;
		}
		acquireWriteLock();
		try
		{
			setMoney(getMoney() + value);
			sendMessage(_("addedToAccount", Util.formatCurrency(value, ess)));
			if (initiator != null)
			{
				initiator.sendMessage(_("addedToOthersAccount", Util.formatCurrency(value, ess), this.getDisplayName()));
			}
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public void payUser(final IUser reciever, final double value) throws Exception
	{
		if (value == 0)
		{
			return;
		}
		if (canAfford(value))
		{
			setMoney(getMoney() - value);
			reciever.setMoney(reciever.getMoney() + value);
			sendMessage(_("moneySentTo", Util.formatCurrency(value, ess), reciever.getDisplayName()));
			reciever.sendMessage(_("moneyRecievedFrom", Util.formatCurrency(value, ess), getDisplayName()));
		}
		else
		{
			throw new Exception(_("notEnoughMoney"));
		}
	}

	@Override
	public void takeMoney(final double value)
	{
		takeMoney(value, null);
	}

	@Override
	public void takeMoney(final double value, final CommandSender initiator)
	{
		if (value == 0)
		{
			return;
		}
		setMoney(getMoney() - value);
		sendMessage(_("takenFromAccount", Util.formatCurrency(value, ess)));
		if (initiator != null)
		{
			initiator.sendMessage(_("takenFromOthersAccount", Util.formatCurrency(value, ess), this.getDisplayName()));
		}
	}

	public boolean canAfford(final double cost)
	{
		final double mon = getMoney();
		return mon >= cost || isAuthorized("essentials.eco.loan");
	}

	public void setHome()
	{
		setHome("home", getLocation());
	}

	public void setHome(final String name)
	{
		setHome(name, getLocation());
	}

	@Override
	public void setLastLocation()
	{
		acquireWriteLock();
		try
		{
			getData().setLastLocation(getLocation());
		}
		finally
		{
			unlock();
		}
	}

	public void requestTeleport(final User player, final boolean here)
	{
		teleportRequestTime = System.currentTimeMillis();
		teleportRequester = player;
		teleportRequestHere = here;
	}

	public String getNick(boolean addprefixsuffix)
	{
		acquireReadLock();
		try
		{
			final String nick = getData().getNickname();
			@Cleanup
			final ISettings settings = ess.getSettings();
			settings.acquireReadLock();
			@Cleanup
			final IGroups groups = ess.getGroups();
			groups.acquireReadLock();
			// default: {PREFIX}{NICKNAMEPREFIX}{NAME}{SUFFIX}
			String displayname = settings.getData().getChat().getDisplaynameFormat();
			if (settings.getData().getCommands().isDisabled("nick") || nick == null || nick.isEmpty() || nick.equals(getName()))
			{
				displayname = displayname.replace("{NAME}", getName());
			}
			else
			{
				displayname = displayname.replace("{NAME}", nick);
				displayname = displayname.replace("{NICKNAMEPREFIX}", settings.getData().getChat().getNicknamePrefix());
			}

			if (displayname.contains("{PREFIX}"))
			{
				displayname = displayname.replace("{PREFIX}", groups.getPrefix(this));
			}
			if (displayname.contains("{SUFFIX}"))
			{
				displayname = displayname.replace("{SUFFIX}", groups.getSuffix(this));
			}
			displayname = displayname.replace("{WORLDNAME}", this.getWorld().getName());
			displayname = displayname.replace('&', '§');
			displayname = displayname.concat("§f");

			return displayname;
		}
		finally
		{
			unlock();
		}
	}

	public void setDisplayNick()
	{
		String name = getNick(true);
		setDisplayName(name);
		if (name.length() > 16)
		{
			name = getNick(false);
		}
		if (name.length() > 16)
		{
			name = name.substring(0, name.charAt(15) == '§' ? 15 : 16);
		}
		try
		{
			setPlayerListName(name);
		}
		catch (IllegalArgumentException e)
		{
			logger.info("Playerlist for " + name + " was not updated. Use a shorter displayname prefix.");
		}
	}

	@Override
	public String getDisplayName()
	{
		return super.getDisplayName() == null ? super.getName() : super.getDisplayName();
	}

	@Override
	public void updateDisplayName()
	{
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		if (isOnlineUser() && settings.getData().getChat().getChangeDisplayname())
		{
			setDisplayNick();
		}
	}

	@Override
	public double getMoney()
	{
		if (ess.getPaymentMethod().hasMethod())
		{
			try
			{
				final Method method = ess.getPaymentMethod().getMethod();
				if (!method.hasAccount(this.getName()))
				{
					throw new Exception();
				}
				final Method.MethodAccount account = ess.getPaymentMethod().getMethod().getAccount(this.getName());
				return account.balance();
			}
			catch (Throwable ex)
			{
			}
		}
		return super.getMoney();
	}

	@Override
	public void setMoney(final double value)
	{
		if (ess.getPaymentMethod().hasMethod())
		{
			try
			{
				final Method method = ess.getPaymentMethod().getMethod();
				if (!method.hasAccount(this.getName()))
				{
					throw new Exception();
				}
				final Method.MethodAccount account = ess.getPaymentMethod().getMethod().getAccount(this.getName());
				account.set(value);
			}
			catch (Throwable ex)
			{
			}
		}
		super.setMoney(value);
	}

	public void setAfk(final boolean set)
	{
		acquireWriteLock();
		try
		{
			this.setSleepingIgnored(this.isAuthorized("essentials.sleepingignored") ? true : set);
			if (set && !getData().isAfk())
			{
				afkPosition = getLocation();
			}
			getData().setAfk(set);
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public boolean toggleAfk()
	{
		final boolean now = super.toggleAfk();
		this.setSleepingIgnored(this.isAuthorized("essentials.sleepingignored") ? true : now);
		return now;
	}

	//Returns true if status expired during this check
	public boolean checkJailTimeout(final long currentTime)
	{
		acquireReadLock();
		try
		{
			if (getTimestamp(UserData.TimestampType.JAIL) > 0 && getTimestamp(UserData.TimestampType.JAIL) < currentTime && getData().isJailed())
			{
				acquireWriteLock();

				setTimestamp(UserData.TimestampType.JAIL, 0);
				getData().setJailed(false);
				sendMessage(_("haveBeenReleased"));
				getData().setJail(null);

				try
				{
					teleport.back();
				}
				catch (Exception ex)
				{
				}
				return true;
			}
			return false;
		}
		finally
		{
			unlock();
		}
	}

	//Returns true if status expired during this check
	public boolean checkMuteTimeout(final long currentTime)
	{
		acquireReadLock();
		try
		{
			if (getTimestamp(UserData.TimestampType.MUTE) > 0 && getTimestamp(UserData.TimestampType.MUTE) < currentTime && getData().isMuted())
			{
				acquireWriteLock();
				setTimestamp(UserData.TimestampType.MUTE, 0);
				sendMessage(_("canTalkAgain"));
				getData().setMuted(false);
				return true;
			}
			return false;
		}
		finally
		{
			unlock();
		}
	}

	//Returns true if status expired during this check
	public boolean checkBanTimeout(final long currentTime)
	{
		acquireReadLock();
		try
		{
			if (getData().getBan() != null && getData().getBan().getTimeout() > 0 && getData().getBan().getTimeout() < currentTime && isBanned())
			{
				acquireWriteLock();
				getData().setBan(null);
				setBanned(false);
				return true;
			}
			return false;
		}
		finally
		{
			unlock();
		}
	}

	public void updateActivity(final boolean broadcast)
	{
		acquireReadLock();
		try
		{
			if (getData().isAfk())
			{
				acquireWriteLock();
				getData().setAfk(false);
				if (broadcast && !hidden)
				{
					ess.broadcastMessage(this, _("userIsNotAway", getDisplayName()));
				}
			}
			lastActivity = System.currentTimeMillis();
		}
		finally
		{
			unlock();
		}
	}

	public void checkActivity()
	{
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		final long autoafkkick = settings.getData().getCommands().getAfk().getAutoAFKKick();
		if (autoafkkick > 0 && lastActivity > 0 && (lastActivity + (autoafkkick * 1000)) < System.currentTimeMillis()
			&& !hidden && !isAuthorized("essentials.kick.exempt") && !isAuthorized("essentials.afk.kickexempt"))
		{
			final String kickReason = _("autoAfkKickReason", autoafkkick / 60.0);
			lastActivity = 0;
			kickPlayer(kickReason);


			for (Player player : ess.getServer().getOnlinePlayers())
			{
				final IUser user = ess.getUser(player);
				if (user.isAuthorized("essentials.kick.notify"))
				{
					player.sendMessage(_("playerKicked", Console.NAME, getName(), kickReason));
				}
			}
		}
		final long autoafk = settings.getData().getCommands().getAfk().getAutoAFK();
		acquireReadLock();
		try
		{
			if (!getData().isAfk() && autoafk > 0 && lastActivity + autoafk * 1000 < System.currentTimeMillis() && isAuthorized("essentials.afk"))
			{
				setAfk(true);
				if (!hidden)
				{
					ess.broadcastMessage(this, _("userIsAway", getDisplayName()));
				}
			}
		}
		finally
		{
			unlock();
		}
	}

	public Location getAfkPosition()
	{
		return afkPosition;
	}

	public boolean toggleGodModeEnabled()
	{
		if (!isGodModeEnabled())
		{
			setFoodLevel(20);
		}
		return super.toggleGodmode();
	}

	public boolean isGodModeEnabled()
	{
		acquireReadLock();
		try
		{
			@Cleanup
			final ISettings settings = ess.getSettings();
			settings.acquireReadLock();
			return (getData().isGodmode()
					&& !settings.getData().getWorldOptions(getLocation().getWorld().getName()).isGodmode())
				   || (getData().isAfk() && settings.getData().getCommands().getAfk().isFreezeAFKPlayers());
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public String getGroup()
	{
		return ess.getPermissionsHandler().getGroup(base);
	}

	public boolean inGroup(final String group)
	{
		return ess.getPermissionsHandler().inGroup(base, group);
	}

	public boolean canBuild()
	{
		return ess.getPermissionsHandler().canBuild(base, getGroup());
	}

	@Override
	public Location getHome(String name) throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void updateCompass()
	{
		try
		{
			Location loc = getHome(getLocation());
			if (loc == null)
			{
				loc = getBedSpawnLocation();
			}
			if (loc != null)
			{
				setCompassTarget(loc);
			}
		}
		catch (Exception ex)
		{
			// Ignore
		}
	}

	@Override
	public List<String> getHomes()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int compareTo(final IUser t)
	{
		return Util.stripColor(this.getDisplayName()).compareTo(Util.stripColor(t.getDisplayName()));
	}

	@Override
	public void requestTeleport(IUser user, boolean b)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setReplyTo(CommandSender user)
	{
		replyTo = user;
	}

	@Override
	public CommandSender getReplyTo()
	{
		return replyTo;
	}

	@Override
	public boolean gotMailInfo()
	{
		return gotMailInfo.getAndSet(true);
	}

	@Override
	public void addMail(String mail)
	{
		super.addMail(mail);
		gotMailInfo.set(false);
	}

	@Override
	public void giveItems(ItemStack itemStack)
	{
		if (giveItemStack(itemStack))
		{
			sendMessage(_("InvFull"));
		}
	}

	@Override
	public void giveItems(List<ItemStack> itemStacks)
	{
		boolean spew = false;
		for (ItemStack itemStack : itemStacks)
		{
			if (giveItemStack(itemStack))
			{
				spew = true;
			}
		}
		if (spew)
		{
			sendMessage(_("InvFull"));
		}
	}

	private boolean giveItemStack(ItemStack itemStack)
	{
		boolean spew = false;
		final Map<Integer, ItemStack> overfilled;
		if (isAuthorized("essentials.oversizedstacks"))
		{
			@Cleanup
			final ISettings settings = ess.getSettings();
			settings.acquireReadLock();
			int oversizedStackSize = settings.getData().getGeneral().getOversizedStacksize();

			overfilled = InventoryWorkaround.addItem(getInventory(), true, oversizedStackSize, itemStack);
		}
		else
		{
			overfilled = InventoryWorkaround.addItem(getInventory(), true, itemStack);
		}
		for (ItemStack overflowStack : overfilled.values())
		{
			getWorld().dropItemNaturally(getLocation(), overflowStack);
			spew = true;
		}
		return spew;
	}

	@Override
	public void setDisplayName(String string)
	{
		base.setDisplayName(string);
	}

	@Override
	public String getPlayerListName()
	{
		return base.getPlayerListName();
	}

	@Override
	public void setPlayerListName(String string)
	{
		base.setPlayerListName(string);
	}

	@Override
	public void setCompassTarget(Location lctn)
	{
		base.setCompassTarget(lctn);
	}

	@Override
	public Location getCompassTarget()
	{
		return base.getCompassTarget();
	}

	@Override
	public InetSocketAddress getAddress()
	{
		return base.getAddress();
	}

	@Override
	public void sendRawMessage(String string)
	{
		base.sendRawMessage(string);
	}

	@Override
	public void kickPlayer(String string)
	{
		base.kickPlayer(string);
	}

	@Override
	public void chat(String string)
	{
		base.chat(string);
	}

	@Override
	public boolean performCommand(String string)
	{
		return base.performCommand(string);
	}

	@Override
	public boolean isSneaking()
	{
		return base.isSneaking();
	}

	@Override
	public void setSneaking(boolean bln)
	{
		base.setSneaking(bln);
	}

	@Override
	public boolean isSprinting()
	{
		return base.isSprinting();
	}

	@Override
	public void setSprinting(boolean bln)
	{
		base.setSprinting(bln);
	}

	@Override
	public void saveData()
	{
		base.saveData();
	}

	@Override
	public void loadData()
	{
		base.loadData();
	}

	@Override
	public void setSleepingIgnored(boolean bln)
	{
		base.setSleepingIgnored(bln);
	}

	@Override
	public boolean isSleepingIgnored()
	{
		return base.isSleepingIgnored();
	}

	@Override
	public void playNote(Location lctn, byte b, byte b1)
	{
		base.playNote(lctn, b, b1);
	}

	@Override
	public void playNote(Location lctn, Instrument i, Note note)
	{
		base.playNote(lctn, i, note);
	}

	@Override
	public void playEffect(Location lctn, Effect effect, int i)
	{
		base.playEffect(lctn, effect, i);
	}

	@Override
	public void sendBlockChange(Location lctn, Material mtrl, byte b)
	{
		base.sendBlockChange(lctn, mtrl, b);
	}

	@Override
	public boolean sendChunkChange(Location lctn, int i, int i1, int i2, byte[] bytes)
	{
		return base.sendChunkChange(lctn, i, i1, i2, bytes);
	}

	@Override
	public void sendBlockChange(Location lctn, int i, byte b)
	{
		base.sendBlockChange(lctn, i, b);
	}

	@Override
	public void sendMap(MapView mv)
	{
		base.sendMap(mv);
	}

	@Override
	public void updateInventory()
	{
		base.updateInventory();
	}

	@Override
	public void awardAchievement(Achievement a)
	{
		base.awardAchievement(a);
	}

	@Override
	public void incrementStatistic(Statistic ststc)
	{
		base.incrementStatistic(ststc);
	}

	@Override
	public void incrementStatistic(Statistic ststc, int i)
	{
		base.incrementStatistic(ststc, i);
	}

	@Override
	public void incrementStatistic(Statistic ststc, Material mtrl)
	{
		base.incrementStatistic(ststc, mtrl);
	}

	@Override
	public void incrementStatistic(Statistic ststc, Material mtrl, int i)
	{
		base.incrementStatistic(ststc, mtrl, i);
	}

	@Override
	public void setPlayerTime(long l, boolean bln)
	{
		base.setPlayerTime(l, bln);
	}

	@Override
	public long getPlayerTime()
	{
		return base.getPlayerTime();
	}

	@Override
	public long getPlayerTimeOffset()
	{
		return base.getPlayerTimeOffset();
	}

	@Override
	public boolean isPlayerTimeRelative()
	{
		return base.isPlayerTimeRelative();
	}

	@Override
	public void resetPlayerTime()
	{
		base.resetPlayerTime();
	}

	@Override
	public void giveExp(int i)
	{
		base.giveExp(i);
	}

	@Override
	public float getExp()
	{
		return base.getExp();
	}

	@Override
	public void setExp(float f)
	{
		base.setExp(f);
	}

	@Deprecated
	@Override
	public int getExperience()
	{
		return base.getExperience();
	}

	@Deprecated
	@Override
	public void setExperience(int i)
	{
		base.setExperience(i);
	}

	@Override
	public int getLevel()
	{
		return base.getLevel();
	}

	@Override
	public void setLevel(int i)
	{
		base.setLevel(i);
	}

	@Override
	public int getTotalExperience()
	{
		return base.getTotalExperience();
	}

	@Override
	public void setTotalExperience(int i)
	{
		base.setTotalExperience(i);
	}

	@Override
	public float getExhaustion()
	{
		return base.getExhaustion();
	}

	@Override
	public void setExhaustion(float f)
	{
		base.setExhaustion(f);
	}

	@Override
	public float getSaturation()
	{
		return base.getSaturation();
	}

	@Override
	public void setSaturation(float f)
	{
		base.setSaturation(f);
	}

	@Override
	public int getFoodLevel()
	{
		return base.getFoodLevel();
	}

	@Override
	public void setFoodLevel(int i)
	{
		base.setFoodLevel(i);
	}

	@Override
	public PlayerInventory getInventory()
	{
		return base.getInventory();
	}

	@Override
	public ItemStack getItemInHand()
	{
		return base.getItemInHand();
	}

	@Override
	public void setItemInHand(ItemStack is)
	{
		base.setItemInHand(is);
	}

	@Override
	public boolean isSleeping()
	{
		return base.isSleeping();
	}

	@Override
	public int getSleepTicks()
	{
		return base.getSleepTicks();
	}

	@Override
	public GameMode getGameMode()
	{
		return base.getGameMode();
	}

	@Override
	public void setGameMode(GameMode gm)
	{
		base.setGameMode(gm);
	}

	@Override
	public int getHealth()
	{
		return base.getHealth();
	}

	@Override
	public void setHealth(int i)
	{
		base.setHealth(i);
	}

	@Override
	public int getMaxHealth()
	{
		return base.getMaxHealth();
	}

	@Override
	public double getEyeHeight()
	{
		return base.getEyeHeight();
	}

	@Override
	public double getEyeHeight(boolean bln)
	{
		return base.getEyeHeight(bln);
	}

	@Override
	public Location getEyeLocation()
	{
		return base.getEyeLocation();
	}

	@Override
	public List<Block> getLineOfSight(HashSet<Byte> hs, int i)
	{
		return base.getLineOfSight(hs, i);
	}

	@Override
	public Block getTargetBlock(HashSet<Byte> hs, int i)
	{
		return base.getTargetBlock(hs, i);
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hs, int i)
	{
		return base.getLastTwoTargetBlocks(hs, i);
	}

	@Override
	public Egg throwEgg()
	{
		return base.throwEgg();
	}

	@Override
	public Snowball throwSnowball()
	{
		return base.throwSnowball();
	}

	@Override
	public Arrow shootArrow()
	{
		return base.shootArrow();
	}

	@Override
	public boolean isInsideVehicle()
	{
		return base.isInsideVehicle();
	}

	@Override
	public boolean leaveVehicle()
	{
		return base.leaveVehicle();
	}

	@Override
	public Vehicle getVehicle()
	{
		return base.getVehicle();
	}

	@Override
	public int getRemainingAir()
	{
		return base.getRemainingAir();
	}

	@Override
	public void setRemainingAir(int i)
	{
		base.setRemainingAir(i);
	}

	@Override
	public int getMaximumAir()
	{
		return base.getMaximumAir();
	}

	@Override
	public void setMaximumAir(int i)
	{
		base.setMaximumAir(i);
	}

	@Override
	public void damage(int i)
	{
		base.damage(i);
	}

	@Override
	public void damage(int i, Entity entity)
	{
		base.damage(i, entity);
	}

	@Override
	public int getMaximumNoDamageTicks()
	{
		return base.getMaximumNoDamageTicks();
	}

	@Override
	public void setMaximumNoDamageTicks(int i)
	{
		base.setMaximumNoDamageTicks(i);
	}

	@Override
	public int getLastDamage()
	{
		return base.getLastDamage();
	}

	@Override
	public void setLastDamage(int i)
	{
		base.setLastDamage(i);
	}

	@Override
	public int getNoDamageTicks()
	{
		return base.getNoDamageTicks();
	}

	@Override
	public void setNoDamageTicks(int i)
	{
		base.setNoDamageTicks(i);
	}

	@Override
	public Player getKiller()
	{
	    return base.getKiller();
	}

	@Override
	public Location getLocation()
	{
		return base.getLocation();
	}

	@Override
	public void setVelocity(Vector vector)
	{
		base.setVelocity(vector);
	}

	@Override
	public Vector getVelocity()
	{
		return base.getVelocity();
				
	}

	@Override
	public World getWorld()
	{
		return base.getWorld();
	}

	@Override
	public boolean teleport(Location lctn)
	{
		return base.teleport(lctn);				
	}

	@Override
	public boolean teleport(Location lctn, TeleportCause tc)
	{
		return base.teleport(this, tc);
	}

	@Override
	public boolean teleport(Entity entity)
	{
		return base.teleport(entity);
	}

	@Override
	public boolean teleport(Entity entity, TeleportCause tc)
	{
		return base.teleport(entity, tc);
	}

	@Override
	public List<Entity> getNearbyEntities(double d, double d1, double d2)
	{
		return base.getNearbyEntities(d, d1, d2);
	}

	@Override
	public int getEntityId()
	{
		return base.getEntityId();
	}

	@Override
	public int getFireTicks()
	{
		return base.getFireTicks();
	}

	@Override
	public int getMaxFireTicks()
	{
		return base.getMaxFireTicks();
	}

	@Override
	public void setFireTicks(int i)
	{
		base.setFireTicks(i);
	}

	@Override
	public void remove()
	{
		base.remove();
	}

	@Override
	public boolean isDead()
	{
		return base.isDead();
	}

	@Override
	public Server getServer()
	{
		return base.getServer();
	}

	@Override
	public Entity getPassenger()
	{
		return base.getPassenger();
	}

	@Override
	public boolean setPassenger(Entity entity)
	{
		return base.setPassenger(entity);
	}

	@Override
	public boolean isEmpty()
	{
		return base.isEmpty();
	}

	@Override
	public boolean eject()
	{
		return base.eject();
	}

	@Override
	public float getFallDistance()
	{
		return base.getFallDistance();
	}

	@Override
	public void setFallDistance(float f)
	{
		base.setFallDistance(f);
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent ede)
	{
		base.setLastDamageCause(ede);
	}

	@Override
	public EntityDamageEvent getLastDamageCause()
	{
		return base.getLastDamageCause();
	}

	@Override
	public UUID getUniqueId()
	{
		return base.getUniqueId();
	}

	@Override
	public int getTicksLived()
	{
		return base.getTicksLived();
	}

	@Override
	public void setTicksLived(int i)
	{
		base.setTicksLived(i);
	}

	@Override
	public boolean isPermissionSet(String string)
	{
		return base.isPermissionSet(string);
	}

	@Override
	public boolean isPermissionSet(Permission prmsn)
	{
		return base.isPermissionSet(prmsn);
	}

	@Override
	public boolean hasPermission(String string)
	{
		return base.hasPermission(string);
	}

	@Override
	public boolean hasPermission(Permission prmsn)
	{
		return base.hasPermission(prmsn);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln)
	{
		return base.addAttachment(plugin, string, bln);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin)
	{
		return base.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i)
	{
		return base.addAttachment(plugin, string, bln, i);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int i)
	{
		return base.addAttachment(plugin, i);
	}

	@Override
	public void removeAttachment(PermissionAttachment pa)
	{
		base.removeAttachment(pa);
	}

	@Override
	public void recalculatePermissions()
	{
		base.recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions()
	{
		return base.getEffectivePermissions();
	}

	@Override
	public boolean isOp()
	{
		return base.isOp();
	}

	@Override
	public void setOp(boolean bln)
	{
		base.setOp(bln);
	}

	@Override
	public void sendMessage(String string)
	{
		base.sendMessage(string);
	}

	@Override
	public boolean isOnline()
	{
		return base.isOnline();
	}

	@Override
	public boolean isBanned()
	{
		return base.isBanned();
	}

	@Override
	public boolean isWhitelisted()
	{
		return base.isWhitelisted();
	}

	@Override
	public void setWhitelisted(boolean bln)
	{
		base.setWhitelisted(bln);
	}

	@Override
	public Player getPlayer()
	{
		return base.getPlayer();
	}

	@Override
	public long getFirstPlayed()
	{
		return base.getFirstPlayed();
	}

	@Override
	public long getLastPlayed()
	{
		return base.getLastPlayed();
	}

	@Override
	public boolean hasPlayedBefore()
	{
		return base.hasPlayedBefore();
	}

	@Override
	public Map<String, Object> serialize()
	{
		return base.serialize();
	}	
}
