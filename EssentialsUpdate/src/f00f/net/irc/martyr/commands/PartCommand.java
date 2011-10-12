package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.clientstate.Channel;
import f00f.net.irc.martyr.clientstate.ClientState;
import f00f.net.irc.martyr.util.FullNick;

/**
 * <p>Defines PART command.  If the part command is from us, we should
 * remove that channel from the list of channels.  If the part command
 * is from someone else, we should remove that user from the list of
 * users for that channel.
 */

public class PartCommand extends AbstractCommand
{

    private String reason;
    private String channel;
    private FullNick user;

    /** For use as a factory */
    public PartCommand()
    {
        this( null, null, null );
    }

    /**
     * For use as an incoming command.
     *
     * @param user User that is parting
     * @param channel Channel that the user is parting from
     * @param reason Reason for part
     */
    public PartCommand( FullNick user, String channel, String reason )
    {
        this.user = user;
        this.reason = reason;
        this.channel = channel;
    }

    /**
     * For use as an outgoing command.
     *
     * @param channel Channel that we are parting from
     * @param reason Reason we are parting
     */
    public PartCommand( String channel, String reason )
    {
        this( null, channel, reason );
    }

    /**
     * For use as an outgoing command. Part with no reason.
     *
     * @param channel Channel that we are parting from
     */
    public PartCommand( String channel )
    {
        this( null, channel, null );
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new PartCommand( new FullNick( prefix ), getParameter( params, 0 ), getParameter( params, 1 ) );
    }

    public String getIrcIdentifier()
    {
        return "PART";
    }

    public String renderParams()
    {
        if( reason != null )
            return channel + " :" + reason;
        else
            return channel;
    }

    public String getReason()
    {
        return reason;
    }

    public String getChannel()
    {
        return channel;
    }

    public FullNick getUser()
    {
        return user;
    }

    /** Takes client state action.  If we are parting, then remove that
     * channel from our list of channels.  If someone else is parting,
     * remove them from the channel they are parting from.
     */
    public boolean updateClientState( ClientState state )
    {
        // We parted
        if( user.equals( state.getNick() ) )
        {
            state.removeChannel( channel );
            return true;
        }
        else
        {
            // Someone else parted.

            // 1) Grab channel
            Channel chanObj = state.getChannel( channel );

            // 2) Remove user
            chanObj.removeMember( user, this );
            return true;
        }

    }

}




