package f00f.net.irc.martyr.util;

import java.util.NoSuchElementException;

public class CtcpUtil
{
	public static final char CTCP_TAG_DELIM = '\001';
	
	/**
	 * Returns a new string ready for sending via MessageCommand.
     *
     * @param action Action string to create
     * @return Action string ready for sending
	 */
	public static String makeActionString( String action )
	{
		return makeCtcpString( "ACTION " + action );
	}
	
	public static String makeCtcpString( String s )
	{
		return "" + CTCP_TAG_DELIM + s + CTCP_TAG_DELIM;
	}
	
	/**
	 * Parses the string into tokens, where each token is either a
	 * CTCP escaped sequence or not.
	 */
	public static class CtcpTokenizer
	{
		private String str;

		public CtcpTokenizer( String in )
		{
			this.str = in;
		}

		public boolean isNextACtcp()
		{
			return str.charAt(0) == CTCP_TAG_DELIM;
		}
		
		public boolean hasNext()
		{
			return !str.equals("");
		}
		
		public String next()
		{
			return nextToken();
		}
		public String nextToken()
		{
			if( !hasNext() )
			{
				throw new NoSuchElementException();
			}
			
			int pos = str.indexOf( CTCP_TAG_DELIM, 1 );
			String result;
			if( isNextACtcp() )
			{
				if( pos < 0 )
				{
					// Error?  Well, whatever, return the rest of the
					// string.
					result = str.substring( 1 );
					str = "";
					return result;
				}
				else
				{
					// ^Aour string^A(rest of string)
					// Lose both ^A
					result = str.substring( 1, pos );
					str = str.substring( pos + 1 );
					return result;
				}
			}
			else 
			{
				// Not a CTCP
				if( pos < 0 )
				{
					result = str;
					str = "";
					return result;
				}
				else
				{
					result = str.substring( 0, pos );
					str = str.substring( pos );
					return result;
				}
			}
		}
	}
}

