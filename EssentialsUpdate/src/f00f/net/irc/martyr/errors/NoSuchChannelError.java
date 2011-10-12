package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 403 ERR_NOSUCHCHANNEL
 * &lt;channel name&gt; :No such channel
 * Used to indicate the given channel name is invalid.
 */
public class NoSuchChannelError extends GenericError
{
    private String channel;
    private String errorMessage;

    public NoSuchChannelError()
    {
    }

    public NoSuchChannelError(String channel, String errorMessage)
    {
        this.channel = channel;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "403";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoSuchChannelError(getParameter(params, 1), getParameter(params, 2));
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

