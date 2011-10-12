package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 442 ERR_NOTONCHANNEL
 * &lt;channel&gt; :You're not on that channel
 * Returned by the server whenever a client tries to perform a channel effecting command for which the
 * client isn't a member.
 */
public class NotOnChannelError extends GenericError
{
    private String channel;
    private String errorMessage;

    public NotOnChannelError()
    {
    }

    public NotOnChannelError(String channel, String errorMessage)
    {
        this.channel = channel;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "442";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NotOnChannelError(getParameter(params, 1), getParameter(params, 2));
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

