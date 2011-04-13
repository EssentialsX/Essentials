package com.earth2me.essentials;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;


public class OfflinePlayer implements Player
{
	private final String name;
	private Location location = new Location(null, 0, 0, 0, 0, 0);

	public OfflinePlayer(String name)
	{
		this.name = name;
	}

	public boolean isOnline()
	{
		return false;
	}

	public boolean isOp()
	{
		return false;
	}

	public void sendMessage(String string)
	{
	}

	public String getDisplayName()
	{
		return name;
	}

	public void setDisplayName(String string)
	{
	}

	public void setCompassTarget(Location lctn)
	{
	}

	public InetSocketAddress getAddress()
	{
		return null;
	}

	public void kickPlayer(String string)
	{
	}

	public String getName()
	{
		return name;
	}

	public PlayerInventory getInventory()
	{
		return null;
	}

	public ItemStack getItemInHand()
	{
		return null;
	}

	public void setItemInHand(ItemStack is)
	{
	}

	public int getHealth()
	{
		return 0;
	}

	public void setHealth(int i)
	{
	}

	public Egg throwEgg()
	{
		return null;
	}

	public Snowball throwSnowball()
	{
		return null;
	}

	public Arrow shootArrow()
	{
		return null;
	}

	public boolean isInsideVehicle()
	{
		return false;
	}

	public boolean leaveVehicle()
	{
		return false;
	}

	public Vehicle getVehicle()
	{
		return null;
	}

	public Location getLocation()
	{
		return location;
	}

	public World getWorld()
	{
		return null;
	}

	public void teleportTo(Location lctn)
	{
	}

	public void teleportTo(Entity entity)
	{
	}

	public int getEntityId()
	{
		return -1;
	}

	public boolean performCommand(String string)
	{
		return false;
	}

	public boolean isPlayer()
	{
		return false;
	}

	public int getRemainingAir()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setRemainingAir(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getMaximumAir()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setMaximumAir(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isSneaking()
	{
		return false;
	}

	public void setSneaking(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void updateInventory()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void chat(String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public double getEyeHeight()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public double getEyeHeight(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Block> getLineOfSight(HashSet<Byte> hs, int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Block getTargetBlock(HashSet<Byte> hs, int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hs, int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getFireTicks()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getMaxFireTicks()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setFireTicks(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Server getServer()
	{
		return Essentials.getStatic() == null ? null : Essentials.getStatic().getServer();
	}

	public Vector getMomentum()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setMomentum(Vector vector)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setVelocity(Vector vector)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Vector getVelocity()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void damage(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void damage(int i, Entity entity)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Location getEyeLocation()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void sendRawMessage(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Location getCompassTarget()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getMaximumNoDamageTicks()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setMaximumNoDamageTicks(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getLastDamage()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setLastDamage(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getNoDamageTicks()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setNoDamageTicks(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean teleport(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean teleport(Entity entity)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Entity getPassenger()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean setPassenger(Entity entity)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isEmpty()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean eject()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void saveData()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void loadData()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isSleeping()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getSleepTicks()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Entity> getNearbyEntities(double d, double d1, double d2)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isDead()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public float getFallDistance()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setFallDistance(float f)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setSleepingIgnored(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isSleepingIgnored()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
