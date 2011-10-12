package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 404 ERR_CANNOTSENDTOCHAN
 * &lt;channel name&gt; :Cannot send to channel
 * Sent to a user who is either (a) not on a channel which is mode +n or (b) not a chanop (or mode +v)
 * on a channel which has mode +m set and is trying to send a PRIVMSG message to that channel.
 */
public class CannotSendToChanError extends GenericError
{
    private String channel;
    private String errorMessage;

    public CannotSendToChanError()
    {
    }

    public CannotSendToChanError(String channel, String errorMessage)
    {
        this.channel = channel;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "404";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new CannotSendToChanError(getParameter(params, 1), getParameter(params, 2));
    }

    public String getChannel()
    {
        return channel;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

