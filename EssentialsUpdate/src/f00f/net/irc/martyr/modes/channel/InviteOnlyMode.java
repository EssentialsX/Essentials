package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>Invite Only Flag - When the channel flag 'i' is set, new
 *    members are only accepted if their mask matches Invite-list (See
 *    section 4.3.2) or they have been invited by a channel operator.
 *    This flag also restricts the usage of the INVITE command (See
 *    "IRC Client Protocol" [IRC-CLIENT]) to channel operators.</p>
 * (From RFC2811)
 *
 * @see InviteMaskMode
 */
public class InviteOnlyMode extends GenericChannelMode
{
	public boolean requiresParam()
	{
		return false;
	}
	
	public char getChar()
	{
		return 'i';
	}
	
	public Mode newInstance()
	{
		return new InviteOnlyMode();
	}
}

