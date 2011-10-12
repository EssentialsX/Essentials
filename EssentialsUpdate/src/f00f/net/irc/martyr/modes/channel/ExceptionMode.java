package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>Channel Ban and Exception - When a user requests to join a
 *    channel, his local server checks if the user's address matches
 *    any of the ban masks set for the channel.  If a match is found,
 *    the user request is denied unless the address also matches an
 *    exception mask set for the channel.</p>
 * 
 *    <p>Servers MUST NOT allow a channel member who is banned from the
 *    channel to speak on the channel, unless this member is a channel
 *    operator or has voice privilege. (See Section 4.1.3 (Voice
 *    Privilege)).</p>
 * 
 *    <p>A user who is banned from a channel and who carries an invitation
 *    sent by a channel operator is allowed to join the channel.</p>
 * (From RFC2811)
*/
public class ExceptionMode extends GenericChannelMask
{
	public boolean requiresParam()
	{
		return true;
	}
	
	public char getChar()
	{
		return 'e';
	}
	
	public Mode newInstance()
	{
		return new ExceptionMode();
	}
}

