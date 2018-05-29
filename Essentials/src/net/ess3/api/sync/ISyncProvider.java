package net.ess3.api.sync;

import com.earth2me.essentials.UserData;

/*
 * ISyncProviders are called when EssentialsX data is modified.
 * They may propagate changes to other servers through their own mechanism.
 */
public interface ISyncProvider {

    /**
     * Add a mail message to the user's mailbox.
     *
     * @param user The UserData of the user
     * @param message The message sent to the user
     */
    public void addMail(UserData user, String message);

    /**
     * Clear the user's mailbox.
     *
     * @param user The UserData of the user
     */
    public void clearMail(UserData user);

    /**
     * Set the user's nickname.
     *
     * @param user The UserData of the user
     * @param nick The user's new nickname
     */
    public void setNickname(UserData user, String nick);

    /**
     * Set the user's teleport toggle
     *
     * @param user The UserData of the user
     * @param state The user's teleport toggle state
     */
    public void setTeleport(UserData user, boolean state);

    /**
     * Set the user's mute status.
     *
     * @param user The UserData of the user
     * @param state The user's mute status
     */
    public void setMuted(UserData user, boolean state);

    /**
     * Set the user's mute timeout.
     *
     * @param user The UserData of the user
     * @param time The user's mute timeout
     */
    public void setMuteTimeout(UserData user, long time);
}