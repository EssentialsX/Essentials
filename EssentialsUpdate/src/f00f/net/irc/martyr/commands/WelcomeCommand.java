package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.CommandRegister;
import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.clientstate.ClientState;
import f00f.net.irc.martyr.util.FullNick;
import f00f.net.irc.martyr.util.ParameterIterator;
import java.util.logging.Logger;


/**
 * Defines the commands that a server issues to welcome us.  These are
 * identified with 001, 002... etc.  These commands are only received
 * after we register, unlike the NOTICE command.
 */
public class WelcomeCommand extends AbstractInCommand
{
    static Logger log = Logger.getLogger(WelcomeCommand.class.getName());

    private String notice;
    private String nick;

    /** Factory */
    public WelcomeCommand()
    {
        this( null, null );
    }

    /**
     * Used by parse to create an instance of WelcomeCommand.
     *
     * @param nick Nick that send the welcome
     * @param notice Notice that was sent
     * */
    public WelcomeCommand( String nick, String notice )
    {
        this.notice = notice;
        this.nick = nick;
        //log.debug("WelcomeCommand: Nick is: `" + nick + "'");
        //log.debug("WelcomeCommand: Notice is: `"+notice+"'");
    }

    /**
     * Parses a string and produces a formed command object, if it can.
     * Should return null if it cannot form the command object.
     */
    public InCommand parse( String prefix, String identifier, String params )
    {
        ParameterIterator pi = new ParameterIterator( params );
        String nick = pi.next().toString();
        String notice;
        if( pi.hasNext() )
        {
            // We are looking at a "nick :msg" pair
            notice = pi.next().toString();
        }
        else
        {
            // There is only one parameter, a notice.
            notice = nick;
            nick = null;
        }
        if( pi.hasNext() )
        {
            //log.severe("WelcomeCommand: More than two parameters, confused.");
        }


        //String str = getParameter( params, 0 );
        //
        return new WelcomeCommand( nick, notice );
    }

    /**
     * Sets the nick of the client state, if there is one included with
     * this command.
     */
    public boolean updateClientState( ClientState state )
    {
        //log.debug("WelcomeCommand: updated client state with: " + new FullNick( nick ));
        state.setNick( new FullNick( nick ) );

        return true;
    }

    /**
     * Returns the string IRC uses to identify this command.  Examples:
     * NICK, PING, KILL, 332.  In our case, there is no one thing.
     */
    public String getIrcIdentifier()
    {
        return "001";
    }

    public void selfRegister( CommandRegister commandRegister )
    {
        commandRegister.addCommand( "001", this );
        commandRegister.addCommand( "002", this );
        commandRegister.addCommand( "003", this );
        commandRegister.addCommand( "004", this );
        commandRegister.addCommand( "005", this );
    }

    public String getNotice()
    {
        return notice;
    }

    /**
     * @return the nick received with this command, or null if there isn't
     * one.
     * */
    public String getNick()
    {
        return nick;
    }

    public String toString()
    {
        return "WelcomeCommand";
    }

}


