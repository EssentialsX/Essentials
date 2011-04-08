package com.earth2me.essentials;

import java.net.InetSocketAddress;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.util.Vector;

public class PlayerWrapper implements Player
{
	protected Player base;
	
	public PlayerWrapper(Player base)
	{
		this.base = base;
	}
	
	public final Player getBase()
	{
		return base;
	}
	
	public final Player setBase(Player base)
	{
		return this.base = base;
	}

	@Override
	public void setDisplayName(String string)
	{
		base.setDisplayName(string);
	}

	@Override
	public void setCompassTarget(Location lctn)
	{
		base.setCompassTarget(lctn);
	}

	@Override
	public InetSocketAddress getAddress()
	{
		return base.getAddress();
	}

	@Override
	public void kickPlayer(String string)
	{
		base.kickPlayer(string);
	}

	@Override
	public String getName()
	{
		return base.getName();
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
	public Location getLocation()
	{
		return base.getLocation();
	}

	@Override
	public World getWorld()
	{
		return base.getWorld();
	}

	@Override
	public Server getServer()
	{
		return base.getServer();
	}

	@Override
	public boolean isOnline()
	{
		return base.isOnline();
	}

	@Override
	public boolean isOp()
	{
		return base.isOp();
	}
	
	@Override
	public boolean teleport(Location lctn)
	{
		return base.teleport(lctn);
	}
	
	@Override
	public boolean teleport(Entity entity)
	{
		return base.teleport(entity);
	}

	@Override
	public void sendMessage(String string)
	{
		base.sendMessage(string);
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
	
	/**
	 * This is not deprecated because the underlying method isn't really deprecated; rather, it's just "imperfect".  By
	 * We will continue to use this method even after the underlying CraftBukkit method is changed, so do not deprecate
	 * it.  Chances are Bukkit will also choose to un-deprecate this method at some point.
	 */
	@Override
	public void updateInventory()
	{
		base.updateInventory();
	}

	@Override
	public void chat(String string)
	{
		base.chat(string);
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
	public int getEntityId()
	{
		return base.getEntityId();
	}

	@Override
	public boolean performCommand(String string)
	{
		return base.performCommand(string);
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
	public String getDisplayName()
	{
		if (base.getDisplayName() != null)
			return base.getDisplayName();
		else
			return base.getName();
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
	public Location getEyeLocation()
	{
		return base.getEyeLocation();
	}

	@Override
	public void sendRawMessage(String string) {
		base.sendRawMessage(string);
	}

	@Override
	public Location getCompassTarget()
	{
		return base.getCompassTarget();
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
	@Deprecated
	public void teleportTo(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	@Deprecated
	public void teleportTo(Entity entity)
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
}
