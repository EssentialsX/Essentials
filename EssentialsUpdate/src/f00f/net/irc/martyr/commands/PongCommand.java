package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.InCommand;


/**
 * Defines the PONG command.  At this point, PONGs can only be sent to
 * the server, so all we need to do is provide render().
 */
public class PongCommand extends PingCommand
{

    public PongCommand( String dest )
    {
        super( dest );
    }

    /**
     * PONG shouldn't be sent to us.
     */
    public InCommand parse( String prefix, String identifier, String params )
    {
        throw new UnsupportedOperationException("PONG is not an incommand.");
    }

    public String getIrcIdentifier()
    {
        return "PONG";
    }

    // ===== Pong-specific methods =======================================
    public String getPongDest()
    {
        return getPingSource();
    }

}


