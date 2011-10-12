package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 462 ERR_ALREADYREGISTERED
 * :You may not reregister
 * Returned by the server to any link which tries to change part of the registered details (such as
 * password or user details from second USER message).
 */
public class AlreadyRegisteredError extends GenericError
{
    private String errorMessage;

    public AlreadyRegisteredError()
    {
    }

    public AlreadyRegisteredError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "462";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new AlreadyRegisteredError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

