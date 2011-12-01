package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Delegate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;


public class OfflinePlayer implements Player
{
	private final transient IEssentials ess;
	private Location location = new Location(null, 0, 0, 0, 0, 0);
	private World world;
	private UUID uniqueId = UUID.randomUUID();
	@Delegate(types = org.bukkit.OfflinePlayer.class)
	private final org.bukkit.OfflinePlayer base;

	public OfflinePlayer(final String name, final IEssentials ess)
	{
		this.ess = ess;
		this.world = ess.getServer().getWorlds().get(0);
		this.base = ess.getServer().getOfflinePlayer(name);
	}

	@Override
	public void sendMessage(final String string)
	{
	}

	@Override
	public String getDisplayName()
	{
		return base.getName();
	}

	@Override
	public void setDisplayName(String string)
	{
	}

	@Override
	public void setCompassTarget(Location lctn)
	{
	}

	@Override
	public InetSocketAddress getAddress()
	{
		return null;
	}

	@Override
	public void kickPlayer(String string)
	{
	}

	@Override
	public PlayerInventory getInventory()
	{
		return null;
	}

	@Override
	public ItemStack getItemInHand()
	{
		return null;
	}

	@Override
	public void setItemInHand(ItemStack is)
	{
	}

	@Override
	public int getHealth()
	{
		return 0;
	}

	@Override
	public void setHealth(int i)
	{
	}

	@Override
	public Egg throwEgg()
	{
		return null;
	}

	@Override
	public Snowball throwSnowball()
	{
		return null;
	}

	@Override
	public Arrow shootArrow()
	{
		return null;
	}

	@Override
	public boolean isInsideVehicle()
	{
		return false;
	}

	@Override
	public boolean leaveVehicle()
	{
		return false;
	}

	@Override
	public Vehicle getVehicle()
	{
		return null;
	}

	@Override
	public Location getLocation()
	{
		return location;
	}

	@Override
	public World getWorld()
	{
		return world;
	}

	public void setLocation(Location loc)
	{
		location = loc;
		world = loc.getWorld();
	}

	public void teleportTo(Location lctn)
	{
	}

	public void teleportTo(Entity entity)
	{
	}

	@Override
	public int getEntityId()
	{
		return -1;
	}

	@Override
	public boolean performCommand(String string)
	{
		return false;
	}

	public boolean isPlayer()
	{
		return false;
	}

	@Override
	public int getRemainingAir()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setRemainingAir(int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public int getMaximumAir()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setMaximumAir(int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean isSneaking()
	{
		return false;
	}

	@Override
	public void setSneaking(boolean bln)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void updateInventory()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void chat(String string)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public double getEyeHeight()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public double getEyeHeight(boolean bln)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public List<Block> getLineOfSight(HashSet<Byte> hs, int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public Block getTargetBlock(HashSet<Byte> hs, int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hs, int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public int getFireTicks()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public int getMaxFireTicks()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setFireTicks(int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public Server getServer()
	{
		return ess == null ? null : ess.getServer();
	}

	public Vector getMomentum()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	public void setMomentum(Vector vector)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setVelocity(Vector vector)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public Vector getVelocity()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void damage(int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void damage(int i, Entity entity)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public Location getEyeLocation()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void sendRawMessage(String string)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public Location getCompassTarget()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public int getMaximumNoDamageTicks()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setMaximumNoDamageTicks(int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public int getLastDamage()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setLastDamage(int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public int getNoDamageTicks()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setNoDamageTicks(int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean teleport(Location lctn)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean teleport(Entity entity)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public Entity getPassenger()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean setPassenger(Entity entity)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean isEmpty()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean eject()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void saveData()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void loadData()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean isSleeping()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public int getSleepTicks()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public List<Entity> getNearbyEntities(double d, double d1, double d2)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean isDead()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public float getFallDistance()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setFallDistance(float f)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setSleepingIgnored(boolean bln)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean isSleepingIgnored()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void awardAchievement(Achievement a)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void incrementStatistic(Statistic ststc)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void incrementStatistic(Statistic ststc, int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void incrementStatistic(Statistic ststc, Material mtrl)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void incrementStatistic(Statistic ststc, Material mtrl, int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void playNote(Location lctn, byte b, byte b1)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void sendBlockChange(Location lctn, Material mtrl, byte b)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void sendBlockChange(Location lctn, int i, byte b)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent ede)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public EntityDamageEvent getLastDamageCause()
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public void playEffect(Location lctn, Effect effect, int i)
	{
		throw new UnsupportedOperationException(_("notSupportedYet"));
	}

	@Override
	public boolean sendChunkChange(Location lctn, int i, int i1, int i2, byte[] bytes)
	{
		return true;
	}

	@Override
	public UUID getUniqueId()
	{
		return uniqueId;
	}

	@Override
	public void playNote(Location lctn, Instrument i, Note note)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setPlayerTime(long l, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getPlayerTime()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getPlayerTimeOffset()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isPlayerTimeRelative()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void resetPlayerTime()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isPermissionSet(String string)
	{
		return false;
	}

	@Override
	public boolean isPermissionSet(Permission prmsn)
	{
		return false;
	}

	@Override
	public boolean hasPermission(String string)
	{
		return false;
	}

	@Override
	public boolean hasPermission(Permission prmsn)
	{
		return false;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void removeAttachment(PermissionAttachment pa)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void recalculatePermissions()
	{
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void sendMap(MapView mv)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public GameMode getGameMode()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setGameMode(GameMode gm)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getExperience()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setExperience(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getLevel()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setLevel(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getTotalExperience()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTotalExperience(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public float getExhaustion()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setExhaustion(float f)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public float getSaturation()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setSaturation(float f)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getFoodLevel()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setFoodLevel(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Location getBedSpawnLocation()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isSprinting()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setSprinting(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setPlayerListName(String name)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getPlayerListName()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getTicksLived()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTicksLived(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getMaxHealth()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
