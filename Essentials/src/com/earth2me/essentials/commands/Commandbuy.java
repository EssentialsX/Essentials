package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import static com.earth2me.essentials.I18n.tl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class Commandbuy extends EssentialsCommand {
    public Commandbuy() {
        super("buy");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        int amountToGive;
        
        if (args[1] != null && NumberUtil.isInt(args[1])) {
        	amountToGive = NumberUtils.toInt(args[1]);
        } else if (args[1] == null) {
        	amountToGive = 1;
        } else {
        	throw new Exception("The second argument must be an integer.");
        }
        
        if (amountToGive <= 0) {
        	throw new Exception("You cannot buy 0 items.");
        }
        
        ItemStack is = ess.getItemDb().get(args[0]);
        is.setAmount(amountToGive);
        BigDecimal worthSingleItem = ess.getWorth().getBuyPrice(ess, is);
        BigDecimal worth = worthSingleItem.multiply(BigDecimal.valueOf(amountToGive));
        
        if (worth == null) {
            throw new Exception(tl("itemCannotBeSold"));
        }
        
        if (worth.compareTo(user.getMoney()) == -1) {
        	boolean isDropItemsIfFull = ess.getSettings().isDropItemsIfFull();
        	BigDecimal leftoverValue = BigDecimal.ZERO;
        	Map<Integer, ItemStack> leftovers;

        	if (user.isAuthorized("essentials.oversizedstacks")) {
                leftovers = InventoryWorkaround.addOversizedItems(user.getBase().getInventory(), ess.getSettings().getOversizedStackSize(), is);
            } else {
                leftovers = InventoryWorkaround.addItems(user.getBase().getInventory(), is);
            }

        	if (isDropItemsIfFull) {
        		for (ItemStack item : leftovers.values()) {
        			World w = user.getWorld();
                    w.dropItemNaturally(user.getLocation(), item);
                }
            } else if(!leftovers.values().isEmpty()) {
            	for (ItemStack item : leftovers.values()) {
            		leftoverValue = leftoverValue.add(worthSingleItem.multiply(BigDecimal.valueOf(item.getAmount())));
                }
            	
            	user.sendMessage("Not enough inventory space. Refunding $<amount>.");
            }

        	user.takeMoney(worth.subtract(leftoverValue));
        	user.getBase().updateInventory();
        } else {
        	throw new Exception("You do not have enough money.");
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getItems();
        } else if (args.length == 2) {
            return Lists.newArrayList("1", "64");
        } else {
            return Collections.emptyList();
        }
    }
}
