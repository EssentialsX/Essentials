package f00f.net.irc.martyr.clientstate;

import f00f.net.irc.martyr.util.FullNick;

/**
 * <p>This class allows channels to keep track of individual users.  Each
 * user in the channel has a nick and has voice, ops, both, or none.
 * Note that nicks can change, but the information we have about that
 * person does not.</p>
 *
 * <p>Control over events that happen to this class can be obtained in
 * a similar fashion to how control for the Channel is taken from
 * ClientState.</p>
 */
public class Member
{

    private FullNick nick;
    private boolean hasOpsV = false;
    private boolean hasVoiceV = false;

    /**
     * <p>Strips off the leading 'at' or 'plus', sets ops or voice, and
     * keeps the nick.  Calls the methods <code>setVoice(...)</code> and
     * <code>setOps(...)</code> from the constructor, if those conditions
     * are true.  The nick is set before setVoice or setOps are
     * called.</p>
     *
     * @param nickStr Nick of member
     */
    public Member( String nickStr )
    {
        char first = nickStr.charAt(0);
        String shortNick = nickStr.substring(1, nickStr.length() );
        if( first == '@' )
        {
            nick = new FullNick( shortNick );
            setOps( true );
        }
        else if( first == '+' )
        {
            nick = new FullNick( shortNick );
            setVoice( true );
        }
        else
        {
            nick = new FullNick( nickStr );
        }
    }

    /**
     * Does a nick-wise compare.
     *
     * @param member Member to compare against
     * @return True or false of this member equals the other one
     */
    public boolean equals( Member member )
    {
        return equals( member.nick );
    }

    public boolean equals( FullNick fullNick )
    {
        return nick.equals( fullNick );
    }

    public boolean equals( Object o )
    {
        if( o instanceof Member )
            return equals( (Member)o );
        else if( o instanceof FullNick )
            return equals( (FullNick)o );
        else return false;
    }

    public int hashCode()
    {
        return nick.hashCode();
    }

    public void setOps( boolean ops )
    {
        hasOpsV = ops;
    }

    public void setVoice( boolean voice )
    {
        hasVoiceV = voice;
    }

    public boolean hasOps()
    {
        return hasOpsV;
    }

    public boolean hasVoice()
    {
        return hasVoiceV;
    }

    public void setNick( FullNick nick )
    {
        this.nick = nick;
    }

    public FullNick getNick()
    {
        return nick;
    }

}



