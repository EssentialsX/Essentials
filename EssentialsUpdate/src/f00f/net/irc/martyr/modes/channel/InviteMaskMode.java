package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>Channel Invitation - For channels which have the invite-only
 *    flag set (See Section 4.2.2 (Invite Only Flag)), users whose
 *    address matches an invitation mask set for the channel are
 *    allowed to join the channel without any
 *    invitation.</p>
 * (From RFC2811)
 */
public class InviteMaskMode extends GenericChannelMask
{
	public boolean requiresParam()
	{
		return true;
	}
	
	public char getChar()
	{
		return 'I';
	}
	
	public Mode newInstance()
	{
		return new InviteMaskMode();
	}
}

