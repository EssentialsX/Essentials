package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 412 ERR_NOTEXTTOSEND
 * :No text to send
 * 412 - 414 are returned by PRIVMSG to indicate that the message wasn't delivered for some reason.
 * ERR_NOTOPLEVEL and ERR_WILDTOPLEVEL are errors that are returned when an invalid use of
 * "PRIVMSG $&lt;server&gt;" or "PRIVMSG #&lt;host&gt;" is attempted.
 */
public class NoTextToSendError extends GenericError
{
    private String errorMessage;

    public NoTextToSendError()
    {
    }

    public NoTextToSendError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "412";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoTextToSendError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

