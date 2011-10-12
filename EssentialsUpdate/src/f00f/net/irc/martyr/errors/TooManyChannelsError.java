package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 405 ERR_TOOMANYCHANNELS
 * &lt;channel name&gt; :You have joined too many channels
 * Sent to a user when they have joined the maximum number of allowed channels and they try to join another channel.
 */
public class TooManyChannelsError extends GenericError
{
    private String channel;
    private String errorMessage;

    public TooManyChannelsError()
    {
    }

    public TooManyChannelsError(String channel, String errorMessage)
    {
        this.channel = channel;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "405";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new TooManyChannelsError(getParameter(params, 1), getParameter(params, 2));
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

