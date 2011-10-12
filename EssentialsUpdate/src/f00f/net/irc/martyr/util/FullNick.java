package f00f.net.irc.martyr.util;

import java.util.StringTokenizer;

/**
 * Parses out a full nick (ex: sork&lt;exclaimation&gt;sork&lt;at&gt;f00f.net) and stores it for
 * use.  It also provides a consistent hashing method.
 */
public class FullNick
{

    private String nick;
    private String user;
    private String remotehost;

    private String original;

    public FullNick( String original )
    {
        this.original = original;
        parse( original );
    }


    /**
     * It can't deal with parameters that have no '!'.  When given a parameter with
     * no '!', it simply places the entire string into the 'nick' field.  FullNick
     * is intended to be immutable.
     *
     * TODO: Should this enforce proper nick syntax?
     * @param original Original nick we will parse
     */
    private void parse( String original )
    {
        if( original == null )
            return;

        StringTokenizer tokens = new StringTokenizer( original, "!", false );

        nick = tokens.nextToken();

        if( tokens.hasMoreTokens() )
        {
            user = tokens.nextToken("@");
            if( user.charAt(0) == '!' )
                user = user.substring(1);
        }

        if( tokens.hasMoreTokens() )
        {
            remotehost = tokens.nextToken("");
            if( remotehost.charAt(0) == '@' )
                remotehost = remotehost.substring(1);
        }
    }

    public String getNick()
    {
        return nick;
    }

    public String getUser()
    {
        return user;
    }

    public String getHost()
    {
        return remotehost;
    }

    public String getSource()
    {
        //return nick+"!"+user+"@"+remotehost;
        return original;
    }


    public int hashCode()
    {
        if( nick == null )
            return 0;

        return nick.hashCode();
    }

    /**
     * Performs case insesitive equals on the nicks only.  Does not strip
     * off any leading @ or +.  ({ == [ and ] == } and | == \)  It appears
     * that servers are not RFC complient on this, so we will not as well.
     *
     * @param nick Nick to compare this nick with
     * @return True or false of nick is the same
     */

    public boolean equals( String nick )
    {
        if( nick == null )
            return false;

        return nick.equalsIgnoreCase( this.nick );
    }

    public boolean equals( FullNick nick )
    {
        if( nick == null )
            return false;
        return equals( nick.getNick() );
    }

    public boolean equals( Object object )
    {
        if( object instanceof FullNick )
            return equals( (FullNick)object );
        return false;
    }

    /**
     * @return the nick part
     * */
    public String toString()
    {
        return nick;
    }

    /**
     * Unit test.
     *
     * @param args Args passed to program
     */
    public static void main( String args[] )
    {

        FullNick nick = new FullNick( args[0] );

        System.out.println( nick.getNick() );
        System.out.println( nick.getUser() );
        System.out.println( nick.getHost() );
        System.out.println( nick.getSource() );

        if( args.length > 1 )
        {

            FullNick nick2 = new FullNick( args[1] );

            System.out.println( "" );
            System.out.println( nick2.getNick() );
            System.out.println( nick2.getUser() );
            System.out.println( nick2.getHost() );
            System.out.println( nick2.getSource() );

            System.out.println( nick2.equals( nick ) ? "Equal." : "Not equal." );

        }
    }

}


