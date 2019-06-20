package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;


/**
 * <p>IEssentialsXMPP interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IEssentialsXMPP extends Plugin {
    /**
     * <p>getAddress.</p>
     *
     * @param user a {@link org.bukkit.entity.Player} object.
     * @return a {@link java.lang.String} object.
     */
    String getAddress(final Player user);

    /**
     * <p>getAddress.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    String getAddress(final String name);

    /**
     * <p>getSpyUsers.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<String> getSpyUsers();

    /**
     * <p>getUserByAddress.</p>
     *
     * @param address a {@link java.lang.String} object.
     * @return a {@link net.ess3.api.IUser} object.
     */
    IUser getUserByAddress(final String address);

    /**
     * <p>sendMessage.</p>
     *
     * @param user a {@link org.bukkit.entity.Player} object.
     * @param message a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean sendMessage(final Player user, final String message);

    /**
     * <p>sendMessage.</p>
     *
     * @param address a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean sendMessage(final String address, final String message);

    /**
     * <p>setAddress.</p>
     *
     * @param user a {@link org.bukkit.entity.Player} object.
     * @param address a {@link java.lang.String} object.
     */
    void setAddress(final Player user, final String address);

    /**
     * <p>toggleSpy.</p>
     *
     * @param user a {@link org.bukkit.entity.Player} object.
     * @return a boolean.
     */
    boolean toggleSpy(final Player user);

    /**
     * <p>broadcastMessage.</p>
     *
     * @param sender a {@link net.ess3.api.IUser} object.
     * @param message a {@link java.lang.String} object.
     * @param xmppAddress a {@link java.lang.String} object.
     */
    void broadcastMessage(final IUser sender, final String message, final String xmppAddress);

    /**
     * <p>getEss.</p>
     *
     * @return a {@link com.earth2me.essentials.IEssentials} object.
     */
    IEssentials getEss();
}
