package net.ess3.api.sync;

import com.earth2me.essentials.UserData;

/*
 * ISyncProviders are called when EssentialsX data is modified.
 * They may propagate changes to other servers through their own mechanism.
 */
public interface ISyncProvider {

    /*
     * Add a mail message to the user's mailbox.
     */
    public void addMail(UserData user, String message);

    /*
     * Clear the user's mailbox.
     */
    public void clearMail(UserData user);

    /*
     * Set the user's nickname.
     */
    public void setNickname(UserData user, String nick);

}