package com.earth2me.essentials.signs;

//This enum is used when checking to see what signs are enabled
/**
 * <p>Signs class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public enum Signs {
    BALANCE(new SignBalance()),
    BUY(new SignBuy()),
    DISPOSAL(new SignDisposal()),
    ENCHANT(new SignEnchant()),
    FREE(new SignFree()),
    GAMEMODE(new SignGameMode()),
    HEAL(new SignHeal()),
    INFO(new SignInfo()),
    KIT(new SignKit()),
    MAIL(new SignMail()),
    PROTECTION(new SignProtection()),
    REPAIR(new SignRepair()),
    SELL(new SignSell()),
    SPAWNMOB(new SignSpawnmob()),
    TIME(new SignTime()),
    TRADE(new SignTrade()),
    WARP(new SignWarp()),
    WEATHER(new SignWeather());
    private final EssentialsSign sign;

    Signs(final EssentialsSign sign) {
        this.sign = sign;
    }

    /**
     * <p>Getter for the field <code>sign</code>.</p>
     *
     * @return a {@link com.earth2me.essentials.signs.EssentialsSign} object.
     */
    public EssentialsSign getSign() {
        return sign;
    }
}
