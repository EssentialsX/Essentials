package f00f.net.irc.martyr.commands;

/**
 * ActionCtcp allows the application to do a '/me'.
 */
public class ActionCtcp extends CtcpMessage
{
	public ActionCtcp( String dest, String message )
	{
		super( dest, "ACTION " + message );
	}
}
