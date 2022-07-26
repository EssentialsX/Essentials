package net.essentialsx.api.v2.services.mail;

import net.ess3.api.IUser;

/**
 * This interface provides access to core Essentials mailing functions, allowing API users to send messages to {@link IUser IUser's }.
 */
public interface MailService {
    /**
     * Sends a message from the specified {@link MailSender sender} to the specified {@link IUser recipient}.
     * @param recipient The {@link IUser} which to send the message to.
     * @param sender    The {@link MailSender} which sent the message.
     * @param message   The message content.
     */
    void sendMail(IUser recipient, MailSender sender, String message);

    /**
     * Sends a message from the specified {@link MailSender sender} to the specified {@link IUser recipient}.
     * @param recipient The {@link IUser} which to send the message to.
     * @param sender    The {@link MailSender} which sent the message.
     * @param message   The message content.
     * @param expireAt  The millisecond epoch at which this message expires, or 0 if the message doesn't expire.
     */
    void sendMail(IUser recipient, MailSender sender, String message, long expireAt);

    /**
     * Sends a legacy message to the user without any advanced features.
     * @param recipient The {@link IUser} which to send the message to.
     * @param message   The message content.
     * @see #sendMail(IUser, MailSender, String)
     * @see #sendMail(IUser, MailSender, String, long)
     * @deprecated This is only for maintaining backwards compatibility with old API, please use the newer {@link #sendMail(IUser, MailSender, String)} API.
     */
    @Deprecated
    void sendLegacyMail(IUser recipient, String message);

    /**
     * Generates the message sent to the recipient of the given {@link MailMessage}.
     * @param message The {@link MailMessage} to generate the message for.
     * @return The formatted message to be sent to the recipient.
     */
    String getMailLine(MailMessage message);

    /**
     * Helper method to get the translation key for a given {@link MailMessage}.
     * @return the translation key.
     */
    String getMailTlKey(MailMessage message);

    /**
     * Helper method to get the translation arguments for a given {@link MailMessage}.
     * @return the translation arguments.
     */
    Object[] getMailTlArgs(MailMessage message);
}
