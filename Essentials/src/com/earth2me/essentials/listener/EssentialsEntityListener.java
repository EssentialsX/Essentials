package com.earth2me.essentials.listener;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import java.util.List;
import lombok.Cleanup;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;


public class EssentialsEntityListener extends EntityListener
{
	private final transient IEssentials ess;

	public EssentialsEntityListener(final IEssentials ess)
	{
		super();
		this.ess = ess;
	}

	@Override
	public void onEntityDamage(final EntityDamageEvent event)
	{
		if (event instanceof EntityDamageByEntityEvent)
		{
			final EntityDamageByEntityEvent edEvent = (EntityDamageByEntityEvent)event;
			final Entity eAttack = edEvent.getDamager();
			final Entity eDefend = edEvent.getEntity();
			if (eDefend instanceof Player && eAttack instanceof Player)
			{
				@Cleanup
				final IUser attacker = ess.getUser((Player)eAttack);
				attacker.acquireReadLock();
				attacker.updateActivity(true);
				final ItemStack itemstack = attacker.getItemInHand();
				final List<String> commandList = attacker.getData().getPowertool(itemstack.getType());
				if (commandList != null && !commandList.isEmpty())
				{
					for (String command : commandList)
					{

						if (command != null && !command.isEmpty())
						{
							final IUser defender = ess.getUser((Player)eDefend);
							attacker.getServer().dispatchCommand(attacker, command.replaceAll("\\{player\\}", defender.getName()));
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			if (eDefend instanceof Animals && eAttack instanceof Player)
			{
				final IUser player = ess.getUser((Player)eAttack);
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
		if (event.getEntity() instanceof Player && ess.getUser((Player)event.getEntity()).isGodModeEnabled())
		{
			final Player player = (Player)event.getEntity();
			player.setFireTicks(0);
			player.setRemainingAir(player.getMaximumAir());
			event.setCancelled(true);
		}
	}

	@Override
	public void onEntityCombust(final EntityCombustEvent event)
	{
		if (event.getEntity() instanceof Player && ess.getUser((Player)event.getEntity()).isGodModeEnabled())
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
			final IUser user = ess.getUser((Player)pdevent.getEntity());
			@Cleanup
			final ISettings settings = ess.getSettings();
			settings.acquireReadLock();
			if (user.isAuthorized("essentials.back.ondeath") && !settings.getData().getCommands().isDisabled("back"))
			{
				user.setLastLocation();
				user.sendMessage(_("backAfterDeath"));
			}
			if (!settings.getData().getGeneral().isDeathMessages())
			{
				pdevent.setDeathMessage("");
			}
		}
	}

	@Override
	public void onFoodLevelChange(final FoodLevelChangeEvent event)
	{
		if (event.getEntity() instanceof Player && ess.getUser((Player)event.getEntity()).isGodModeEnabled())
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onEntityRegainHealth(final EntityRegainHealthEvent event)
	{

		if (event.getRegainReason() == RegainReason.SATIATED && event.getEntity() instanceof Player)
		{
			@Cleanup
			final ISettings settings = ess.getSettings();
			settings.acquireReadLock();
			@Cleanup
			final IUser user = ess.getUser((Player)event.getEntity());
			user.acquireReadLock();
			if (user.getData().isAfk() && settings.getData().getCommands().getAfk().isFreezeAFKPlayers())
			{
				event.setCancelled(true);
			}
		}
	}
}
