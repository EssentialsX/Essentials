package com.earth2me.essentials;

import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;


public class Worth implements IConf {
    private final EssentialsConf config;

    public Worth(File dataFolder) {
        config = new EssentialsConf(new File(dataFolder, "worth.yml"));
        config.setTemplateName("/worth.yml");
        config.load();
    }

    public BigDecimal getPrice(ItemStack itemStack) {
        String itemname = itemStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
        BigDecimal result;

        //First check for matches with item name
        result = config.getBigDecimal("worth." + itemname + "." + itemStack.getDurability(), BigDecimal.ONE.negate());
        if (result.signum() < 0) {
            final ConfigurationSection itemNameMatch = config.getConfigurationSection("worth." + itemname);
            if (itemNameMatch != null && itemNameMatch.getKeys(false).size() == 1) {
                result = config.getBigDecimal("worth." + itemname + ".0", BigDecimal.ONE.negate());
            }
        }
        if (result.signum() < 0) {
            result = config.getBigDecimal("worth." + itemname + ".*", BigDecimal.ONE.negate());
        }
        if (result.signum() < 0) {
            result = config.getBigDecimal("worth." + itemname, BigDecimal.ONE.negate());
        }

        //Now we should check for item ID
        if (result.signum() < 0) {
            result = config.getBigDecimal("worth." + itemStack.getTypeId() + "." + itemStack.getDurability(), BigDecimal.ONE.negate());
        }
        if (result.signum() < 0) {
            final ConfigurationSection itemNumberMatch = config.getConfigurationSection("worth." + itemStack.getTypeId());
            if (itemNumberMatch != null && itemNumberMatch.getKeys(false).size() == 1) {
                result = config.getBigDecimal("worth." + itemStack.getTypeId() + ".0", BigDecimal.ONE.negate());
            }
        }
        if (result.signum() < 0) {
            result = config.getBigDecimal("worth." + itemStack.getTypeId() + ".*", BigDecimal.ONE.negate());
        }
        if (result.signum() < 0) {
            result = config.getBigDecimal("worth." + itemStack.getTypeId(), BigDecimal.ONE.negate());
        }

        //This is to match the old worth syntax
        if (result.signum() < 0) {
            result = config.getBigDecimal("worth-" + itemStack.getTypeId(), BigDecimal.ONE.negate());
        }
        if (result.signum() < 0) {
            return null;
        }
        return result;
    }

    public int getAmount(IEssentials ess, User user, ItemStack is, String[] args, boolean isBulkSell) throws Exception {
        if (is == null || is.getType() == Material.AIR) {
            throw new Exception(tl("itemSellAir"));
        }
        int id = is.getTypeId();
        int amount = 0;

        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
            } catch (NumberFormatException ex) {
                throw new NotEnoughArgumentsException(ex);
            }
            if (args[1].startsWith("-")) {
                amount = -amount;
            }
        }

        boolean stack = args.length > 1 && args[1].endsWith("s");
        boolean requireStack = ess.getSettings().isTradeInStacks(id);

        if (requireStack && !stack) {
            throw new Exception(tl("itemMustBeStacked"));
        }

        int max = 0;
        for (ItemStack s : user.getBase().getInventory().getContents()) {
            if (s == null || !s.isSimilar(is)) {
                continue;
            }
            max += s.getAmount();
        }

        if (stack) {
            amount *= is.getType().getMaxStackSize();
        }
        if (amount < 1) {
            amount += max;
        }

        if (requireStack) {
            amount -= amount % is.getType().getMaxStackSize();
        }
        if (amount > max || amount < 1) {
            if (!isBulkSell) {
                user.sendMessage(tl("itemNotEnough2"));
                user.sendMessage(tl("itemNotEnough3"));
                throw new Exception(tl("itemNotEnough1"));
            } else {
                return amount;
            }
        }

        return amount;
    }

    public void setPrice(ItemStack itemStack, double price) {
        if (itemStack.getType().getData() == null) {
            config.setProperty("worth." + itemStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", ""), price);
        } else {
            // Bukkit-bug: getDurability still contains the correct value, while getData().getData() is 0.
            config.setProperty("worth." + itemStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "") + "." + itemStack.getDurability(), price);
        }
        config.removeProperty("worth-" + itemStack.getTypeId());
        config.save();
    }

    @Override
    public void reloadConfig() {
        config.load();
    }
}
