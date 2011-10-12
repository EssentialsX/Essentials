package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 409 ERR_NOORIGIN
 * :No origin specified
 * PING or PONG message missing the originator parameter which is required since these commands must
 * work without valid prefixes.
 */
public class NoOriginError extends GenericError
{
    private String errorMessage;

    public NoOriginError()
    {
    }

    public NoOriginError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "409";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoOriginError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

