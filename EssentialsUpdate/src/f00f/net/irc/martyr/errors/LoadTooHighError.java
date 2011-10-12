package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.util.FullNick;

public class LoadTooHighError extends GenericError
{
    private FullNick nick;
    private String command;
    private String errorMessage;

    public LoadTooHighError()
    {
    }
    
    public LoadTooHighError(FullNick nick, String command, String errorMessage)
    {
        this.nick = nick;
        this.command = command;
        this.errorMessage = errorMessage;
    }

    public String getIrcIdentifier()
    {
        return "263";
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new LoadTooHighError(new FullNick(getParameter(params, 1)), getParameter(params, 2), getParameter(params, 3));
    }

    public FullNick getNick()
    {
        return nick;
    }

    public String getCommand()
    {
        return command;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

