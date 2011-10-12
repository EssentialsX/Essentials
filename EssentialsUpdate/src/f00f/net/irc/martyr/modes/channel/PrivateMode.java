package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>Private and Secret Channels - The channel flag 'p' is used to
 *    mark a channel "private" and the channel flag 's' to mark a
 *    channel "secret".  Both properties are similar and conceal the
 *    existence of the channel from other users.</p>
 * 
 *    <p>This means that there is no way of getting this channel's name from
 *    the server without being a member.  In other words, these channels
 *    MUST be omitted from replies to queries like the WHOIS
 *    command.</p>
 * 
 *    <p>When a channel is "secret", in addition to the restriction above, the
 *    server will act as if the channel does not exist for queries like the
 *    TOPIC, LIST, NAMES commands.  Note that there is one exception to
 *    this rule: servers will correctly reply to the MODE command.
 *    Finally, secret channels are not accounted for in the reply to the
 *    LUSERS command (See "Internet Relay Chat: Client Protocol" [IRC-
 *    CLIENT]) when the &lt;mask&gt; parameter is specified.</p>
 * 
 *    <p>The channel flags 'p' and 's' MUST NOT both be set at the same time.
 *    If a MODE message originating from a server sets the flag 'p' and the
 *    flag 's' is already set for the channel, the change is silently
 *    ignored.  This should only happen during a split healing phase
 *    (mentioned in the "IRC Server Protocol" document
 *    [IRC-SERVER]).</p>
 * 
 * (From RFC2811)
 */
public class PrivateMode extends GenericChannelMode
{
	public boolean requiresParam()
	{
		return false;
	}
	
	public char getChar()
	{
		return 'p';
	}
	
	public Mode newInstance()
	{
		return new PrivateMode();
	}
}

