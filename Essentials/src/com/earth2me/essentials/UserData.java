package com.earth2me.essentials;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class UserData extends PlayerExtension implements IConf {
	private EssentialsConf config;
	private static final Logger logger = Logger.getLogger("Minecraft");
	
	protected UserData(Player base, File folder) {
		super(base);
		folder = new File(folder, "userdata");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		config = new EssentialsConf(new File(folder, base.getName()+".yml"));
		reloadConfig();
	}

	public final void reloadConfig() {
		config.load();
		updateConfig();
		unlimited = _getUnlimited();
		powertools = getPowertools();
		lastLocation = _getLastLocation();
		lastTeleportTimestamp = _getLastTeleportTimestamp();
		lastHealTimestamp = _getLastHealTimestamp();
		jail = _getJail();
		mails = _getMails();
		savedInventory = _getSavedInventory();
		teleportEnabled = getTeleportEnabled();
		ignoredPlayers = getIgnoredPlayers();
		godmode = getGodModeEnabled();
		muted = getMuted();
		muteTimeout = _getMuteTimeout();
		jailed = getJailed();
		jailTimeout = _getJailTimeout();
		lastLogin = _getLastLogin();
		lastLogout = _getLastLogout();		
	}
	
	public double getMoney() {
		if (config.hasProperty("money"))
		{
			return config.getDouble("money", Essentials.getSettings().getStartingBalance());
		}

		try
		{
			return com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(getName()).getBalance();
		}
		catch (Throwable ex)
		{
			return Essentials.getSettings().getStartingBalance();
		}
	}
	
	public void setMoney(double value) {
		try
		{
			com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(getName()).setBalance(value);
		}
		catch (Throwable ex)
		{
			config.setProperty("money", value);
			config.save();
		}
	}
	
	public Location getHome() throws Exception {
		if (config.hasProperty("home"))
		{
			World world = getLocation().getWorld();
			String worldHome = "home.worlds." + world.getName().toLowerCase();
			if (!config.hasProperty(worldHome)) {
				String defaultWorld = config.getString("home.default");
				worldHome = "home.worlds." + defaultWorld;
			}
			return config.getLocation(worldHome, getServer());
		} else {
			throw new Exception("You have not set a home.");
		}
	}

	public void setHome(Location loc, boolean b) {
		String worldName = loc.getWorld().getName().toLowerCase();
		if (worldName == null || worldName.isEmpty()) {
			logger.log(Level.WARNING, "Set Home: World name is null or empty.");
			return;
		}
		if (b) {
			config.setProperty("home.default", worldName);
		}
		
		config.setProperty("home.worlds."+worldName, loc);
		config.save();
	}
	
	public String getNickname() {
		return config.getString("nickname");
	}
	
	public void setNickname(String nick) {
		config.setProperty("nickname", nick);
		config.save();
	}
	
	private List<Integer> unlimited;
	
	private List<Integer> _getUnlimited() {
		return config.getIntList("unlimited", new ArrayList<Integer>());
	}
	
	public List<Integer> getUnlimited() {
		return unlimited;
	}
	
	public boolean hasUnlimited(ItemStack stack) {
		return unlimited.contains(stack.getTypeId());
	}

	public void setUnlimited(ItemStack stack, boolean state) {
		if (unlimited.contains(stack.getTypeId())) {
			unlimited.remove(Integer.valueOf(stack.getTypeId()));
		}
		if (state) {
			unlimited.add(stack.getTypeId());
		}
		config.setProperty("unlimited", unlimited);
		config.save();
	}
	
	private Map<Integer, String> powertools;
	
	@SuppressWarnings("unchecked")
	private Map<Integer, String> getPowertools() {
		Object o = config.getProperty("powertools");
		if (o != null && o instanceof Map) {
			return (Map<Integer, String>)o;
		} else {
			return new HashMap<Integer, String>();
		}
		
	}
	
	public String getPowertool(ItemStack stack) {
		return powertools.get(stack.getTypeId());
	}

	public void setPowertool(ItemStack stack, String command) {
		if (command == null || command.isEmpty()) {
			powertools.remove(stack.getTypeId());
		} else {
			powertools.put(stack.getTypeId(), command);
		}
		config.setProperty("powertools", powertools);
		config.save();
	}
	
	private Location lastLocation;
	
	private Location _getLastLocation() {
		return config.getLocation("lastlocation", getServer());
	}
	
	public Location getLastLocation() {
		return lastLocation;
	}
	
	public void setLastLocation(Location loc) {
		lastLocation = loc;
		config.setProperty("lastlocation", loc);
		config.save();
	}
	
	private long lastTeleportTimestamp;
	
	private long _getLastTeleportTimestamp() {
		return config.getLong("timestamps.lastteleport", 0);
	}
	
	public long getLastTeleportTimestamp() {
		return lastTeleportTimestamp;
	}
	
	public void setLastTeleportTimestamp(long time) {
		lastTeleportTimestamp = time;
		config.setProperty("timestamps.lastteleport", time);
		config.save();
	}
	
	private long lastHealTimestamp;
	
	private long _getLastHealTimestamp() {
		return config.getLong("timestamps.lastheal", 0);
	}
	
	public long getLastHealTimestamp() {
		return lastHealTimestamp;
	}
	
	public void setLastHealTimestamp(long time) {
		lastHealTimestamp = time;
		config.setProperty("timestamps.lastheal", time);
		config.save();
	}
	
	private String jail;
	
	private String _getJail() {
		return config.getString("jail");
	}
	
	public String getJail() {
		return jail;
	}
	
	public void setJail(String jail) {
		if (jail == null || jail.isEmpty()) {
			this.jail = null;
			config.removeProperty("jail");
		} else {
			this.jail = jail;
			config.setProperty("jail", jail);
		}
		config.save();
	}
	
	private List<String> mails;
	
	private List<String> _getMails() {
		return config.getStringList("mail", new ArrayList<String>());
	}
	
	public List<String> getMails() {
		return mails;
	}
	
	public void setMails(List<String> mails) {
		if (mails == null) {
			config.removeProperty("mail");
		} else {
			config.setProperty("mail", mails);
		}
		this.mails = mails;
		config.save();
	}
	
	public void addMail(String mail) {
		mails.add(mail);
		setMails(mails);
	}
	
	private ItemStack[] savedInventory;
	
	public ItemStack[] getSavedInventory() {
		return savedInventory;
	}
	
	private ItemStack[] _getSavedInventory() {
		int size = config.getInt("inventory.size", 0);
		if (size < 1 || size > getInventory().getSize()) {
			return null;
		}
		ItemStack[] is = new ItemStack[size];
		for (int i = 0; i < size; i++) {
			is[i] = config.getItemStack("inventory."+i); 
		}
		return is;
	}
	
	public void setSavedInventory(ItemStack[] is) {
		if (is == null || is.length == 0) {
			savedInventory = null;
			config.removeProperty("inventory");
		} else {
			savedInventory = is;
			config.setProperty("inventory.size", is.length);
			for (int i = 0; i < is.length; i++) {
				if (is[i].getType() == Material.AIR) {
					continue;
				}
				config.setProperty("inventory."+i, is[i]);
			}
		}
	}
	
	private boolean teleportEnabled;
	
	private boolean getTeleportEnabled() {
		return config.getBoolean("teleportenabled", true);
	}
	
	public boolean isTeleportEnabled() {
		return teleportEnabled;
	}
	
	public void setTeleportEnabled(boolean set) {
		teleportEnabled = set;
		config.setProperty("teleportenabled", set);
		config.save();
	}
	
	public boolean toggleTeleportEnabled() {
		boolean ret = !isTeleportEnabled();
		setTeleportEnabled(ret);
		return ret;
	}
	
	private List<String> ignoredPlayers;
	
	public List<String> getIgnoredPlayers() {
		return config.getStringList("ignore", new ArrayList<String>());
	}
	
	public void setIgnoredPlayers(List<String> players) {
		if (players == null || players.isEmpty()) {
			ignoredPlayers = new ArrayList<String>();
			config.removeProperty("ignore");
		} else {
			ignoredPlayers = players;
			config.setProperty("ignore", players);
		}
		config.save();
	}

	public boolean isIgnoredPlayer(String name) {
		return ignoredPlayers.contains(name);
	}
	
	public void setIgnoredPlayer(String name, boolean set) {
		if (set) {
			ignoredPlayers.add(name);
		} else {
			ignoredPlayers.remove(name);
		}
		setIgnoredPlayers(ignoredPlayers);
	}
	
	private boolean godmode;
	
	private boolean getGodModeEnabled() {
		return config.getBoolean("godmode", true);
	}
	
	public boolean isGodModeEnabled() {
		return godmode;
	}
	
	public void setGodModeEnabled(boolean set) {
		godmode = set;
		config.setProperty("godmode", set);
		config.save();
	}
	
	public boolean toggleGodModeEnabled() {
		boolean ret = !isGodModeEnabled();
		setGodModeEnabled(ret);
		return ret;
	}

	private boolean muted;
	
	private boolean getMuted() {
		return config.getBoolean("muted", true);
	}
	
	public boolean isMuted() {
		return muted;
	}
	
	public void setMuted(boolean set) {
		muted = set;
		config.setProperty("muted", set);
		config.save();
	}
	
	public boolean toggleMuted() {
		boolean ret = !isMuted();
		setMuted(ret);
		return ret;
	}
	
	private long muteTimeout;
	
	private long _getMuteTimeout() {
		return config.getLong("timestamps.mute", 0);
	}
	
	public long getMuteTimeout() {
		return muteTimeout;
	}
	
	public void setMuteTimeout(long time) {
		muteTimeout = time;
		config.setProperty("timestamps.mute", time);
		config.save();
	}
	
	private boolean jailed;
	
	private boolean getJailed() {
		return config.getBoolean("jailed", true);
	}
	
	public boolean isJailed() {
		return jailed;
	}
	
	public void setJailed(boolean set) {
		jailed = set;
		config.setProperty("jailed", set);
		config.save();
	}
	
	public boolean toggleJailed() {
		boolean ret = !isJailed();
		setJailed(ret);
		return ret;
	}
	
	private long jailTimeout;
	
	private long _getJailTimeout() {
		return config.getLong("timestamps.jail", 0);
	}
	
	public long getJailTimeout() {
		return jailTimeout;
	}
	
	public void setJailTimeout(long time) {
		jailTimeout = time;
		config.setProperty("timestamps.jail", time);
		config.save();
	}
	
	
	public String getBanReason() {
		return config.getString("ban.reason");
	}
	
	public void setBanReason(String reason) {
		config.setProperty("ban.reason", reason);
		config.save();
	}
	
	public long getBanTimeout() {
		return config.getLong("ban.timeout", 0);
	}
	
	public void setBanTimeout(long time) {
		config.setProperty("ban.timeout", time);
		config.save();
	}
	
	private long lastLogin;
	
	private long _getLastLogin() {
		return config.getLong("timestamps.login", 0);
	}
	
	public long getLastLogin() {
		return lastLogin;
	}
	
	public void setLastLogin(long time) {
		lastLogin = time;
		config.setProperty("timestamps.login", time);
		config.save();
	}
	
	private long lastLogout;
	
	private long _getLastLogout() {
		return config.getLong("timestamps.logout", 0);
	}
	
	public long getLastLogout() {
		return lastLogout;
	}
	
	public void setLastLogout(long time) {
		lastLogout = time;
		config.setProperty("timestamps.logout", time);
		config.save();
	}
	
	private void updateConfig() {
		if (config.hasProperty("home") && !config.hasProperty("home.default")) {
			@SuppressWarnings("unchecked")
			List<Object> vals = (List<Object>)config.getProperty("home");
			World world = getServer() == null ? null : getServer().getWorlds().get(0);
			if (vals.size() > 5 && getServer() != null) {
				world = getServer().getWorld((String)vals.get(5));
			}
			Location loc = new Location(
					world,
					((Number)vals.get(0)).doubleValue(),
					((Number)vals.get(1)).doubleValue(),
					((Number)vals.get(2)).doubleValue(),
					((Number)vals.get(3)).floatValue(),
					((Number)vals.get(4)).floatValue());
			config.removeProperty("home");
			setHome(loc, true);
		}
	}
}
