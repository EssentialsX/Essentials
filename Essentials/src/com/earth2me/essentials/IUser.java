package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import net.ess3.api.ITeleport;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * <p>IUser interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IUser {
    /**
     * <p>isAuthorized.</p>
     *
     * @param node a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isAuthorized(String node);

    /**
     * <p>isAuthorized.</p>
     *
     * @param cmd a {@link com.earth2me.essentials.commands.IEssentialsCommand} object.
     * @return a boolean.
     */
    boolean isAuthorized(IEssentialsCommand cmd);

    /**
     * <p>isAuthorized.</p>
     *
     * @param cmd a {@link com.earth2me.essentials.commands.IEssentialsCommand} object.
     * @param permissionPrefix a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isAuthorized(IEssentialsCommand cmd, String permissionPrefix);

    /**
     * <p>isPermissionSet.</p>
     *
     * @param node a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isPermissionSet(String node);

    /**
     * <p>healCooldown.</p>
     *
     * @throws java.lang.Exception if any.
     */
    void healCooldown() throws Exception;

    /**
     * <p>giveMoney.</p>
     *
     * @param value a {@link java.math.BigDecimal} object.
     * @throws net.ess3.api.MaxMoneyException if any.
     */
    void giveMoney(BigDecimal value) throws MaxMoneyException;

    /**
     * <p>giveMoney.</p>
     *
     * @param value a {@link java.math.BigDecimal} object.
     * @param initiator a {@link com.earth2me.essentials.CommandSource} object.
     * @throws net.ess3.api.MaxMoneyException if any.
     */
    void giveMoney(final BigDecimal value, final CommandSource initiator) throws MaxMoneyException;

    /**
     * <p>payUser.</p>
     *
     * @param reciever a {@link com.earth2me.essentials.User} object.
     * @param value a {@link java.math.BigDecimal} object.
     * @throws java.lang.Exception if any.
     */
    void payUser(final User reciever, final BigDecimal value) throws Exception;

    /**
     * <p>takeMoney.</p>
     *
     * @param value a {@link java.math.BigDecimal} object.
     */
    void takeMoney(BigDecimal value);

    /**
     * <p>takeMoney.</p>
     *
     * @param value a {@link java.math.BigDecimal} object.
     * @param initiator a {@link com.earth2me.essentials.CommandSource} object.
     */
    void takeMoney(final BigDecimal value, final CommandSource initiator);

    /**
     * <p>canAfford.</p>
     *
     * @param value a {@link java.math.BigDecimal} object.
     * @return a boolean.
     */
    boolean canAfford(BigDecimal value);

    /**
     * <p>canSpawnItem.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a {@link java.lang.Boolean} object.
     */
    Boolean canSpawnItem(final Material material);

    /**
     * <p>setLastLocation.</p>
     */
    void setLastLocation();

    /**
     * <p>setLogoutLocation.</p>
     */
    void setLogoutLocation();

    /**
     * <p>requestTeleport.</p>
     *
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param here a boolean.
     */
    void requestTeleport(final User player, final boolean here);

    /**
     * Returns whether this user has an outstanding teleport request to deal with.
     *
     * @return whether there is a teleport request
     */
    boolean hasOutstandingTeleportRequest();

    /**
     * <p>getTeleport.</p>
     *
     * @return a {@link net.ess3.api.ITeleport} object.
     */
    ITeleport getTeleport();

    /**
     * <p>getMoney.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    BigDecimal getMoney();

    /**
     * <p>setMoney.</p>
     *
     * @param value a {@link java.math.BigDecimal} object.
     * @throws net.ess3.api.MaxMoneyException if any.
     */
    void setMoney(final BigDecimal value) throws MaxMoneyException;

    /**
     * <p>setAfk.</p>
     *
     * @param set a boolean.
     */
    void setAfk(final boolean set);

    /**
     * 'Hidden' Represents when a player is hidden from others. This status includes when the player is hidden via other
     * supported plugins. Use isVanished() if you want to check if a user is vanished by Essentials.
     *
     * @return If the user is hidden or not
     * @see isVanished
     */
    boolean isHidden();

    /**
     * <p>setHidden.</p>
     *
     * @param vanish a boolean.
     */
    void setHidden(boolean vanish);

    /**
     * <p>isGodModeEnabled.</p>
     *
     * @return a boolean.
     */
    boolean isGodModeEnabled();

    /**
     * <p>getGroup.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getGroup();

    /**
     * <p>inGroup.</p>
     *
     * @param group a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean inGroup(final String group);

    /**
     * <p>canBuild.</p>
     *
     * @return a boolean.
     */
    boolean canBuild();

    /**
     * <p>getTeleportRequestTime.</p>
     *
     * @return a long.
     */
    long getTeleportRequestTime();

    /**
     * <p>enableInvulnerabilityAfterTeleport.</p>
     */
    void enableInvulnerabilityAfterTeleport();

    /**
     * <p>resetInvulnerabilityAfterTeleport.</p>
     */
    void resetInvulnerabilityAfterTeleport();

    /**
     * <p>hasInvulnerabilityAfterTeleport.</p>
     *
     * @return a boolean.
     */
    boolean hasInvulnerabilityAfterTeleport();

    /**
     * 'Vanished' Represents when a player is hidden from others by Essentials. This status does NOT include when the
     * player is hidden via other plugins. Use isHidden() if you want to check if a user is vanished by any supported
     * plugin.
     *
     * @return If the user is vanished or not
     * @see isHidden
     */
    boolean isVanished();

    /**
     * <p>setVanished.</p>
     *
     * @param vanish a boolean.
     */
    void setVanished(boolean vanish);

    /**
     * <p>isIgnoreExempt.</p>
     *
     * @return a boolean.
     */
    boolean isIgnoreExempt();

    /**
     * <p>sendMessage.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    void sendMessage(String message);

    /*
     * UserData
     */
    /**
     * <p>getHome.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.bukkit.Location} object.
     * @throws java.lang.Exception if any.
     */
    Location getHome(String name) throws Exception;

    /**
     * <p>getHome.</p>
     *
     * @param loc a {@link org.bukkit.Location} object.
     * @return a {@link org.bukkit.Location} object.
     * @throws java.lang.Exception if any.
     */
    Location getHome(Location loc) throws Exception;

    /**
     * <p>getHomes.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<String> getHomes();

    /**
     * <p>setHome.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param loc a {@link org.bukkit.Location} object.
     */
    void setHome(String name, Location loc);

    /**
     * <p>delHome.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @throws java.lang.Exception if any.
     */
    void delHome(String name) throws Exception;

    /**
     * <p>hasHome.</p>
     *
     * @return a boolean.
     */
    boolean hasHome();

    /**
     * <p>getLastLocation.</p>
     *
     * @return a {@link org.bukkit.Location} object.
     */
    Location getLastLocation();

    /**
     * <p>getLogoutLocation.</p>
     *
     * @return a {@link org.bukkit.Location} object.
     */
    Location getLogoutLocation();

    /**
     * <p>getLastTeleportTimestamp.</p>
     *
     * @return a long.
     */
    long getLastTeleportTimestamp();

    /**
     * <p>setLastTeleportTimestamp.</p>
     *
     * @param time a long.
     */
    void setLastTeleportTimestamp(long time);

    /**
     * <p>getJail.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getJail();

    /**
     * <p>setJail.</p>
     *
     * @param jail a {@link java.lang.String} object.
     */
    void setJail(String jail);

    /**
     * <p>getMails.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<String> getMails();

    /**
     * <p>addMail.</p>
     *
     * @param mail a {@link java.lang.String} object.
     */
    void addMail(String mail);

    /**
     * <p>isAfk.</p>
     *
     * @return a boolean.
     */
    boolean isAfk();

    /**
     * <p>setIgnoreMsg.</p>
     *
     * @param ignoreMsg a boolean.
     */
    void setIgnoreMsg(boolean ignoreMsg);

    /**
     * <p>isIgnoreMsg.</p>
     *
     * @return a boolean.
     */
    boolean isIgnoreMsg();

    /**
     * <p>setConfigProperty.</p>
     *
     * @param node a {@link java.lang.String} object.
     * @param object a {@link java.lang.Object} object.
     */
    void setConfigProperty(String node, Object object);

    /**
     * <p>getConfigKeys.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    Set<String> getConfigKeys();

    /**
     * <p>getConfigMap.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, Object> getConfigMap();

    /**
     * <p>getConfigMap.</p>
     *
     * @param node a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    Map<String, Object> getConfigMap(String node);
    
    /**
     * <p>getCommandCooldowns.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<Pattern, Long> getCommandCooldowns();

    /**
     * <p>getCommandCooldownExpiry.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @return a {@link java.util.Date} object.
     */
    Date getCommandCooldownExpiry(String label);
    
    /**
     * <p>addCommandCooldown.</p>
     *
     * @param pattern a {@link java.util.regex.Pattern} object.
     * @param expiresAt a {@link java.util.Date} object.
     * @param save a boolean.
     */
    void addCommandCooldown(Pattern pattern, Date expiresAt, boolean save);
    
    /**
     * <p>clearCommandCooldown.</p>
     *
     * @param pattern a {@link java.util.regex.Pattern} object.
     * @return a boolean.
     */
    boolean clearCommandCooldown(Pattern pattern);

    /*
     *  PlayerExtension
     */
    /**
     * <p>getBase.</p>
     *
     * @return a {@link org.bukkit.entity.Player} object.
     */
    Player getBase();

    /**
     * <p>getSource.</p>
     *
     * @return a {@link com.earth2me.essentials.CommandSource} object.
     */
    CommandSource getSource();

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getName();

    /**
     * <p>getAfkMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getAfkMessage();

    /**
     * <p>setAfkMessage.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    void setAfkMessage(final String message);
    
    /**
     * <p>getAfkSince.</p>
     *
     * @return a long.
     */
    long getAfkSince();
    
    /**
     * <p>isAcceptingPay.</p>
     *
     * @return a boolean.
     */
    boolean isAcceptingPay();
    
    /**
     * <p>setAcceptingPay.</p>
     *
     * @param acceptingPay a boolean.
     */
    void setAcceptingPay(boolean acceptingPay);
    
    /**
     * <p>isPromptingPayConfirm.</p>
     *
     * @return a boolean.
     */
    boolean isPromptingPayConfirm();
    
    /**
     * <p>setPromptingPayConfirm.</p>
     *
     * @param prompt a boolean.
     */
    void setPromptingPayConfirm(boolean prompt);
    
    /**
     * <p>isPromptingClearConfirm.</p>
     *
     * @return a boolean.
     */
    boolean isPromptingClearConfirm();
    
    /**
     * <p>setPromptingClearConfirm.</p>
     *
     * @param prompt a boolean.
     */
    void setPromptingClearConfirm(boolean prompt);

    /**
     * <p>isLastMessageReplyRecipient.</p>
     *
     * @return a boolean.
     */
    boolean isLastMessageReplyRecipient();

    /**
     * <p>setLastMessageReplyRecipient.</p>
     *
     * @param enabled a boolean.
     */
    void setLastMessageReplyRecipient(boolean enabled);

    /**
     * <p>getConfirmingPayments.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<User, BigDecimal> getConfirmingPayments();
}
