package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 407 ERR_TOOMANYTARGETS
 * &lt;target&gt; :Duplicate recipients.  No message delivered
 * Returned to a client which is attempting to send a PRIVMSG/NOTICE using the user@host destination
 * format and for a user@host which has several occurrences.
 */
public class TooManyTargetsError extends GenericError
{
    private String dest;
    private String errorMessage;

    public TooManyTargetsError()
    {
    }

    public TooManyTargetsError(String dest, String errorMessage)
    {
        this.dest = dest;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "407";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new TooManyTargetsError(getParameter(params, 1), getParameter(params, 2));
    }

    public String getDest()
    {
        return dest;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

