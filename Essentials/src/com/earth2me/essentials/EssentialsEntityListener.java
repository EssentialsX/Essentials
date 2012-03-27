package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;


public class EssentialsEntityListener implements Listener
{
	private final IEssentials ess;

	public EssentialsEntityListener(IEssentials ess)
	{
		this.ess = ess;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(final EntityDamageByEntityEvent event)
	{
		final Entity eAttack = event.getDamager();
		final Entity eDefend = event.getEntity();
		if (eDefend instanceof Player && eAttack instanceof Player)
		{
			final User defender = ess.getUser(eDefend);
			final User attacker = ess.getUser(eAttack);
			if (attacker.hasInvulnerabilityAfterTeleport() || defender.hasInvulnerabilityAfterTeleport()) {
				event.setCancelled(true);
			}
			attacker.updateActivity(true);
			final List<String> commandList = attacker.getPowertool(attacker.getItemInHand());
			if (commandList != null && !commandList.isEmpty())
			{
				for (String command : commandList)
				{
					if (command != null && !command.isEmpty())
					{
						attacker.getServer().dispatchCommand(attacker, command.replaceAll("\\{player\\}", defender.getName()));
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		else if (eDefend instanceof Animals && eAttack instanceof Player)
		{
			final User player = ess.getUser(eAttack);
			final ItemStack hand = player.getItemInHand();
			if (hand != null && hand.getType() == Material.MILK_BUCKET)
			{
				((Animals)eDefend).setAge(-24000);
				hand.setType(Material.BUCKET);
				player.setItemInHand(hand);
				player.updateInventory();
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
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

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
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

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFoodLevelChange(final FoodLevelChangeEvent event)
	{
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityRegainHealth(final EntityRegainHealthEvent event)
	{
		if (event.getRegainReason() == RegainReason.SATIATED && event.getEntity() instanceof Player
			&& ess.getUser(event.getEntity()).isAfk() && ess.getSettings().getFreezeAfkPlayers())
		{
			event.setCancelled(true);
		}
	}
}
