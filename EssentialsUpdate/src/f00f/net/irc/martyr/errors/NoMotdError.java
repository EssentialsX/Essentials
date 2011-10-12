package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 422 ERR_NOMOTD
 * :MOTD File is missing
 * Server's MOTD file could not be opened by the server.
 */
public class NoMotdError extends GenericError
{
    private String errorMessage;

    public NoMotdError()
    {
    }

    public NoMotdError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "422";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoMotdError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

