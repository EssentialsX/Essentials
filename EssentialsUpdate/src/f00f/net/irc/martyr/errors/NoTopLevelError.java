package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 413 ERR_NOTOPLEVEL
 * &lt;mask&gt; :No toplevel domain specified
 * 412 - 414 are returned by PRIVMSG to indicate that the message wasn't delivered for some reason.
 * ERR_NOTOPLEVEL and ERR_WILDTOPLEVEL are errors that are returned when an invalid use of
 * "PRIVMSG $&lt;server&gt;" or "PRIVMSG #&lt;host&gt;" is attempted.
 */
public class NoTopLevelError extends GenericError
{
    private String mask;
    private String errorMessage;

    public NoTopLevelError()
    {
    }

    public NoTopLevelError(String mask, String errorMessage)
    {
        this.mask = mask;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "413";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoTopLevelError(getParameter(params, 1), getParameter(params, 2));
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

