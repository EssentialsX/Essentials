package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 451 ERR_NOTREGISTERED
 * :You have not registered
 * Returned by the server to indicate that the client must be registered before the
 * server will allow it to be parsed in detail.
 */
public class NotRegisteredError extends GenericError
{
    private String errorMessage;

    public NotRegisteredError()
    {
    }

    public NotRegisteredError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "451";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NotRegisteredError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

