/*
 * Original version: Ben Damm <bdamm@dammfine.com>
 * Changes by: Mog
 * 	- added getOldNick
 * 	*/
package f00f.net.irc.martyr.commands;

import java.util.Enumeration;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.clientstate.Channel;
import f00f.net.irc.martyr.clientstate.ClientState;
import f00f.net.irc.martyr.clientstate.Member;
import f00f.net.irc.martyr.util.FullNick;

/**
 * Defines NICK command.
 */
public class NickCommand extends AbstractCommand
{

    private FullNick oldNick;
    private FullNick newNick;

    /** For use as a factory */
    public NickCommand()
    {
        this( null, null );
    }

    public NickCommand( FullNick oldNick, FullNick newNick )
    {
        this.oldNick = oldNick;
        this.newNick = newNick;
    }

    public NickCommand( String newNick )
    {
        this( null, new FullNick( newNick ) );
    }

    public InCommand parse( String prefix, String identifier, String params )
    {
        return new NickCommand( new FullNick( prefix ), new FullNick ( getParameter( params, 0 ) ) );
    }

    public String getIrcIdentifier()
    {
        return "NICK";
    }

    public String renderParams()
    {
        return getNick();
    }

    public String getNick()
    {
        return newNick.getNick();
    }

    public String getOldNick()
    {
        return oldNick.getNick();
    }

    public boolean updateClientState( ClientState state )
    {
        // Does this apply to us?
        if( oldNick.equals( state.getNick() ) )
        {
            state.setNick( newNick );
            return true;
        }
        else
        {
            // Ok, so we need to change someone's nick.
            // This needs to occur for each member with that nick in each
            // channel that we are in.  Just use Member.setNick for each
            // occurance.
            // Note: I do not believe this code has received a vigorous
            // test.
            Enumeration channels = state.getChannels();
            while( channels.hasMoreElements() )
            {
                Channel channel = (Channel)channels.nextElement();
                Member member = channel.findMember( oldNick.getNick() );
                if( member != null )
                    member.setNick( newNick );
            }
        }
        return false;
    }

}


