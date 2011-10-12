package f00f.net.irc.martyr.commands;

import java.util.StringTokenizer;

import f00f.net.irc.martyr.util.FullNick;

/**
 * This facilitates the sending and receiving of CTCP messages.  Upon
 * receiving a message, MessageCommand checks to see if it is a CTCP,
 * and if it is, it instantiates this class instead of a
 * MessageCommand.  You can then use the getAction() and getMessage()
 * methods to retreive the action and payload, respectively.
 *
 * @see MessageCommand
 */
public class CtcpMessage extends MessageCommand
{
	private String actionStr;
	
	/**
	 * Use this to send a CTCP message.  This simply wraps the string
	 * with the CTCP tags, \001.
     *
     * @param dest Target of CTCP message
     * @param message Actual CTCP message
	 */
	public CtcpMessage( String dest, String message )
	{
		super( dest, "\001" + message + "\001" );
	}

	public CtcpMessage( String dest, String action, String message )
	{
		this( dest, action + " " + message );
	}
	
	/**
	 * This is only to be called by MessageCommand, as a way of
	 * receiving a Ctcp message.  It strips the \001's off and holds
	 * the message left over.
     *
     * @param from Nick that sent the message
     * @param dest Target of the CTCP message
     * @param message Actual CTCP message
	 */
	protected CtcpMessage( FullNick from, String dest, String message )
	{
		super( from, dest, getMessageStr( stripCtcpWrapper( message ) ) );

		actionStr = getActionStr( stripCtcpWrapper( message ) );
	}
	
	/**
	 * Returns the action of this CTCP.  Use getMessage() to retreive
	 * the payload of the action.
     *
     * @return The action specified by the CTCP message
	 */
	public String getAction()
	{
		return actionStr;
	}
	
	/**
	 * Given a stripped CTCP message, returns the ctcp action string.
     *
     * @param msg Message to be parsed into an action
     * @return Action string from message
	 */
	public static String getActionStr( String msg )
	{
		StringTokenizer tokens = new StringTokenizer( msg );
		return tokens.nextToken();
	}

	public static String getMessageStr( String msg )
	{
		String acn = getActionStr( msg );
		return msg.substring( acn.length() ).trim();
	}
	
	/**
	 * If the string is wrapped with CTCP signal chars (\001) returns
	 * true.
     *
     * @param msg String to check whether it's a CTCP message or not
     * @return True or false if it's a CTCP message
	 */
	public static boolean isCtcpString( String msg )
	{
		return msg.charAt(0) == '\001' && msg.charAt(msg.length()-1) == '\001';
	}
	
	/**
	 * Strips a CTCP wrapper, if there is one.
     *
     * @param msg String to be stripped
     * @return Stripped string
	 */
	public static String stripCtcpWrapper( String msg )
	{
		if( isCtcpString( msg ) )
		{
			return msg.substring( 1, msg.length()-1 );
		}
		else
		{
			return msg;
		}
	}
	
	/**
	 * Dysfunctional.  Returns dat immediatly.
	 */
	/*public static byte[] escapeMsg( byte[] dat )
	{
		return dat;
	}*/

	/**
	 * Dysfunctional.  Returns dat immediatly.
	 */
	/*public static byte[] unEscapeMsg( byte[] dat )
	{
		return dat;
	}*/
}


