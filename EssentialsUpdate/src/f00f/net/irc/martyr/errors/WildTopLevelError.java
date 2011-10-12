package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 414 ERR_WILDTOPLEVEL
 * &lt;mask&gt; :Wildcard in toplevel domain
 * 412 - 414 are returned by PRIVMSG to indicate that the message wasn't delivered for some reason.
 * ERR_NOTOPLEVEL and ERR_WILDTOPLEVEL are errors that are returned when an invalid use of
 * "PRIVMSG $&lt;server&gt;" or "PRIVMSG #&lt;host&gt;" is attempted.
 */
public class WildTopLevelError extends GenericError
{
    private String mask;
    private String errorMessage;

    public WildTopLevelError()
    {
    }

    public WildTopLevelError(String mask, String errorMessage)
    {
        this.mask = mask;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "414";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new WildTopLevelError(getParameter(params, 1), getParameter(params, 2));
    }

    public String getMask()
    {
        return mask;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

