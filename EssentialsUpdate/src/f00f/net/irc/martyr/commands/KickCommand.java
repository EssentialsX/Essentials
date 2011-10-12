package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.clientstate.Channel;
import f00f.net.irc.martyr.clientstate.ClientState;
import f00f.net.irc.martyr.util.FullNick;
import java.util.logging.Logger;


/**
 * Defines KICK command.
 */
public class KickCommand extends AbstractCommand
{

    static Logger log = Logger.getLogger(KickCommand.class.getName());

    private String channel;
    private FullNick userKicker;
    private FullNick userKicked;
    private String comment;

    /** For use as a factory */
    public KickCommand()
    {
        this( null, null, null, null );
    }

    public KickCommand( FullNick userKicker, String channel,
        String userKicked, String comment )
    {
        this.userKicker = userKicker;
        this.channel = channel;
        this.userKicked = new FullNick( userKicked );
        this.comment = comment;
    }

    public KickCommand( String channel, String userToKick, String comment )
    {
        this( null, channel, userToKick, comment );
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new KickCommand(
            new FullNick( prefix ),
            getParameter( params, 0 ),
            getParameter( params, 1 ),
            getParameter( params, 2 )
        );
    }

    public String getIrcIdentifier()
    {
        return "KICK";
    }

    public String renderParams()
    {
        return channel + " " + userKicked + " :" + comment;
    }

    public String getChannel()
    {
        return channel;
    }

    public FullNick getKicker()
    {
        return userKicker;
    }

    public FullNick getKicked()
    {
        return userKicked;
    }

    public String getComment()
    {
        return comment;
    }

    public boolean kickedUs( ClientState state )
    {
        return userKicked.equals( state.getNick() );
    }

    public boolean updateClientState( ClientState state )
    {
        if( kickedUs( state ) )
        {
            // We've been kicked.
            //log.debug("KICK: We've been kicked " + channel);
            state.removeChannel( channel );
            return true;
        }
        else
        {
            // Someone else was kicked.
            //log.debug("KICK: " + userKicked.getNick() + " kicked " + channel);
            // 1) Grab group
            Channel channelObj = state.getChannel( channel );
            channelObj.removeMember( userKicked, this );
            return true;
        }
    }

}


