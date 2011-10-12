package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 502 ERR_USERSDONTMATCH
 * :Cant change mode for other users
 * Error sent to any user trying to view or change the user mode for a user other than themselves.
 */
public class UsersDontMatchError extends GenericError
{
    private String errorMessage;

    public UsersDontMatchError()
    {
    }

    public UsersDontMatchError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "502";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new UsersDontMatchError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

