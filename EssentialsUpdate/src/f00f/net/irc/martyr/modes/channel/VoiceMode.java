package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>Voice Privilege - The mode 'v' is used to give and take voice
 *    privilege to/from a channel member.  Users with this privilege
 *    can talk on moderated channels.  (See section 4.2.3 (Moderated
 *    Channel Flag).</p>
 * (From RFC2811)
 */
public class VoiceMode extends GenericChannelMode
{
	public boolean requiresParam()
	{
		return true;
	}
	
	public char getChar()
	{
		return 'v';
	}
	
	public boolean recordInChannel()
	{
		return false;
	}
	
	public Mode newInstance()
	{
		return new VoiceMode();
	}
}

