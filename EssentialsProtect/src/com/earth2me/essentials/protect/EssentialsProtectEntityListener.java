package com.earth2me.essentials.protect;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.ChunkPosition;
import net.minecraft.server.Packet60Explosion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.ExplosionPrimeEvent;


public class EssentialsProtectEntityListener extends EntityListener
{
	private final transient IProtect prot;
	private final transient IEssentials ess;
	
	public EssentialsProtectEntityListener(final IProtect prot)
	{
		this.prot = prot;
		this.ess = prot.getEssentials();
	}
	
	@Override
	public void onEntityDamage(final EntityDamageEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final Entity target = event.getEntity();
		final User user = ess.getUser(target);
		if (event instanceof EntityDamageByBlockEvent)
		{
			final DamageCause cause = event.getCause();
			
			if (prot.getSettingBool(ProtectConfig.disable_contactdmg)
				&& cause == DamageCause.CONTACT
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.contact")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			if (prot.getSettingBool(ProtectConfig.disable_lavadmg)
				&& cause == DamageCause.LAVA
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.lava")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			if (prot.getSettingBool(ProtectConfig.prevent_tnt_explosion)
				&& cause == DamageCause.BLOCK_EXPLOSION
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.tnt")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
		}
		
		if (event instanceof EntityDamageByEntityEvent)
		{
			final EntityDamageByEntityEvent edEvent = (EntityDamageByEntityEvent)event;
			final Entity eAttack = edEvent.getDamager();
			final User attacker = ess.getUser(eAttack);

			// PVP Settings
			if (target instanceof Player && eAttack instanceof Player
				&& prot.getSettingBool(ProtectConfig.disable_pvp)
				&& (!user.isAuthorized("essentials.protect.pvp") || !attacker.isAuthorized("essentials.protect.pvp")))
			{
				event.setCancelled(true);
				return;
			}

			//Creeper explode prevention
			if (eAttack instanceof Creeper && prot.getSettingBool(ProtectConfig.prevent_creeper_explosion)
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.creeper")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			
			if (eAttack instanceof Creeper && prot.getSettingBool(ProtectConfig.prevent_creeper_playerdmg)
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.creeper")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			
			if (eAttack instanceof Fireball && prot.getSettingBool(ProtectConfig.prevent_fireball_playerdmg)
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.fireball")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			
			if (eAttack instanceof TNTPrimed && prot.getSettingBool(ProtectConfig.prevent_tnt_playerdmg)
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.tnt")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			
			if (edEvent.getDamager() instanceof Projectile
				&& target instanceof Player
				&& ((prot.getSettingBool(ProtectConfig.disable_projectiles)
					 && !(user.isAuthorized("essentials.protect.damage.projectiles")
						  && !user.isAuthorized("essentials.protect.damage.disable")))
					|| (((Projectile)edEvent.getDamager()).getShooter() instanceof Player
						&& prot.getSettingBool(ProtectConfig.disable_pvp)
						&& (!user.isAuthorized("essentials.protect.pvp")
							|| !ess.getUser(((Projectile)edEvent.getDamager()).getShooter()).isAuthorized("essentials.protect.pvp")))))
			{
				event.setCancelled(true);
				return;
			}
		}
		
		final DamageCause cause = event.getCause();
		if (target instanceof Player)
		{
			if (cause == DamageCause.FALL
				&& prot.getSettingBool(ProtectConfig.disable_fall)
				&& !(user.isAuthorized("essentials.protect.damage.fall")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			
			if (cause == DamageCause.SUFFOCATION
				&& prot.getSettingBool(ProtectConfig.disable_suffocate)
				&& !(user.isAuthorized("essentials.protect.damage.suffocation")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			if ((cause == DamageCause.FIRE
				 || cause == DamageCause.FIRE_TICK)
				&& prot.getSettingBool(ProtectConfig.disable_firedmg)
				&& !(user.isAuthorized("essentials.protect.damage.fire")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			if (cause == DamageCause.DROWNING
				&& prot.getSettingBool(ProtectConfig.disable_drown)
				&& !(user.isAuthorized("essentials.protect.damage.drowning")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			if (cause == DamageCause.LIGHTNING
				&& prot.getSettingBool(ProtectConfig.disable_lightning)
				&& !(user.isAuthorized("essentials.protect.damage.lightning")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@Override
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final int maxHeight = ess.getSettings().getProtectCreeperMaxHeight();
		//Nicccccccccce plaaacccccccccce..
		if (event.getEntity() instanceof LivingEntity
			&& (prot.getSettingBool(ProtectConfig.prevent_creeper_explosion)
				|| prot.getSettingBool(ProtectConfig.prevent_creeper_blockdmg)
				|| (maxHeight >= 0 && event.getLocation().getBlockY() > maxHeight)))
		{
			try
			{
				final Set<ChunkPosition> set = new HashSet<ChunkPosition>(event.blockList().size());
				final Player[] players = ess.getServer().getOnlinePlayers();
				final Set<ChunkPosition> blocksUnderPlayers = new HashSet<ChunkPosition>(players.length);
				final Location loc = event.getLocation();
				for (Player player : players)
				{
					if (player.getWorld().equals(loc.getWorld()))
					{
						blocksUnderPlayers.add(
								new ChunkPosition(
								player.getLocation().getBlockX(),
								player.getLocation().getBlockY() - 1,
								player.getLocation().getBlockZ()));
					}
				}
				ChunkPosition cp;
				for (Block block : event.blockList())
				{
					cp = new ChunkPosition(block.getX(), block.getY(), block.getZ());
					if (!blocksUnderPlayers.contains(cp))
					{
						set.add(cp);
					}
				}
				
				((CraftServer)ess.getServer()).getHandle().sendPacketNearby(loc.getX(), loc.getY(), loc.getZ(), 64.0D, ((CraftWorld)loc.getWorld()).getHandle().worldProvider.dimension,
																			new Packet60Explosion(loc.getX(), loc.getY(), loc.getZ(), 3.0f, set));
			}
			catch (Throwable ex)
			{
				Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
			}
			event.setCancelled(true);
			return;
		}
		else if (event.getEntity() instanceof TNTPrimed
				 && prot.getSettingBool(ProtectConfig.prevent_tnt_explosion))
		{
			event.setCancelled(true);
			return;
		}
		else if (event.getEntity() instanceof Fireball
				 && prot.getSettingBool(ProtectConfig.prevent_fireball_explosion))
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
				&& prot.getSettingBool(ProtectConfig.protect_rails))
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
				&& prot.getSettingBool(ProtectConfig.protect_signs))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@Override
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
		final String creatureName = event.getCreatureType().toString().toLowerCase();
		if (creatureName == null || creatureName.isEmpty())
		{
			return;
		}
		if (ess.getSettings().getProtectPreventSpawn(creatureName))
		{
			event.setCancelled(true);
		}
	}
	
	@Override
	public void onEntityTarget(final EntityTargetEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (!(event.getTarget() instanceof Player))
		{
			return;
		}
		final User user = ess.getUser(event.getTarget());
		if ((event.getReason() == TargetReason.CLOSEST_PLAYER
			 || event.getReason() == TargetReason.TARGET_ATTACKED_ENTITY
			 || event.getReason() == TargetReason.PIG_ZOMBIE_TARGET
			 || event.getReason() == TargetReason.RANDOM_TARGET
			 || event.getReason() == TargetReason.TARGET_ATTACKED_OWNER
			 || event.getReason() == TargetReason.OWNER_ATTACKED_TARGET)
			&& prot.getSettingBool(ProtectConfig.prevent_entitytarget)
			&& !user.isAuthorized("essentials.protect.entitytarget.bypass"))
		{
			event.setCancelled(true);
			return;
		}
	}
	
	@Override
	public void onExplosionPrime(ExplosionPrimeEvent event)
	{
		if (event.getEntity() instanceof Fireball
			&& prot.getSettingBool(ProtectConfig.prevent_fireball_fire))
		{
			event.setFire(false);
		}
	}
}
