package com.earth2me.essentials;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.InventoryPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.ItemStack;


public class EssentialsPlayerListener extends PlayerListener
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private final Server server;
	private final Essentials ess;
	private EssentialsBlockListener essBlockListener = null;

	public EssentialsPlayerListener(Essentials parent)
	{
		this.ess = parent;
		this.server = parent.getServer();
		essBlockListener = new EssentialsBlockListener(parent);
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		User user = ess.getUser(event.getPlayer());
		user.setDisplayName(user.getNick());
		updateCompass(user);
		if (user.isJailed() && user.getJail() != null && !user.getJail().isEmpty()) {
			try
			{
				event.setRespawnLocation(Essentials.getJail().getJail(user.getJail()));
			}
			catch (Exception ex)
			{
			}
		}
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event)
	{
		User user = ess.getUser(event.getPlayer());
		if (user.isMuted())
		{
			event.setCancelled(true);
			logger.info(Util.format("mutedUserSpeaks", user.getName()));
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());

		if (!ess.getSettings().getNetherPortalsEnabled())
		{
			return;
		}

		final Block block = event.getPlayer().getWorld().getBlockAt(event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ());
		List<World> worlds = server.getWorlds();

		if (block.getType() == Material.PORTAL && worlds.size() > 1 && user.isAuthorized("essentials.portal"))
		{
			if (user.getJustPortaled())
			{
				return;
			}

			Location loc = event.getTo();
			World nether = server.getWorld(ess.getSettings().getNetherName());
			if (nether == null) {
				for (World world : worlds)
				{
					if (world.getEnvironment() == World.Environment.NETHER) {
						nether = world;
						break;
					}
				}
				if (nether == null) {
					return;
				}
			}
			final World world = user.getWorld() == nether ? worlds.get(0) : nether;

			double factor;
			if (user.getWorld().getEnvironment() == World.Environment.NETHER && world.getEnvironment() == World.Environment.NORMAL)
			{
				factor = ess.getSettings().getNetherRatio();
			}
			else if (user.getWorld().getEnvironment() != world.getEnvironment())
			{
				factor = 1.0 / ess.getSettings().getNetherRatio();
			}
			else
			{
				factor = 1.0;
			}

			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();

			if (user.getWorld().getBlockAt(x, y, z - 1).getType() == Material.PORTAL)
			{
				z--;
			}
			if (user.getWorld().getBlockAt(x - 1, y, z).getType() == Material.PORTAL)
			{
				x--;
			}

			x = (int)(x * factor);
			z = (int)(z * factor);
			loc = new Location(world, x + .5, y, z + .5);

			Block dest = world.getBlockAt(x, y, z);
			NetherPortal portal = NetherPortal.findPortal(dest);
			if (portal == null)
			{
				if (world.getEnvironment() == World.Environment.NETHER || ess.getSettings().getGenerateExitPortals())
				{
					portal = NetherPortal.createPortal(dest);
					logger.info(Util.format("userCreatedPortal", event.getPlayer().getName()));
					user.sendMessage(Util.i18n("generatingPortal"));
					loc = portal.getSpawn();
				}
			}
			else
			{
				logger.info(Util.format("userUsedPortal", event.getPlayer().getName()));
				user.sendMessage(Util.i18n("usingPortal"));
				loc = portal.getSpawn();
			}

			event.setFrom(loc);
			event.setTo(loc);
			try
			{
				user.getTeleport().teleport(loc, "portal");
			}
			catch (Exception ex)
			{
				user.sendMessage(ex.getMessage());
			}
			user.setJustPortaled(true);
			user.sendMessage(Util.i18n("teleportingPortal"));

			event.setCancelled(true);
			return;
		}

		user.setJustPortaled(false);
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		User user = ess.getUser(event.getPlayer());

		if (user.getSavedInventory() != null)
		{
			user.getInventory().setContents(user.getSavedInventory());
			user.setSavedInventory(null);
		}
		if (!ess.getSettings().getReclaimSetting())
		{
			return;
		}
		user.dispose();
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					Thread.sleep(1000);
					Runtime rt = Runtime.getRuntime();
					double mem = rt.freeMemory();
					rt.runFinalization();
					rt.gc();
					mem = rt.freeMemory() - mem;
					mem /= 1024 * 1024;
					logger.log(Level.INFO, Util.format("freedMemory", mem));
				}
				catch (InterruptedException ex)
				{
					return;
				}
			}
		});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Essentials.getBackup().onPlayerJoin();
		User user = ess.getUser(event.getPlayer());

		//we do not know the ip address on playerlogin so we need to do this here.
		if (user.isIpBanned())
		{
			String banReason = user.getBanReason();
			user.kickPlayer(banReason != null && !banReason.isEmpty() ? banReason : Util.i18n("defaultBanReason"));
			return;
		}

		user.setDisplayName(user.getNick());

		if (!ess.getSettings().isCommandDisabled("motd") && user.isAuthorized("essentials.motd"))
		{
			for (String m : ess.getMotd(user, null))
			{
				if (m == null)
				{
					continue;
				}
				user.sendMessage(m);
			}
		}

		if (!ess.getSettings().isCommandDisabled("mail") && user.isAuthorized("essentials.mail"))
		{
			List<String> mail = user.getMails();
			if (mail.isEmpty())
			{
				user.sendMessage(Util.i18n("noNewMail"));
			}
			else
			{
				user.sendMessage(Util.format("youHaveNewMail", mail.size()));
			}
		}
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		if (event.getResult() != Result.ALLOWED)
		{
			return;
		}
		User user = ess.getUser(event.getPlayer());
		user.setNPC(false);

		if (user.isBanned())
		{
			String banReason = user.getBanReason();
			event.disallow(Result.KICK_BANNED, banReason != null && !banReason.isEmpty() ? banReason : Util.i18n("defaultBanReason"));
			return;
		}

		if (server.getOnlinePlayers().length >= server.getMaxPlayers() && !user.isAuthorized("essentials.joinfullserver"))
		{
			event.disallow(Result.KICK_FULL, Util.i18n("serverFull"));
			return;
		}

		user.setLastLogin(System.currentTimeMillis());
		updateCompass(user);
	}

	private void updateCompass(User user)
	{
		try
		{
			user.setCompassTarget(user.getHome());
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		User user = ess.getUser(event.getPlayer());
		if (!user.isJailed() || user.getJail() == null || user.getJail().isEmpty())
		{
			return;
		}
		try
		{
			event.setTo(Essentials.getJail().getJail(user.getJail()));
		}
		catch (Exception ex)
		{
			logger.log(Level.WARNING, Util.i18n("returnPlayerToJailError"), ex);
		}
		user.sendMessage(Util.i18n("jailMessage"));
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}
		User user = ess.getUser(event.getPlayer());
		if (user.isJailed())
		{
			event.setCancelled(true);
			return;
		}
		if (!ess.getSettings().areSignsDisabled() && EssentialsBlockListener.protectedBlocks.contains(event.getClickedBlock().getType()))
		{
			if (!user.isAuthorized("essentials.signs.protection.override"))
			{
				if (essBlockListener.isBlockProtected(event.getClickedBlock(), user))
				{
					event.setCancelled(true);
					user.sendMessage(Util.format("noAccessPermission", event.getClickedBlock().getType().toString().toLowerCase()));
					return;
				}
			}
		}

		if (ess.getSettings().getBedSetsHome() && event.getClickedBlock().getType() == Material.BED_BLOCK)
		{
			try
			{
				user.setHome();
				user.sendMessage(Util.i18n("homeSetToBed"));
			}
			catch (Throwable ex)
			{
			}
		}


		if (ess.getSettings().areSignsDisabled())
		{
			return;
		}
		if (event.getClickedBlock().getType() != Material.WALL_SIGN && event.getClickedBlock().getType() != Material.SIGN_POST)
		{
			return;
		}
		Sign sign = new CraftSign(event.getClickedBlock());

		try
		{
			if (sign.getLine(0).equals("§1[Free]") && user.isAuthorized("essentials.signs.free.use"))
			{
				ItemStack item = ItemDb.get(sign.getLine(1));
				CraftInventoryPlayer inv = new CraftInventoryPlayer(new InventoryPlayer(user.getHandle()));
				inv.clear();
				item.setAmount(9 * 4 * 64);
				inv.addItem(item);
				user.showInventory(inv);
				return;
			}
			if (sign.getLine(0).equals("§1[Disposal]") && user.isAuthorized("essentials.signs.disposal.use"))
			{
				CraftInventoryPlayer inv = new CraftInventoryPlayer(new InventoryPlayer(user.getHandle()));
				inv.clear();
				user.showInventory(inv);
				return;
			}
			if (sign.getLine(0).equals("§1[Heal]") && user.isAuthorized("essentials.signs.heal.use"))
			{
				if (!sign.getLine(1).isEmpty())
				{
					String[] l1 = sign.getLine(1).split("[ :-]+");
					boolean m1 = l1[0].matches("\\$[0-9]+");
					int q1 = Integer.parseInt(m1 ? l1[0].substring(1) : l1[0]);
					if (q1 < 1)
					{
						throw new Exception(Util.i18n("moreThanZero"));
					}
					if (m1)
					{
						if (user.getMoney() < q1)
						{
							throw new Exception(Util.i18n("notEnoughMoney"));
						}
						user.takeMoney(q1);
						user.sendMessage(Util.format("moneyTaken", Util.formatCurrency(q1)));
					}
					else
					{
						ItemStack i = ItemDb.get(l1[1], q1);
						if (!InventoryWorkaround.containsItem(user.getInventory(), true, i))
						{
							throw new Exception(Util.format("missingItems", q1, l1[1]));
						}
						InventoryWorkaround.removeItem(user.getInventory(), true, i);
						user.updateInventory();
					}
				}
				user.setHealth(20);
				user.sendMessage(Util.i18n("youAreHealed"));
				return;
			}
			if (sign.getLine(0).equals("§1[Mail]") && user.isAuthorized("essentials.signs.mail.use") && user.isAuthorized("essentials.mail"))
			{
				List<String> mail = user.getMails();
				if (mail.isEmpty())
				{
					user.sendMessage(Util.i18n("noNewMail"));
					return;
				}
				for (String s : mail)
				{
					user.sendMessage(s);
				}
				user.sendMessage(Util.i18n("markMailAsRead"));
				return;
			}
			if (sign.getLine(0).equals("§1[Balance]") && user.isAuthorized("essentials.signs.balance.use"))
			{
				user.sendMessage(Util.format("balance", user.getMoney()));
				return;
			}
			if (sign.getLine(0).equals("§1[Warp]"))
			{
				
				if (!sign.getLine(2).isEmpty())
				{
					if (sign.getLine(2).equals("§2Everyone"))
					{
						chargeUserForWarp(sign, user);
						user.getTeleport().warp(sign.getLine(1), "warpsign");
						return;
					}
					if (user.inGroup(sign.getLine(2)))
					{
						chargeUserForWarp(sign, user);
						user.getTeleport().warp(sign.getLine(1), "warpsign");
						return;
					}
				}
				if (user.isAuthorized("essentials.signs.warp.use")
					&& (!ess.getSettings().getPerWarpPermission() || user.isAuthorized("essentials.warp." + sign.getLine(1))))
				{
					chargeUserForWarp(sign, user);
					user.getTeleport().warp(sign.getLine(1), "warpsign");
				}
				return;
			}
		}
		catch (Throwable ex)
		{
			user.sendMessage(Util.format("errorWithMessage", ex.getMessage()));
		}
	}
	
	private void chargeUserForWarp(Sign sign, User user) throws Exception
	{
		if (!sign.getLine(3).isEmpty())
		{
			String[] l1 = sign.getLine(3).split("[ :-]+");
			boolean m1 = l1[0].matches("\\$[0-9]+");
			int q1 = Integer.parseInt(m1 ? l1[0].substring(1) : l1[0]);
			if (q1 < 1)
			{
				throw new Exception(Util.i18n("moreThanZero"));
			}
			if (m1)
			{
				if (user.getMoney() < q1)
				{
					throw new Exception(Util.i18n("notEnoughMoney"));
				}
				user.takeMoney(q1);
				user.sendMessage(Util.format("moneyTaken", Util.formatCurrency(q1)));
			}
			else
			{
				ItemStack i = ItemDb.get(l1[1], q1);
				if (!InventoryWorkaround.containsItem(user.getInventory(), true, i))
				{
					throw new Exception(Util.format("missingItems", q1, l1[1]));
				}
				InventoryWorkaround.removeItem(user.getInventory(), true, i);
				user.updateInventory();
			}
		}
	}

	@Override
	public void onPlayerEggThrow(PlayerEggThrowEvent event)
	{
		User user = ess.getUser(event.getPlayer());
		ItemStack is = new ItemStack(Material.EGG, 1);
		if (user.hasUnlimited(is))
		{
			user.getInventory().addItem(is);
			user.updateInventory();
		}
	}

	@Override
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		if (user.hasUnlimited(new ItemStack(event.getBucket())))
		{
			event.getItemStack().setType(event.getBucket());
			Essentials.getStatic().getScheduler().scheduleSyncDelayedTask(Essentials.getStatic(),
																		  new Runnable()
			{
				public void run()
				{
					user.updateInventory();
				}
			});
		}
	}

	@Override
	public void onPlayerAnimation(PlayerAnimationEvent event)
	{
		usePowertools(event);
	}

	private void usePowertools(PlayerAnimationEvent event)
	{
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING)
		{
			return;
		}
		User user = ess.getUser(event.getPlayer());
		ItemStack is = user.getItemInHand();
		if (is == null || is.getType() == Material.AIR)
		{
			return;
		}
		String command = user.getPowertool(is);
		if (command == null || command.isEmpty())
		{
			return;
		}
		if (command.matches(".*\\{player\\}.*"))
		{
			//user.sendMessage("Click a player to use this command");
			return;
		}
		if (command.startsWith("c:"))
		{
			for (Player p : server.getOnlinePlayers())
			{
				p.sendMessage(user.getDisplayName() + ":" + command.substring(2));
			}
		}
		else
		{
			user.getServer().dispatchCommand(user, command);
		}
	}
}
