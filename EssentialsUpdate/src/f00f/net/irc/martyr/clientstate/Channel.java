package f00f.net.irc.martyr.clientstate;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import f00f.net.irc.martyr.Command;
import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.Mode;
import f00f.net.irc.martyr.modes.channel.OperMode;
import f00f.net.irc.martyr.modes.channel.VoiceMode;
import f00f.net.irc.martyr.util.FullNick;
//import org.apache.log4j.Logger;

/**
 * Channel is simply a repository for information about a channel.
 * Contains channel name, topic, who created the topic, when the topic
 * was created, who is in the channel, mode, etc.
 *
 * <p>If a user of the framework wishes to use their own Member object
 * (to trap events like setting/removing ops), then subclass
 * Channel and add a method as follows.</p>
 *
 * <pre>
 * public Member makeMember( String member )
 * {
 *     return MyMemberSubClass( member ) );
 * }
 * </pre>
 *
 * <p>Each of the methods in Channel that need to create a Member
 * object (many are just temporary members, for the enhanced 'equals')
 * calls makeMember instead of new Member(...).  That is why this
 * version of addMember is protected, so that a part of the framework
 * won't do something silly like:</p>
 *
 * <pre>
 *      ...
 *      channel.addMember( new Member( member ) );
 *      ...
 * </pre>
 */
public class Channel
{

    //static Logger log = Logger.getLogger(Channel.class);

    private String name = null;
	private String topic = null;
	private String topicAuthor = null;
	private Date topicDate = null;
	private Date creationDate = null;

	private List<Mode> modes = null;
	
	/** 
	 * Hopefully we can replace this with a more useful data structure.
	 * This is a vector of Member objects.
	 */
	private Vector<Member> members;
	
	public Channel( String chanName )
	{
		this.name = chanName;
		members = new Vector<Member>();
		modes = new LinkedList<Mode>();
	}
	
	public String getName()
	{
		return name;
	}
	
	protected void addMember( Member member )
	{
		members.addElement( member );
	}
		
	/**
	 * Adds the member to the channel.
	 *
     * @param member Member to add to the channel
     * @param why Command that caused the member to be added
	 * @deprecated Use <code>addMember( String, InCommand )</code>
	 * instead.
	 */
	public void addMember( String member, Command why )
	{
		addMember( makeMember( member ) );
	}

	/**
	 * Adds the member to the channel.
     *
     * @param member Nick to add to the channel
     * @param why Command that caused the member to be added
	 */
	public void addMember( String member, InCommand why )
	{
		addMember( makeMember( member ) );
	}

	
	/**
     * @param nick Nick to add to the channel
     * @param why Command that caused the member to be added
	 * @deprecated Use <code>addMember( FullNick, InCommand )</code> intead.
	 */
	public void addMember( FullNick nick, Command why )
	{
		addMember( makeMember( nick.getNick() ));
	}
	
	/**
     * @param nick Nick to add to the channel
     * @param why Command that caused the member to be added
	 * Adds the member to the channel. Just calls nick.getNick().
	 */
	public void addMember( FullNick nick, InCommand why )
	{
		addMember( nick.getNick(), why );
	}
	
	/**
	 * Removes the user from the channel. Ignores leading @ or + symbols.
	 * This is the cononical version of removeMember.
	 * @param member Nick of the person leaving.
	 * @param why Command issed that caused this action.
	 */
	public void removeMember( String member, InCommand why )
	{
		removeMember( makeMember( member ) );
	}
	
	/**
     * @param member Nick to remove from channel
     * @param why Command that caused removal
	 * @deprecated Use <code>removeMember( String, InCommand ) instead.</code>
	 * */
	public void removeMember( String member, Command why )
	{
		removeMember( makeMember( member ) );
	}

	/**
     * @param member Member to remove from channel
     * @param why Command that caused removal
	 * @deprecated Use <code>removeMember( FullNick, InCommand ) instead.</code>
	 * */
	public void removeMember( FullNick member, Command why )
	{
		removeMember( member, (InCommand)why );
	}

	/**
	 * Simply a wrapper to allow FullNicks to be used.  Calls the string
	 * version of removeMember with nick.getNick().
     *
     * @param nick Nick to remove from channel
     * @param why Command that caused removal
	 */
	public void removeMember( FullNick nick, InCommand why )
	{
		removeMember( nick.getNick(), why );
	}
	
	protected void removeMember( Member compareTo )
	{
		for( int i = 0; i < members.size(); ++i )
		{
			if( (members.elementAt( i )).equals( compareTo ) )
			{
				members.removeElementAt( i );
				return;
			}
		}
	}
	
