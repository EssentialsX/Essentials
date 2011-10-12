package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 501 ERR_UMODEUNKNOWNFLAG
 * :Unknown MODE flag
 * Returned by the server to indicate that a MODE message was sent with a nickname
 * parameter and that the a mode flag sent was not recognized.
 */
public class UModeUnknownFlagError extends GenericError
{
    private String errorMessage;

    public UModeUnknownFlagError()
    {
    }

    public UModeUnknownFlagError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "501";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new UModeUnknownFlagError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

