package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.Trade.OverflowType;
import com.earth2me.essentials.Trade.TradeType;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Map;

import static com.earth2me.essentials.I18n.tl;

//TODO: TL exceptions
public class SignTrade extends EssentialsSign {
    private static int MAX_STOCK_LINE_LENGTH = 15;

    public SignTrade() {
        super("Trade");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        validateTrade(sign, 1, false, ess);
        validateTrade(sign, 2, true, ess);
        final Trade trade = getTrade(sign, 2, AmountType.ROUNDED, true, true, ess);
        final Trade charge = getTrade(sign, 1, AmountType.ROUNDED, false, true, ess);
        if (trade.getType() == charge.getType() && (trade.getType() != TradeType.ITEM || trade.getItemStack().isSimilar(charge.getItemStack()))) {
            throw new SignException("You cannot trade for the same item type.");
        }
        trade.isAffordableFor(player);
        sign.setLine(3, "§8" + username);
        trade.charge(player);
        Trade.log("Sign", "Trade", "Create", username, trade, username, null, sign.getBlock().getLocation(), player.getMoney(), ess);
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException, MaxMoneyException {
        if (sign.getLine(3).substring(2).equalsIgnoreCase(username)) {
            final Trade store = rechargeSign(sign, ess, player);
            final Trade stored;
            try {
                stored = getTrade(sign, 1, AmountType.TOTAL, true, true, ess);
                subtractAmount(sign, 1, stored, ess);

                final Map<Integer, ItemStack> withdraw = stored.pay(player, OverflowType.RETURN);

                if (withdraw == null) {
                    Trade.log("Sign", "Trade", "Withdraw", username, store, username, null, sign.getBlock().getLocation(), player.getMoney(), ess);
                } else {
                    setAmount(sign, 1, BigDecimal.valueOf(withdraw.get(0).getAmount()), ess);
                    Trade.log("Sign", "Trade", "Withdraw", username, stored, username, new Trade(withdraw.get(0), ess), sign.getBlock().getLocation(), player.getMoney(), ess);
                }
            } catch (final SignException e) {
                if (store == null) {
                    throw new SignException(tl("tradeSignEmptyOwner"), e);
                }
            }
            Trade.log("Sign", "Trade", "Deposit", username, store, username, null, sign.getBlock().getLocation(), player.getMoney(), ess);
        } else {
            final Trade charge = getTrade(sign, 1, AmountType.COST, false, true, ess);
            final Trade trade = getTrade(sign, 2, AmountType.COST, true, true, ess);
            charge.isAffordableFor(player);
            addAmount(sign, 1, charge, ess);
            subtractAmount(sign, 2, trade, ess);
            if (!trade.pay(player)) {
                subtractAmount(sign, 1, charge, ess);
                addAmount(sign, 2, trade, ess);
                throw new ChargeException("Full inventory");
            }
            charge.charge(player);
            Trade.log("Sign", "Trade", "Interact", sign.getLine(3).substring(2), charge, username, trade, sign.getBlock().getLocation(), player.getMoney(), ess);
        }
        sign.updateSign();
        return true;
    }

    private Trade rechargeSign(final ISign sign, final IEssentials ess, final User player) throws SignException, ChargeException {
        final Trade trade = getTrade(sign, 2, AmountType.COST, false, true, ess);
        if (trade.getItemStack() != null && player.getBase().getItemInHand() != null && trade.getItemStack().getType() == player.getBase().getItemInHand().getType() && trade.getItemStack().getDurability() == player.getBase().getItemInHand().getDurability() && trade.getItemStack().getEnchantments().equals(player.getBase().getItemInHand().getEnchantments())) {
            final int amount = trade.getItemStack().getAmount();
            if (player.getBase().getInventory().containsAtLeast(trade.getItemStack(), amount)) {
                final ItemStack stack = player.getBase().getItemInHand().clone();
                stack.setAmount(amount);
                final Trade store = new Trade(stack, ess);
                addAmount(sign, 2, store, ess);
                store.charge(player);
                return store;
            }
        }
        return null;
    }

    @Override
    protected boolean onSignBreak(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, MaxMoneyException {
        final String signOwner = sign.getLine(3);

        final boolean isOwner = signOwner.length() > 3 && signOwner.substring(2).equalsIgnoreCase(username);
        final boolean canBreak = isOwner || player.isAuthorized("essentials.signs.trade.override");
        final boolean canCollect = isOwner || player.isAuthorized("essentials.signs.trade.override.collect");

        if (canBreak) {
            try {
                final Trade stored1 = getTrade(sign, 1, AmountType.TOTAL, false, true, ess);
                final Trade stored2 = getTrade(sign, 2, AmountType.TOTAL, false, true, ess);

                if (!canCollect) {
                    Trade.log("Sign", "Trade", "Destroy", signOwner.substring(2), stored2, username, stored1, sign.getBlock().getLocation(), player.getMoney(), ess);
                    return true;
                }

                final Map<Integer, ItemStack> withdraw1 = stored1.pay(player, OverflowType.RETURN);
                final Map<Integer, ItemStack> withdraw2 = stored2.pay(player, OverflowType.RETURN);

                if (withdraw1 == null && withdraw2 == null) {
                    Trade.log("Sign", "Trade", "Break", signOwner.substring(2), stored2, username, stored1, sign.getBlock().getLocation(), player.getMoney(), ess);
                    return true;
                }

                setAmount(sign, 1, BigDecimal.valueOf(withdraw1 == null ? 0L : withdraw1.get(0).getAmount()), ess);
                Trade.log("Sign", "Trade", "Withdraw", signOwner.substring(2), stored1, username, withdraw1 == null ? null : new Trade(withdraw1.get(0), ess), sign.getBlock().getLocation(), player.getMoney(), ess);

                setAmount(sign, 2, BigDecimal.valueOf(withdraw2 == null ? 0L : withdraw2.get(0).getAmount()), ess);
                Trade.log("Sign", "Trade", "Withdraw", signOwner.substring(2), stored2, username, withdraw2 == null ? null : new Trade(withdraw2.get(0), ess), sign.getBlock().getLocation(), player.getMoney(), ess);

                sign.updateSign();
            } catch (final SignException e) {
                if (player.isAuthorized("essentials.signs.trade.override")) {
                    return true;
                }
                throw e;
            }
            return false;
        } else {
            return false;
        }
    }

    private void validateSignLength(final String newLine) throws SignException {
        if (newLine.length() > MAX_STOCK_LINE_LENGTH) {
            throw new SignException("This sign is full!");
        }
    }

    protected final void validateTrade(final ISign sign, final int index, final boolean amountNeeded, final IEssentials ess) throws SignException {
        final String line = sign.getLine(index).trim();
        if (line.isEmpty()) {
            throw new SignException("Empty line");
        }
        final String[] split = line.split("[ :]+");

        if (split.length == 1 && !amountNeeded) {
            final BigDecimal money = getMoney(split[0], ess);
            if (money != null) {
                final String newLine = NumberUtil.shortCurrency(money, ess) + ":0";
                validateSignLength(newLine);
                sign.setLine(index, newLine);
                return;
            }
        }

        if (split.length == 2 && amountNeeded) {
            final BigDecimal money = getMoney(split[0], ess);
            BigDecimal amount = getBigDecimalPositive(split[1], ess);
            if (money != null && amount != null) {
                amount = amount.subtract(amount.remainder(money));
                if (amount.compareTo(MINTRANSACTION) < 0 || money.compareTo(MINTRANSACTION) < 0) {
                    throw new SignException(tl("moreThanZero"));
                }
                final String newLine = NumberUtil.shortCurrency(money, ess) + ":" + NumberUtil.shortCurrency(amount, ess).substring(1);
                validateSignLength(newLine);
                sign.setLine(index, newLine);
                return;
            }
        }

        if (split.length == 2 && !amountNeeded) {
            final int amount = getIntegerPositive(split[0]);

            if (amount < 1) {
                throw new SignException(tl("moreThanZero"));
            }
            if (!(split[1].equalsIgnoreCase("exp") || split[1].equalsIgnoreCase("xp")) && getItemStack(split[1], amount, ess).getType() == Material.AIR) {
                throw new SignException(tl("moreThanZero"));
            }
            final String newline = amount + " " + split[1] + ":0";
            validateSignLength(newline);
            sign.setLine(index, newline);
            return;
        }

        if (split.length == 3 && amountNeeded) {
            final int stackamount = getIntegerPositive(split[0]);
            int amount = getIntegerPositive(split[2]);
            amount -= amount % stackamount;
            if (amount < 1 || stackamount < 1) {
                throw new SignException(tl("moreThanZero"));
            }
            if (!(split[1].equalsIgnoreCase("exp") || split[1].equalsIgnoreCase("xp")) && getItemStack(split[1], stackamount, ess).getType() == Material.AIR) {
                throw new SignException(tl("moreThanZero"));
            }
            sign.setLine(index, stackamount + " " + split[1] + ":" + amount);
            return;
        }
        throw new SignException(tl("invalidSignLine", index + 1));
    }

    protected final Trade getTrade(final ISign sign, final int index, final AmountType amountType, final boolean notEmpty, final IEssentials ess) throws SignException {
        return getTrade(sign, index, amountType, notEmpty, false, ess);
    }

    protected final Trade getTrade(final ISign sign, final int index, final AmountType amountType, final boolean notEmpty, final boolean allowId, final IEssentials ess) throws SignException {
        final String line = sign.getLine(index).trim();
        if (line.isEmpty()) {
            throw new SignException("Empty line");
        }
        final String[] split = line.split("[ :]+");

        if (split.length == 2) {
            try {
                final BigDecimal money = getMoney(split[0], ess);
                final BigDecimal amount = notEmpty ? getBigDecimalPositive(split[1], ess) : getBigDecimal(split[1], ess);
                if (money != null && amount != null) {
                    return new Trade(amountType == AmountType.COST ? money : amount, ess);
                }
            } catch (final SignException e) {
                throw new SignException(tl("tradeSignEmpty"), e);
            }
        }

        if (split.length == 3) {
            if (split[1].equalsIgnoreCase("exp") || split[1].equalsIgnoreCase("xp")) {
                final int stackamount = getIntegerPositive(split[0]);
                int amount = getInteger(split[2]);
                if (amountType == AmountType.ROUNDED) {
                    amount -= amount % stackamount;
                }
                if (notEmpty && (amount < 1 || stackamount < 1)) {
                    throw new SignException(tl("tradeSignEmpty"));
                }
                return new Trade(amountType == AmountType.COST ? stackamount : amount, ess);
            } else {
                final int stackamount = getIntegerPositive(split[0]);
                final ItemStack item = getItemStack(split[1], stackamount, allowId, ess);
                int amount = getInteger(split[2]);
                if (amountType == AmountType.ROUNDED) {
                    amount -= amount % stackamount;
                }
                if (notEmpty && (amount < 1 || stackamount < 1 || item.getType() == Material.AIR || amount < stackamount)) {
                    throw new SignException(tl("tradeSignEmpty"));
                }
                item.setAmount(amountType == AmountType.COST ? stackamount : amount);
                return new Trade(item, ess);
            }
        }
        throw new SignException(tl("invalidSignLine", index + 1));
    }

    protected final void subtractAmount(final ISign sign, final int index, final Trade trade, final IEssentials ess) throws SignException {
        final BigDecimal money = trade.getMoney();
        if (money != null) {
            changeAmount(sign, index, money.negate(), ess);
        }
        final ItemStack item = trade.getItemStack();
        if (item != null) {
            changeAmount(sign, index, BigDecimal.valueOf(-item.getAmount()), ess);
        }
        final Integer exp = trade.getExperience();
        if (exp != null) {
            changeAmount(sign, index, BigDecimal.valueOf(-exp), ess);
        }
    }

    protected final void addAmount(final ISign sign, final int index, final Trade trade, final IEssentials ess) throws SignException {
        final BigDecimal money = trade.getMoney();
        if (money != null) {
            changeAmount(sign, index, money, ess);
        }
        final ItemStack item = trade.getItemStack();
        if (item != null) {
            changeAmount(sign, index, BigDecimal.valueOf(item.getAmount()), ess);
        }
        final Integer exp = trade.getExperience();
        if (exp != null) {
            changeAmount(sign, index, BigDecimal.valueOf(exp), ess);
        }
    }

    //TODO: Translate these exceptions.
    private void changeAmount(final ISign sign, final int index, final BigDecimal value, final IEssentials ess) throws SignException {
        final String line = sign.getLine(index).trim();
        if (line.isEmpty()) {
            throw new SignException("Empty line");
        }
        final String[] split = line.split("[ :]+");

        if (split.length == 2) {
            final BigDecimal amount = getBigDecimal(split[1], ess).add(value);
            setAmount(sign, index, amount, ess);
            return;
        }
        if (split.length == 3) {
            final BigDecimal amount = getBigDecimal(split[2], ess).add(value);
            setAmount(sign, index, amount, ess);
            return;
        }
        throw new SignException(tl("invalidSignLine", index + 1));
    }

    //TODO: Translate these exceptions.
    private void setAmount(final ISign sign, final int index, final BigDecimal value, final IEssentials ess) throws SignException {

        final String line = sign.getLine(index).trim();
        if (line.isEmpty()) {
            throw new SignException("Empty line");
        }
        final String[] split = line.split("[ :]+");

        if (split.length == 2) {
            final BigDecimal money = getMoney(split[0], ess);
            final BigDecimal amount = getBigDecimal(split[1], ess);
            if (money != null && amount != null) {
                final String newline = NumberUtil.shortCurrency(money, ess) + ":" + NumberUtil.shortCurrency(value, ess).substring(1);
                validateSignLength(newline);
                sign.setLine(index, newline);
                return;
            }
        }

        if (split.length == 3) {
            if (split[1].equalsIgnoreCase("exp") || split[1].equalsIgnoreCase("xp")) {
                final int stackamount = getIntegerPositive(split[0]);
                final String newline = stackamount + " " + split[1] + ":" + value.intValueExact();
                validateSignLength(newline);
                sign.setLine(index, newline);
                return;
            } else {
                final int stackamount = getIntegerPositive(split[0]);
                getItemStack(split[1], stackamount, ess);
                final String newline = stackamount + " " + split[1] + ":" + value.intValueExact();
                validateSignLength(newline);
                sign.setLine(index, newline);
                return;
            }
        }
        throw new SignException(tl("invalidSignLine", index + 1));
    }

    public enum AmountType {
        TOTAL,
        ROUNDED,
        COST
    }
}
