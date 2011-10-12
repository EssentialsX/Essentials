package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 467 ERR_KEYSEY
 * &lt;channel&gt; :Channel key already set
 */
public class KeySetError extends GenericError
{
    private String channel;
    private String errorMessage;

    public KeySetError()
    {
    }

    public KeySetError(String channel, String errorMessage)
    {
        this.channel = channel;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "467";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new KeySetError(getParameter(params, 1), getParameter(params, 2));
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

