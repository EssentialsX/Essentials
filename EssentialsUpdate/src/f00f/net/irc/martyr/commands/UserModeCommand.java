package f00f.net.irc.martyr.commands;

import java.util.HashMap;
import java.util.StringTokenizer;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.Mode;
import f00f.net.irc.martyr.clientstate.ClientState;
import f00f.net.irc.martyr.modes.user.InvisibleMode;
import f00f.net.irc.martyr.util.FullNick;
import java.util.logging.Logger;

/**
 * Defines a user MODE command.
 */
public class UserModeCommand extends ModeCommand
{
    static Logger log = Logger.getLogger(ModeCommand.class.getName());

    private FullNick user;
	private FullNick sender;
	//private List modes;

	private static HashMap<Character,Mode> modeTypes;
	
	public UserModeCommand( String prefix, String userStr, StringTokenizer tokens )
	{
//		System.out.println( prefix );
		sender = new FullNick( prefix );
		user = new FullNick( userStr );
	
		if( !sender.equals( user ) ) 
		{
			log.severe("UserModeCommand: Odd: mode change for a user that isn't us.");
			return;
		}
		
		makeModeTypes();

		//modes = parseModes( modeTypes, tokens );

//		System.out.println( modes );
	}
	
	private void makeModeTypes()
	{
		if( modeTypes == null )
		{
			modeTypes = new HashMap<Character,Mode>();
			
			// Add new mode types here
			registerMode( modeTypes, new InvisibleMode() );
		}
	}
	
	
	/**
	 * Should not be called, as ModeCommand does the parsing and instantiation
	 * of this class.
	 */
	public InCommand parse( String prefix, String identifier, String params )
	{
		throw new IllegalStateException( "Don't call this method!" );
	}
	
	public String render()
	{
		throw new UnsupportedOperationException("Can't send user modes, yet." );
	}
	
	public FullNick getUser()
	{
		return user;
	}

    public FullNick getSender() {
        return sender;
    }

    {
		//log.debug("TODO: UserModeCommand: Can't send");
		//log.debug("TODO: UserModeCommand: Does not update client state");
	}

    public boolean updateClientState( ClientState state )
	{
		// TODO implement
		return false;
	}
	
	public String toString()
	{
		return "UserModeCommand";
	}
	

}


