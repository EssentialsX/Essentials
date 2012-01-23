package com.earth2me.essentials.protect;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.craftbukkit.FakeExplosion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.*;


public class EssentialsProtectEntityListener implements Listener
{
	private final transient IProtect prot;
	private final transient IEssentials ess;

	public EssentialsProtectEntityListener(final IProtect prot)
	{
		super();
		this.prot = prot;
		this.ess = prot.getEssentialsConnect().getEssentials();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(final EntityDamageEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			final Entity target = event.getEntity();

			if (target instanceof Villager && settings.getData().getPrevent().isVillagerDeath())
			{
				event.setCancelled(true);
				return;
			}

			final IUser user = target instanceof Player ? ess.getUser((Player)target) : null;
			if (target instanceof Player && event instanceof EntityDamageByBlockEvent)
			{
				final DamageCause cause = event.getCause();

				if (cause == DamageCause.CONTACT
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_CONTACT)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}
				if (cause == DamageCause.LAVA
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_LAVADAMAGE)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}
				if (cause == DamageCause.BLOCK_EXPLOSION
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_TNT)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}
			}

			if (target instanceof Player && event instanceof EntityDamageByEntityEvent)
			{
				final EntityDamageByEntityEvent edEvent = (EntityDamageByEntityEvent)event;
				final Entity eAttack = edEvent.getDamager();
				final IUser attacker = eAttack instanceof Player ? ess.getUser((Player)eAttack) : null;

				// PVP Settings
				if (target instanceof Player && eAttack instanceof Player
					&& (!user.isAuthorized(Permissions.PVP) || !attacker.isAuthorized(Permissions.PVP)))
				{
					event.setCancelled(true);
					return;
				}

				//Creeper explode prevention
				if (eAttack instanceof Creeper && settings.getData().getPrevent().isCreeperExplosion()
					|| (user.isAuthorized(Permissions.PREVENTDAMAGE_CREEPER)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}

				if ((event.getEntity() instanceof Fireball || event.getEntity() instanceof SmallFireball)
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_FIREBALL)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}

				if (eAttack instanceof TNTPrimed
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_TNT)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}

				if (edEvent.getDamager() instanceof Projectile
					&& ((user.isAuthorized(Permissions.PREVENTDAMAGE_PROJECTILES)
						 && !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE))
						|| (((Projectile)edEvent.getDamager()).getShooter() instanceof Player
							&& (!user.isAuthorized(Permissions.PVP)
								|| !ess.getUser((Player)((Projectile)edEvent.getDamager()).getShooter()).isAuthorized(Permissions.PVP)))))
				{
					event.setCancelled(true);
					return;
				}
			}

			final DamageCause cause = event.getCause();
			if (target instanceof Player)
			{
				if (cause == DamageCause.FALL
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_FALL)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}

				if (cause == DamageCause.SUFFOCATION
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_SUFFOCATION)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}
				if ((cause == DamageCause.FIRE
					 || cause == DamageCause.FIRE_TICK)
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_FIRE)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}
				if (cause == DamageCause.DROWNING
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_DROWNING)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}
				if (cause == DamageCause.LIGHTNING
					&& (user.isAuthorized(Permissions.PREVENTDAMAGE_LIGHTNING)
						&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
				{
					event.setCancelled(true);
					return;
				}
			}
		}
		finally
		{
			settings.unlock();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			final int maxHeight = settings.getData().getCreeperMaxHeight();

			if (event.getEntity() instanceof EnderDragon
				&& settings.getData().getPrevent().isEnderdragonBlockdamage())
			{
				event.setCancelled(true);
				return;
			}
			else if (event.getEntity() instanceof Creeper
					 && (settings.getData().getPrevent().isCreeperExplosion()
						 || settings.getData().getPrevent().isCreeperBlockdamage()
						 || (maxHeight >= 0 && event.getLocation().getBlockY() > maxHeight)))
			{
				//Nicccccccccce plaaacccccccccce..
				FakeExplosion.createExplosion(event, ess.getServer(), ess.getServer().getOnlinePlayers());
				event.setCancelled(true);
				return;
			}
			else if (event.getEntity() instanceof TNTPrimed
					 && settings.getData().getPrevent().isTntExplosion())
			{
				event.setCancelled(true);
				return;
			}
			else if ((event.getEntity() instanceof Fireball || event.getEntity() instanceof SmallFireball)
					 && settings.getData().getPrevent().isFireballExplosion())
			{
				event.setCancelled(true);
				return;
			}
			// This code will prevent explosions near protected rails, signs or protected chests
			// TODO: Use protect db instead of this code

			for (Block block : event.blockList())
			{
				if ((block.getRelative(BlockFace.UP).getType() == Material.RAILS
					 || block.getType() == Material.RAILS
					 || block.getRelative(BlockFace.UP).getType() == Material.POWERED_RAIL
					 || block.getType() == Material.POWERED_RAIL
					 || block.getRelative(BlockFace.UP).getType() == Material.DETECTOR_RAIL
					 || block.getType() == Material.DETECTOR_RAIL)
					&& settings.getData().getSignsAndRails().isProtectRails())
				{
					event.setCancelled(true);
					return;
				}
				if ((block.getType() == Material.WALL_SIGN
					 || block.getRelative(BlockFace.NORTH).getType() == Material.WALL_SIGN
					 || block.getRelative(BlockFace.EAST).getType() == Material.WALL_SIGN
					 || block.getRelative(BlockFace.SOUTH).getType() == Material.WALL_SIGN
					 || block.getRelative(BlockFace.WEST).getType() == Material.WALL_SIGN
					 || block.getType() == Material.SIGN_POST
					 || block.getRelative(BlockFace.UP).getType() == Material.SIGN_POST)
					&& settings.getData().getSignsAndRails().isProtectSigns())
				{
					event.setCancelled(true);
					return;
				}
			}
		}
		finally
		{
			settings.unlock();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(final CreatureSpawnEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			return;
		}
		if (event.isCancelled())
		{
			return;
		}
		final CreatureType creature = event.getCreatureType();
		if (creature == null)
		{
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			final Boolean prevent = settings.getData().getPrevent().getSpawn().get(creature);
			if (prevent != null && prevent)
			{
				event.setCancelled(true);
			}
		}
		finally
		{
			settings.unlock();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityTarget(final EntityTargetEvent event)
	{
		if (event.isCancelled() || !(event.getTarget() instanceof Player))
		{
			return;
		}
		final IUser user = ess.getUser((Player)event.getTarget());
		if ((event.getReason() == TargetReason.CLOSEST_PLAYER
			 || event.getReason() == TargetReason.TARGET_ATTACKED_ENTITY
			 || event.getReason() == TargetReason.PIG_ZOMBIE_TARGET
			 || event.getReason() == TargetReason.RANDOM_TARGET
			 || event.getReason() == TargetReason.TARGET_ATTACKED_OWNER
			 || event.getReason() == TargetReason.OWNER_ATTACKED_TARGET)
			&& user.isAuthorized(Permissions.ENTITYTARGET))
		{
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplosionPrime(final ExplosionPrimeEvent event)
	{
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			if ((event.getEntity() instanceof Fireball || event.getEntity() instanceof SmallFireball)
				&& settings.getData().getPrevent().isFireballFire())
			{
				event.setFire(false);
			}
		}
		finally
		{
			settings.unlock();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEndermanPickup(final EndermanPickupEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			if (settings.getData().getPrevent().isEndermanPickup())
			{
				event.setCancelled(true);
			}
		}
		finally
		{
			settings.unlock();
		}
	}
}
