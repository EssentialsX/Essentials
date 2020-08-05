package com.earth2me.essentials.signs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import net.ess3.api.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;

import org.bukkit.inventory.ItemStack;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsShopSign extends EssentialsSign {

  public EssentialsShopSign(String signName) {
    super(signName);
  }

  /**
   * Updates the sign cost to the value of the item specified in worth.yml multiplied by a multiplier factor
   * @param sign
   * @param player
   * @param ess
   * @param multiplier multiply the worth price by this amount
   * 
   * @returns true if the price was changed, false otherwise
   */
  protected boolean updateFromWorth(final ISign sign, final User player, final IEssentials ess, final BigDecimal multiplier) throws SignException {
    final ItemStack stack = getItemStack(getSignText(sign, 2), getIntegerPositive(sign.getLine(1)), ess);
    final int amount = stack.getAmount();
    BigDecimal price = ess.getWorth().getPrice(ess, stack);
    if (price != null && amount > 0) {
        price = price.multiply(multiplier).multiply(new BigDecimal(amount)).setScale(2, RoundingMode.UP);
        final BigDecimal oldPrice = getMoney(getSignText(sign, 3), ess);
        if (oldPrice == null || price.compareTo(oldPrice) != 0) {
            final String priceString = NumberUtil.shortCurrency(price, ess);
            sign.setLine(3, tl("signFormatWorth") + priceString);
            sign.updateSign();
            player.sendMessage(tl("priceChanged", amount, stack.getType().toString().toLowerCase(Locale.ENGLISH), (oldPrice == null ? tl("none") : NumberUtil.shortCurrency(oldPrice, ess)), priceString));
            return true;
        }
    }
    return false;
  }
 
  /**
   * Updates the sign cost to the value of the item specified in worth.yml multiplied by the multiplier factor from the settings
   * @param sign
   * @param player
   * @param ess
   */
  protected boolean updateFromWorthMultiplied(final ISign sign, final User player, final IEssentials ess) throws SignException {
    return updateFromWorth(sign, player, ess, ess.getSettings().getEcoBuyMultiplier());
  }
  
  /**
   * Updates the sign cost to the value of the item specified in worth.yml
   * @param sign
   * @param player
   * @param ess
   */
  protected boolean updateFromWorth(final ISign sign, final User player, final IEssentials ess) throws SignException {
    return updateFromWorth(sign, player, ess, new BigDecimal("1.0"));
  }
  
}