package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 446 ERR_USERSDISABLED
 * :USERS has been disabled
 * Returned as a response to the USERS command.  Must be returned by any server which
 * does not implement it.
 */
public class UsersDisabledError extends GenericError
{
    private String errorMessage;

    public UsersDisabledError()
    {
    }

    public UsersDisabledError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "446";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new UsersDisabledError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

