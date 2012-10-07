package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;


public class EssentialsEntityListener implements Listener
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final IEssentials ess;

	public EssentialsEntityListener(IEssentials ess)
	{
		this.ess = ess;
	}

	// This method does something undocumented reguarding certain bucket types #EasterEgg
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(final EntityDamageByEntityEvent event)
	{
		final Entity eAttack = event.getDamager();
		final Entity eDefend = event.getEntity();
		if (eDefend instanceof Player && eAttack instanceof Player)
		{
			final User defender = ess.getUser(eDefend);
			final User attacker = ess.getUser(eAttack);

			if (ess.getSettings().getLoginAttackDelay() > 0 && !attacker.isAuthorized("essentials.pvpdelay.exempt")
				&& (System.currentTimeMillis() < (attacker.getLastLogin() + ess.getSettings().getLoginAttackDelay())))
			{
				event.setCancelled(true);
			}

			if (attacker.hasInvulnerabilityAfterTeleport() || defender.hasInvulnerabilityAfterTeleport())
			{
				event.setCancelled(true);
			}

			if (attacker.isGodModeEnabled() && !attacker.isAuthorized("essentials.god.pvp"))
			{
				event.setCancelled(true);
			}

			if (attacker.isHidden() && !attacker.isAuthorized("essentials.vanish.pvp"))
			{
				event.setCancelled(true);
			}

			attacker.updateActivity(true);
			final List<String> commandList = attacker.getPowertool(attacker.getItemInHand());
			if (commandList != null && !commandList.isEmpty())
			{
				for (final String command : commandList)
				{
					if (command != null && !command.isEmpty())
					{
						ess.scheduleSyncDelayedTask(
								new Runnable()
								{
									@Override
									public void run()
									{
										attacker.getServer().dispatchCommand(attacker.getBase(), command.replaceAll("\\{player\\}", defender.getName()));
										LOGGER.log(Level.INFO, String.format("[PT] %s issued server command: /%s", attacker.getName(), command));
									}
								});

						event.setCancelled(true);
						return;
					}
				}
			}
		}
		else if (eAttack instanceof Player)
		{
			final User player = ess.getUser(eAttack);
			player.updateActivity(true);
			if (eDefend instanceof Ageable)
			{
				final ItemStack hand = player.getItemInHand();
				if (hand != null && hand.getType() == Material.MILK_BUCKET)
				{
					((Ageable)eDefend).setBaby();
					hand.setType(Material.BUCKET);
					player.setItemInHand(hand);
					player.updateInventory();
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			final Player player = (Player)event.getEntity();
			player.setFireTicks(0);
			player.setRemainingAir(player.getMaximumAir());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityCombust(final EntityCombustEvent event)
	{
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeathEvent(final PlayerDeathEvent event)
	{
		final User user = ess.getUser(event.getEntity());
		if (user.isAuthorized("essentials.back.ondeath") && !ess.getSettings().isCommandDisabled("back"))
		{
			user.setLastLocation();
			user.sendMessage(_("backAfterDeath"));
		}
		if (!ess.getSettings().areDeathMessagesEnabled())
		{
			event.setDeathMessage("");
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDeathExpEvent(final PlayerDeathEvent event)
	{
		final User user = ess.getUser(event.getEntity());
		if (user.isAuthorized("essentials.keepxp"))
		{
			event.setKeepLevel(true);
			event.setDroppedExp(0);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFoodLevelChange(final FoodLevelChangeEvent event)
	{
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityRegainHealth(final EntityRegainHealthEvent event)
	{
		if (event.getRegainReason() == RegainReason.SATIATED && event.getEntity() instanceof Player
			&& ess.getUser(event.getEntity()).isAfk() && ess.getSettings().getFreezeAfkPlayers())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPotionSplashEvent(final PotionSplashEvent event)
	{
		for (LivingEntity entity : event.getAffectedEntities())
		{
			if (entity instanceof Player && ess.getUser(entity).isGodModeEnabled())
			{
				event.setIntensity(entity, 0d);
			}
		}
	}
}
