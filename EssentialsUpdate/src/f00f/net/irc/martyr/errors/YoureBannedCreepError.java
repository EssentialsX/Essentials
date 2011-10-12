package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;

/**
 * Code: 465 ERR_YOUREBANNEDCREEP
 * :You are banned from this server
 * Returned after an attempt to connect and register yourself with a server which has been setup to
 * explicitly deny connections to you.
 */
public class YoureBannedCreepError extends GenericError
{
    private String errorMessage;

    public YoureBannedCreepError()
    {
    }

    public YoureBannedCreepError(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "465";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new YoureBannedCreepError(getParameter(params, 1));
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

