package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 481 ERR_NOPRIVILEGES
 * :Permission Denied- You're not an IRC operator
 * Any command requiring operator privileges to operate must return this error to
 * indicate the attempt was unsuccessful.
 */
public class NoPrivilegesError extends GenericError
{
    private String errorMessage;

    public NoPrivilegesError()
    {
    }

    public NoPrivilegesError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "481";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NoPrivilegesError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

