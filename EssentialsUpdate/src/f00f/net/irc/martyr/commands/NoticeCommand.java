package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;
import f00f.net.irc.martyr.clientstate.ClientState;
import f00f.net.irc.martyr.util.FullNick;


/**
 * Defines the NOTICE command.
 */
public class NoticeCommand extends AbstractCommand
{

    private FullNick from;
    private String dest;
    private String notice;

    /** Factory */
    public NoticeCommand()
    {
        from = null;
        dest = null;
        notice = null;
    }

    public NoticeCommand( String notice )
    {
        this.notice = notice;
    }

    public NoticeCommand( String dest, String notice )
    {
        this(null, dest, notice);
    }

    public NoticeCommand( FullNick dest, String notice )
    {
        this(dest.getNick(), notice);
    }

    public NoticeCommand( FullNick source, String dest, String notice ) {
        this.from = source;
        this.dest = dest;
        this.notice = notice;
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
        FullNick from;
        if( prefix == null || prefix.trim().length() == 0 )
        {
            from = null;
        }
        else
        {
            from = new FullNick( prefix );
        }
        String dest = getParameter( params, 0 );
        String msg = getParameter( params, 1 );

        if( CtcpNotice.isCtcpString( msg ) )
        {
            return new CtcpNotice( from, dest, msg );
        }

        return new NoticeCommand( from, dest, msg );
    }

    /**
     * Returns the string IRC uses to identify this command.  Examples:
     * NICK, PING, KILL, 332
     */
    public String getIrcIdentifier()
    {
        return "NOTICE";
    }

    /**
     * Renders the parameters of this command.
     */
    public String renderParams()
    {
        if (dest != null) {
            return dest + " :" + notice;
        }
        else {
            return ":" + notice;
        }
    }

    public FullNick getFrom()
    {
        return from;
    }

    public String getDest()
    {
        return dest;
    }

    public String getNotice()
    {
        return notice;
    }

    /**
     * Returns true if the message is both private and for us.
     *
     * @param state Client state to compare with
     * @return True or false if this is a private message to us
     */
    public boolean isPrivateToUs( ClientState state )
    {
        return state.getNick().equals( dest );
    }

}


