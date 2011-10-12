package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>The channel flag 'a' defines an anonymous channel.  This means that
 *    when a message sent to the channel is sent by the server to users,
 *    and the origin is a user, then it MUST be masked.  To mask the
 *    message, the origin is changed to "anonymous!anonymous@anonymous."
 *    (e.g., a user with the nickname "anonymous", the username "anonymous"
 *    and from a host called "anonymous.").  Because of this, servers MUST
 *    forbid users from using the nickname "anonymous".  Servers MUST also
 *    NOT send QUIT messages for users leaving such channels to the other
 *    channel members but generate a PART message instead.</p>
 * 
 *    <p>On channels with the character '&amp;' as prefix, this flag MAY be
 *    toggled by channel operators, but on channels with the character '!'
 *    as prefix, this flag can be set (but SHALL NOT be unset) by the
 *    "channel creator" only.  This flag MUST NOT be made available on
 *    other types of channels.</p>
 * 
 *    <p>Replies to the WHOIS, WHO and NAMES commands MUST NOT reveal the
 *    presence of other users on channels for which the anonymous flag is
 *    set.</p>
 * (From RFC2811)
 */
public class AnonChannelMode extends GenericChannelMode
{
	public boolean requiresParam()
	{
		return false;
	}
	
	public char getChar()
	{
		return 'a';
	}
	
	public Mode newInstance()
	{
		return new AnonChannelMode();
	}
}

