package net.essentialsx.api.v2.services.discord;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class which provides information about what triggered an interaction event.
 */
public class InteractionEvent {
    private final InteractionMember member;
    private final String token;
    private final String channelId;
    private final Map<String, Object> options;
    private final InteractionController controller;
    private final List<String> replyBuffer = new ArrayList<>();

    /**
     * Creates an interaction event.
     * @param member     The member which caused this event.
     * @param token      The authorization token used to reply to this event.
     * @param channelId  The channel id where this event took place.
     * @param options    The options provided from parsing this command.
     * @param controller The {@link InteractionController} which dispatched this event.
     */
    public InteractionEvent(InteractionMember member, String token, String channelId, Map<String, Object> options, InteractionController controller) {
        this.member = member;
        this.token = token;
        this.channelId = channelId;
        this.options = options;
        this.controller = controller;
    }

    /**
     * Creates/Appends the initial response message.
     * @param message The message to append.
     */
    public void reply(String message) {
        replyBuffer.add(message);
        controller.editInteractionResponse(token, Joiner.on('\n').join(replyBuffer));
    }

    /**
     * Gets the member which caused this event.
     * @return the member which caused the event.
     */
    public InteractionMember getMember() {
        return member;
    }

    /**
     * Helper method to get the String representation of the argument by the given key or null if none by that key is present.
     * @param key The key of the argument to lookup.
     * @return the string value or null.
     */
    public String getStringArgument(String key) {
        final Object value = getArgument(key);
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }

    /**
     * Gets the channel id where this interaction occurred.
     * @return the channel id.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Gets the argument with by the specified key if present, otherwise null.
     * @param key The key of the argument to lookup.
     * @return the argument or null.
     */
    public Object getArgument(String key) {
        return options.get(key);
    }
}
