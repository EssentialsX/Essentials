package net.ess3.api.sync;

import com.earth2me.essentials.User;

/*
 * ISyncProviders are called when EssentialsX data is modified.
 * They may propagate changes to other servers through their own mechanism.
 */
public interface ISyncProvider {

    /*
     * Add a mail message to the user's mailbox.
     */
    public void addMail(User user, String message);

    /*
     * Clear the user's mailbox.
     */
    public void clearMail(User user, String message);
    
    /*
     * Set the user's nickname.
     */
    public void setNickname(User user, String nick);

}