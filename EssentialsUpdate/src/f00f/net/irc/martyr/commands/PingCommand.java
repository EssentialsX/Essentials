package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;


/**
 * Defines the PING command.  At this point, PINGs only come in from
 * the server, so all we need to do is capture the parameters.
 */
public class PingCommand extends AbstractCommand
{

    private String pingSource;

    /** Factory */
    public PingCommand()
    {
        pingSource = null;
    }

    public PingCommand( String source )
    {
        pingSource = source;
    }

    public State getState()
    {
        return State.UNKNOWN;
    }

    /**
     * Parses a string and produces a formed command object, if it can.
     * Should return null if it cannot form the command object.
     */
    public InCommand parse( String prefix, String identifier, String params )
    {
        String str = getParameter( params, 0 );
        return new PingCommand( str );
    }

    /**
     * Returns the string IRC uses to identify this command.  Examples:
     * NICK, PING, KILL, 332
     */
    public String getIrcIdentifier()
    {
        return "PING";
    }

    /**
     * Renders the parameters of this command.
     */
    public String renderParams()
    {
        return ":" + pingSource;
    }

    // ===== Ping-specific methods =======================================
    public String getPingSource()
    {
        return pingSource;
    }

}


