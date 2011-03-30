package com.earth2me.essentials;

import java.util.*;
import java.util.logging.*;
import java.io.*;
import org.bukkit.*;
import com.earth2me.essentials.commands.IEssentialsCommand;
import net.minecraft.server.EntityHuman;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;


public class User extends PlayerExtension implements Comparable<User> 
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private final Yaml yaml = new Yaml(new SafeConstructor());
	private boolean isLoaded = false;
	private final File folder;
	private Map<String, Object> data = new HashMap<String, Object>();
	private static Map<String, User> users = new HashMap<String, User>();
	private boolean teleEnabled = true;
	private long lastTeleport = 0;
	private long lastHeal = 0;
	private boolean justPortaled = false;
	//private TimerTask teleTimer = null;
	private int teleTimer = -1;
	public Location lastLocation = null;
	private User replyTo = null;
	private boolean isNew = false;
	public String currentJail;
	public CraftItemStack[] savedInventory;

	private User(Player base)
	{
		super(base);
		this.folder = new File((Essentials.getStatic() == null ? new File(".") : Essentials.getStatic().getDataFolder()), "userdata");

		if (base instanceof EntityHuman)
		{
			this.lastLocation = getBase().getLocation();
		}
		load();
	}

	public static int size()
	{
		return users.size();
	}

	public static <T> User get(T base)
	{
		if (base instanceof Player)
			return get((Player)base);
		return null;
	}

	public static <T extends Player> User get(T base)
	{
		if (base == null)
			return null;
		
		if (base instanceof User)
			return (User)base;
		
		if (users.containsKey(base.getName()))
			return users.get(base.getName()).update(base);
		
		User u = new User(base);
		users.put(u.getName(), u);
		return u;
	}
	
	public static <T> void charge(T base, IEssentialsCommand cmd) throws Exception
	{
		if (base instanceof Player)
			User.get(base).charge(cmd);
	}

	public boolean isNew()
	{
		return isNew;
	}

	public void respawn(Spawn spawn) throws Exception
	{
		respawn(spawn, null);
	}

	public void respawn(Spawn spawn, final String chargeFor) throws Exception
	{
		teleport(getSafeDestination(spawn.getSpawn(getGroup())), chargeFor);
	}

	private User update(Player base)
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
			return true;

		if (isJailed())
			return false;

		try
		{
			return com.nijikokun.bukkit.Permissions.Permissions.Security.permission(base, node);
		}
		catch (Throwable ex)
		{
			String[] cmds = node.split("\\.", 2);
			return !Essentials.getSettings().isCommandRestricted(cmds[cmds.length - 1]);
		}
	}

	public boolean isTeleEnabled()
	{
		return teleEnabled;
	}

	public boolean toggleTeleEnabled()
	{
		return teleEnabled = !teleEnabled;
	}

	public void teleportCooldown(boolean justCheck) throws Exception
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long cooldown = Essentials.getSettings().getTeleportCooldown();
		long left = lastTeleport + cooldown - now;
		if (left > 0 && !isOp() && !isAuthorized("essentials.teleport.cooldown.bypass"))
			throw new Exception("Time before next teleport: " + Essentials.FormatTime(left));
		// if justCheck is set, don't update lastTeleport; we're just checking
		if (!justCheck) lastTeleport = now;
	}

	public void teleportCooldown() throws Exception
	{
		teleportCooldown(true);
	}

	public void healCooldown() throws Exception
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long cooldown = Essentials.getSettings().getHealCooldown();
		long left = lastHeal + cooldown - now;
		if (left > 0 && !isOp() && !isAuthorized("essentials.heal.cooldown.bypass"))
			throw new Exception("Time before next heal: " + Essentials.FormatTime(left));
		lastHeal = now;
	}

	private void load()
	{
		if (isLoaded) return;
		isLoaded = true;

		data = Essentials.getData(this);

		try
		{
			if (!folder.exists()) folder.mkdirs();
			File file = new File(folder, getName() + ".yml");
			if (!file.exists())
			{
				isNew = true;
				file.createNewFile();
				logger.info(getName() + " has logged in for the first time.");
			}

			FileInputStream rx = new FileInputStream(file);
			Map<String, Object> userData = (Map<String, Object>)yaml.load(new UnicodeReader(rx));
			if (userData != null) data.putAll(userData);
			rx.close();
		}
		catch (Throwable ex)
		{
			logger.log(Level.SEVERE, null, ex);
		}
		finally
		{
			if (data == null) data = new HashMap<String, Object>();
		}
	}

	private void flush()
	{
		try
		{
			if (!folder.exists()) folder.mkdirs();
			File file = new File(folder, getName() + ".yml");
			if (!file.exists()) file.createNewFile();

			FileWriter tx = new FileWriter(file);
			tx.write(yaml.dump(data));
			tx.flush();
			tx.close();
		}
		catch (Throwable ex)
		{
			logger.log(Level.SEVERE, null, ex);
		}
	}

	public boolean isGodModeEnabled()
	{
		load();
		return data.containsKey("godmode") && (Boolean)data.get("godmode");
	}

	public boolean toggleGodMode()
	{
		boolean retval = !isGodModeEnabled();
		data.put("godmode", retval);
		flush();
		return retval;
	}

	public boolean isMuted()
	{
		load();
		return data.containsKey("muted") && (Boolean)data.get("muted");
	}

	public boolean toggleMuted()
	{
		boolean retval = !isMuted();
		data.put("muted", retval);
		flush();
		return retval;
	}

	public boolean isJailed()
	{
		//load(); Do not load config everytime time!
		return data.containsKey("jailed") && (Boolean)data.get("jailed");
	}

	public boolean toggleJailed()
	{
		boolean retval = !isJailed();
		data.put("jailed", retval);
		flush();
		load();
		return retval;
	}

	public double getMoney()
	{
		load();
		if (data.containsKey("money"))
		{
			if (data.get("money") instanceof Integer)
				return (double)((Integer)data.get("money"));
			return (Double)data.get("money");
		}

		try
		{
			return com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(getName()).getBalance();
		}
		catch (Throwable ex)
		{
			try
			{
				Map<String, Object> idata = Essentials.getData(this);
				return (Integer)idata.get("money");
			}
			catch (Throwable ex2)
			{
				return Essentials.getSettings().getStartingBalance();
			}
		}
	}

	public void setMoney(double value)
	{
		try
		{
			com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(getName()).setBalance(value);
		}
		catch (Throwable ex)
		{
			data.put("money", value);
			flush();
		}
	}

	public void giveMoney(double value)
	{
		if (value == 0) return;
		setMoney(getMoney() + value);
		sendMessage("§a$" + value + " has been added to your account.");
	}

	public void payUser(User reciever, int value) throws Exception
	{
		if (value == 0) return;
		if (!canAfford(value))
		{
			throw new Exception("You do not have sufficient funds.");
		}
		else
		{
			setMoney(getMoney() - value);
			reciever.setMoney(reciever.getMoney() + value);
			sendMessage("§a$" + value + " has been sent to " + reciever.getDisplayName());
			reciever.sendMessage("§a$" + value + " has been recieved from " + getDisplayName());
		}
	}

	public void takeMoney(double value)
	{
		if (value == 0) return;
		setMoney(getMoney() - value);
		sendMessage("§c$" + value + " has been taken from your account.");
	}

	public void charge(String cmd) throws Exception
	{
		double mon = getMoney();
		double cost = Essentials.getSettings().getCommandCost(cmd.startsWith("/") ? cmd.substring(1) : cmd);
		if (mon < cost && !isOp())
			throw new Exception("You do not have sufficient funds.");
		takeMoney(cost);
	}

	public void canAfford(String cmd) throws Exception
	{
		double mon = getMoney();
		double cost = Essentials.getSettings().getCommandCost(cmd.startsWith("/") ? cmd.substring(1) : cmd);
		if (mon < cost && !isOp())
			throw new Exception("You do not have sufficient funds.");
	}

	public boolean canAfford(double cost)
	{
		double mon = getMoney();
		if (mon < cost && !isOp())
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

	public void cancelTeleport(boolean notifyUser)
	{
		if (teleTimer == -1) return;
		try
		{
			getServer().getScheduler().cancelTask(teleTimer);
			if (notifyUser) sendMessage("§cPending teleportation request cancelled.");
		}
		catch (Throwable ex)
		{
		}
		finally
		{
			teleTimer = -1;
		}
	}

	public void cancelTeleport()
	{
		cancelTeleport(false);
	}

	public boolean teleport(final Location loc, final String chargeFor)
	{
		final long delay = Essentials.getSettings().getTeleportDelay();

		if (delay <= 0 || isOp() || isAuthorized("essentials.teleport.timer.bypass"))
		{
			try
			{
				if (chargeFor != null) charge(chargeFor);
				teleportCooldown(false);
				return teleportToNow(loc);
			}
			catch (Throwable ex)
			{
				sendMessage("§cError: " + ex.getMessage());
				return false;
			}
		}

		cancelTeleport();
		sendMessage("§7Teleportation will commence in " + Essentials.FormatTime(delay) + ". Don't move.");
		teleTimer = getServer().getScheduler().scheduleSyncRepeatingTask(Essentials.getStatic(), new TeleportTimer(this, delay)
		{
			public void DoTeleport()
			{
				try
				{
					if (chargeFor != null) charge(chargeFor);
					teleportToNow(loc);
				}
				catch (Throwable ex)
				{
					sendMessage("§cError: " + ex.getMessage());
				}
			}
			
			public void DoCancel()
			{
				cancelTeleport();
			}
		}, 10, 10);
		return true;
	}

	@Override
	public boolean teleport(final Location loc)
	{
		return teleport(loc, null);
	}

	public boolean teleport(final Entity entity, final String chargeFor)
	{
		final long delay = Essentials.getSettings().getTeleportDelay();

		if (delay <= 0 || isOp() || isAuthorized("essentials.teleport.timer.bypass"))
		{
			try
			{
				if (chargeFor != null) charge(chargeFor);
				teleportCooldown(false);
				return teleportToNow(entity);
			}
			catch (Throwable ex)
			{
				sendMessage("§cError: " + ex.getMessage());
				return false;
			}
		}

		cancelTeleport();
		sendMessage("§7Teleportation will commence in " + Essentials.FormatTime(delay) + ". Don't move.");
		teleTimer = getServer().getScheduler().scheduleSyncRepeatingTask(Essentials.getStatic(), new TeleportTimer(this, delay)
		{
			public void DoTeleport()
			{
				try
				{
					if (chargeFor != null) charge(chargeFor);
					teleportToNow(entity);
				}
				catch (Throwable ex)
				{
					sendMessage("§cError: " + ex.getMessage());
				}
			}
			
			public void DoCancel()
			{
				cancelTeleport();
			}
		}, 10, 10);
		return true;
	}

	@Override
	public boolean teleport(final Entity entity)
	{
		return teleport(entity, null);
	}

	public Location getHome() throws Exception
	{
		if (data.containsKey("home"))
		{
			List<Object> vals = (List<Object>)data.get("home");
			World world = getServer() == null ? null : getServer().getWorlds().get(0);
			if (vals.size() > 5 && getServer() != null)
				getServer().getWorld((String)vals.get(5));
			return new Location(
					world,
					(Double)vals.get(0),
					(Double)vals.get(1),
					(Double)vals.get(2),
					((Double)vals.get(3)).floatValue(),
					((Double)vals.get(4)).floatValue());
		}

		try
		{
			Map<String, Object> gdata = Essentials.getData(this);
			List<Object> vals = (List<Object>)gdata.get("home");
			World world = getServer().getWorlds().get(0);
			if (vals.size() > 5)
				getServer().getWorld((String)vals.get(5));
			return new Location(world,
								(Double)vals.get(0),
								(Double)vals.get(1),
								(Double)vals.get(2),
								((Double)vals.get(3)).floatValue(),
								((Double)vals.get(4)).floatValue());
		}
		catch (Throwable ex)
		{
			throw new Exception("You have not set a home.");
		}
	}

	public void teleportToHome(final String chargeFor)
	{
		final long delay = Essentials.getSettings().getTeleportDelay();

		Location loc = null;
		try
		{
			// check this first in case user hasn't set a home yet
			loc = getHome();
		}
		catch (Throwable ex)
		{
			sendMessage("§cTeleport: " + ex.getMessage());
			return;
		}

		if (delay <= 0 || isOp() || isAuthorized("essentials.teleport.timer.bypass"))
		{
			try
			{
				if (chargeFor != null) charge(chargeFor);
				teleportCooldown(false);
				teleportToNow(loc);
				sendMessage("§7Teleporting home...");
			}
			catch (Throwable ex)
			{
				sendMessage("§cError: " + ex.getMessage());
			}
			return;
		}

		cancelTeleport();
		sendMessage("§7Teleportation will commence in " + Essentials.FormatTime(delay) + ". Don't move.");
		teleTimer = getServer().getScheduler().scheduleSyncRepeatingTask(Essentials.getStatic(), new TeleportTimer(this, delay)
		{
			public void DoTeleport()
			{
				try
				{
					if (chargeFor != null) charge(chargeFor);
					teleportToNow(getHome());
				}
				catch (Throwable ex)
				{
					sendMessage("§cError: " + ex.getMessage());
				}
			}
			
			public void DoCancel()
			{
				cancelTeleport();
			}
		}, 10, 10);
	}

	public void teleportToHome()
	{
		teleportToHome(null);
	}

	public boolean teleportToNow(Location loc) throws Exception
	{
		cancelTeleport();
		lastLocation = getLocation();
		return getBase().teleport(getSafeDestination(loc));
	}

	public boolean teleportToNow(Entity entity)
	{
		cancelTeleport();
		lastLocation = getLocation();
		return getBase().teleport(entity);
	}

	public void teleportBack(final String chargeFor)
	{
		teleport(lastLocation, chargeFor);
	}

	public void teleportBack()
	{
		teleportBack(null);
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

	public void setReplyTo(User user)
	{
		replyTo = user;
	}

	public User getReplyTo()
	{
		return replyTo;
	}

	public void setHome()
	{
		setHome(getLocation());
	}

	public void setHome(Location home)
	{
		List<Object> vals = new ArrayList<Object>(6);
		vals.add(new Double(home.getX()));
		vals.add(new Double(home.getY()));
		vals.add(new Double(home.getZ()));
		vals.add(new Double(home.getYaw()));
		vals.add(new Double(home.getPitch()));
		vals.add(home.getWorld() == null ? "world" : home.getWorld().getName());
		data.put("home", vals);
		flush();

		setCompassTarget(home);
	}

	public String getNick()
	{
		Essentials ess = Essentials.getStatic();
		String name = ess.getConfiguration().getBoolean("disable-nick", false) ? getName() : ess.readNickname(this);
		if (isOp() && ess.getConfiguration().getString("ops-name-color", "c").matches("^[0-9a-f]$")) {
			name = "§" + ess.getConfiguration().getString("ops-name-color", "c") + name + "§f";
		}
		return name;
	}

	public void warpTo(String warp, final String chargeFor) throws Exception
	{
		lastLocation = getLocation();
		Location loc = Essentials.getWarps().getWarp(warp);
		teleport(loc, chargeFor);
		sendMessage("§7Warping to " + warp + ".");
	}

	public void warpTo(String string) throws Exception
	{
		warpTo(string, null);
	}

	public void clearNewFlag()
	{
		isNew = false;
	}

	public int compareTo(User t) {
		return ChatColor.stripColor(this.getDisplayName()).compareToIgnoreCase(ChatColor.stripColor(t.getDisplayName()));
	}

	public Boolean canSpawnItem(int itemId)
	{
		if(Essentials.getSettings().itemSpawnBlacklist().contains(itemId))return false;
		return true;
	}
}
