package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 464 ERR_PASSWDMISMATCH
 * :Password incorrect
 * Returned to indicate a failed attempt at registering a connection for which a
 * password was required and was either not given or incorrect.
 */
public class PasswdMismatchError extends GenericError
{
    private String errorMessage;

    public PasswdMismatchError()
    {
    }

    public PasswdMismatchError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "464";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new PasswdMismatchError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