	/**
	 * Informs the channel of a mode change. A mode change might be for the
	 * channel (such as l, n, or t) or for a user on this channel (such as
	 * o).
     *
     * @param mode Mode to set on the channel
	 */
	public void setMode( Mode mode )
	{
		// Note that Modes are supposed to be equal if the character is
		// equal.  Thus, we can remove a mode from the set, even though it
		// is different because its sign or parameters may be different.
		if( mode.onePerChannel() && modes.contains( mode ) )
		{
			modes.remove( mode );
		}
		
		if( (mode.getSign() != Mode.Sign.NEGATIVE) && mode.recordInChannel() )
		{
			modes.add( mode );
		}
		
		if( mode instanceof OperMode )
		{
			OperMode oMode = (OperMode)mode;
			Member member = findMember( oMode.getParam() );

			member.setOps( oMode.getSign() == Mode.Sign.POSITIVE );
		}
		else if( mode instanceof VoiceMode )
		{
			VoiceMode vMode = (VoiceMode)mode;
			Member member = findMember( vMode.getParam() );
			
			member.setVoice( vMode.getSign() == Mode.Sign.POSITIVE );
		}
	}
	
	public Iterator getModes()
	{
		return modes.iterator();
	}
	
	/**
	 * Returns an enumeration of Member objects, in no particular order.
     *
     * @return List of members in the channel
	 */
	public Enumeration getMembers()
	{
		return members.elements();
	}
	
	/**
	 * Determines if the nick is in the channel.  Nick can be in the form
	 * "@sork" or "+sork" or just "sork", for example.
     *
     * @param nick Nick of member to check
     * @return True or false if the member is in this channel.
	 */
	public boolean isMemberInChannel( String nick )
	{
		return isMemberInChannel( makeMember( nick ) );
	}
	
	/**
	 * Determines if the member is in this channel.
     *
     * @param member Member to check
     * @return True or false if the member is in this channel.
	 */
	protected boolean isMemberInChannel( Member member )
	{	
		return findMember( member ) != null;
	}
	
	/**
	 * Finds the Member object associated with a specific nick.  Ignores
	 * prefixed + or @.
     *
     * @param nick Nick to check whether it's a member of the channel or not
     * @return Member object for specified nick, if it exists (null if not)
	 */
	public Member findMember( String nick )
	{
		return findMember( makeMember( nick ) );
	}
	
	protected Member findMember( Member member )
	{
		Enumeration membersE = getMembers();
		while( membersE.hasMoreElements() )
		{
			Member memberCompare = (Member)membersE.nextElement();
	
			if( memberCompare.equals( member ) )
			{
				return memberCompare;
			}
		}
		
		return null;
	}
	
	public void setTopic( String topic )
	{
		//log.debug(getName()+": Topic: " + topic);
		this.topic = topic;
	}
	
	public String getTopic()
	{
		return topic;
	}
	
	public Date getTopicDate()
	{
		return topicDate;
	}
	
	public void setTopicDate( Date date )
	{
		//log.debug(getName()+": Topic date: " + date);
		this.topicDate = date;
	}
	
	public Date getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate( Date date )
	{
		//log.debug(getName()+": Creation date: " + date);
		this.creationDate = date;
	}
	
	public String getTopicAuthor()
	{
		return topicAuthor;
	}
	
	public void setTopicAuthor( String author )
	{
		//log.debug(getName()+": Topic by: " + author);
		this.topicAuthor = author;
	}
	
	/**
	 * To use a customized Member class, override this.
     *
     * @param nick Nickname to create a member object for
     * @return Member object for nick
	 */
	protected Member makeMember( String nick )
	{
		return new Member( nick );
	}
	
	
	/**
	 * Determines if the string represents a channel name or not.
     *
     * @param str String to test if it's a channel or not
     * @return True or false if a string looks like a channel
	 */
	public static boolean isChannel( String str )
	{
		return str.charAt(0) == '#' || str.charAt(0) == '!' || str.charAt(0) == '&';
	}

	/**
	 * Compares the two channel names for equality.  Returns false if
	 * either are null.
     *
     * @param one Left side of comparison
     * @param two Right side of comparison
     * @return True or false whether two channels are equal, false of either are null/
	 */
	public static boolean areEqual( String one, String two )
	{
		if( one == null || two == null )
		{
			return false;
		}
		
		return one.equalsIgnoreCase( two );
	}
}



