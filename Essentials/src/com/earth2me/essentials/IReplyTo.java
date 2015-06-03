package com.earth2me.essentials;

public interface IReplyTo {
    /**
     * Sets the user to reply to
     *
     * @param user
     */
    void setReplyTo(CommandSource user);

    /**
     * Gets the user the sender should reply to
     *
     * @return
     */
    CommandSource getReplyTo();
}
