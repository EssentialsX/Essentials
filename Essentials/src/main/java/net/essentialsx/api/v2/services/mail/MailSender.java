package net.essentialsx.api.v2.services.mail;

import java.util.UUID;

/**
 * An entity which is allowed to send mail to an {@link net.ess3.api.IUser IUser}.
 *
 * In Essentials, IUser and Console are the entities that implement this interface.
 */
public interface MailSender {
    /**
     * Gets the username of this {@link MailSender}.
     * @return The sender's username.
     */
    String getName();

    /**
     * Gets the {@link UUID} of this {@link MailSender} or null if this sender doesn't have a UUID.
     * @return The sender's {@link UUID} or null if N/A.
     */
    UUID getUUID();
}
