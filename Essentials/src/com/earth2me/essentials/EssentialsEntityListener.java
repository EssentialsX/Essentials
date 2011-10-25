package com.earth2me.essentials;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsEntityListener extends EntityListener
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final IEssentials ess;

	public EssentialsEntityListener(IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent edEvent = (EntityDamageByEntityEvent)event;
			Entity eAttack = edEvent.getDamager();
			Entity eDefend = edEvent.getEntity();
			if (eDefend instanceof Player && eAttack instanceof Player)
			{
				User defender = ess.getUser(eDefend);
				User attacker = ess.getUser(eAttack);
				attacker.updateActivity(true);
				ItemStack is = attacker.getItemInHand();
				List<String> commandList = attacker.getPowertool(is);
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
		}
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			final Player player = (Player)event.getEntity();
			player.setFireTicks(0);
			player.setRemainingAir(player.getMaximumAir());
			event.setCancelled(true);
		}
	}

	@Override
	public void onEntityCombust(EntityCombustEvent event)
	{
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onEntityDeath(final EntityDeathEvent event)
	{
		if (event instanceof PlayerDeathEvent)
		{
			final PlayerDeathEvent pdevent = (PlayerDeathEvent)event;
			final User user = ess.getUser(pdevent.getEntity());
			if (user.isAuthorized("essentials.back.ondeath") && !ess.getSettings().isCommandDisabled("back"))
			{
				user.setLastLocation();
				user.sendMessage(Util.i18n("backAfterDeath"));
			}
			if (!ess.getSettings().areDeathMessagesEnabled())
			{
				pdevent.setDeathMessage("");
			}
		}
	}

	@Override
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		LOGGER.log(Level.INFO, "Getting hungry...");
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		if (event.getRegainReason() == RegainReason.SATIATED && event.getEntity() instanceof Player
			&& ess.getUser(event.getEntity()).isAfk() && ess.getSettings().getFreezeAfkPlayers())
		{
			event.setCancelled(true);
		}
	}
}
