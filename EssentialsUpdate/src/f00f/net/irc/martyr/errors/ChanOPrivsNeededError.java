package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 482 ERR_CHANOPRIVSNEEDED
 * &lt;channel&gt; :You're not channel operator
 * Any command requiring 'chanop' privileges (such as MODE messages) must return
 * this error if the client making the attempt is not a chanop on the specified
 * channel.
 */
public class ChanOPrivsNeededError extends GenericError
{
    private String channel;
    private String errorMessage;

    public ChanOPrivsNeededError()
    {
    }

    public ChanOPrivsNeededError(String channel, String errorMessage)
    {
        this.channel = channel;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "482";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new ChanOPrivsNeededError(getParameter(params, 1), getParameter(params, 2));
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

