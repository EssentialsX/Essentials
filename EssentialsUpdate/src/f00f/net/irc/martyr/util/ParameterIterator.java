/*
 * Original author: Ben Damm <bdamm@dammfine.com>
 * Changes by: Mog
 * 	- Fixed bug with substring handling
 * 	*/
package f00f.net.irc.martyr.util;


import java.util.Iterator;
import java.util.NoSuchElementException;

//TODO: Unit test

/**
 * This class iterates over the parameter string of an IRC command,
 * returning each parameter in order as next() is called.  This class
 * also knows about the ":" parameters, which is the large string at
 * the end of most commands, and treats it specially.
 */
public class ParameterIterator implements Iterator
{
    //static Logger log = Logger.getLogger(ParameterIterator.class);

    private String paramStr;
	private int position;
	private String last = null;
	
	public ParameterIterator( String paramStr )
	{
		//log.debug("ParameterIterator: Params: `" + paramStr + "'");
		// We don't check for null here because hasNext is the place
		// to do it, according to the definition for Iterator.
		// next() should throw an exception.
		if( paramStr != null )
		{
			this.paramStr = paramStr.trim();
			position = 0;
		}
		else
		{
			this.paramStr = null;
			position = -1;
		}
	}
	
	/**
	 * @return true if there are more parameters, and false
	 * otherwise.
	 */
	public boolean hasNext()
	{
		if( paramStr == null )
			return false;
		
		return position < paramStr.length();
	}

	/**
	 * @throws NoSuchElementException if there are no more params
	 * @return true if the next parameter is also the ":" parameter.
	 * */
	public boolean nextIsLast()
	{
		if( ! hasNext() )
		{
			throw new NoSuchElementException("No more parameters.");
		}
		return paramStr.charAt(position) == ':';
	}

	/**
	 * @throws NoSuchElementException if there are no more params
	 * */
	public Object next()
	{
		if( ! hasNext() )
		{
			throw new NoSuchElementException("No more parameters.");
		}
		
		// If : is the first char, the rest of the string is a
		// parameter.
		if( paramStr.charAt(position) == ':' )
		{
			String result = paramStr.substring(position + 1);
			position = paramStr.length();
			last = result;
			return result;
		}
		
		int spaceIndex = paramStr.indexOf( ' ', position );
		// We can't have a space after the last parameter, it gets
		// trimmed in the constructor.  Also, we can't have only
		// spaces, so we don't need to check for -1.  Finally, we are
		// guaranteed to have a space before the colon, so we don't
		// have to do any checking at all!
		
		String result = paramStr.substring( position, spaceIndex );
		position = spaceIndex + 1;
		return result;
	}

	/**
	 * Forwards the iterator to the last element and returns it.  The
	 * "last" parameter should be the ":" parameter.
     *
     * @return Last parameter
	 * */
	public String last()
	{
		while( hasNext() )
			next();

		return last;
	}
	
	public void remove()
	{
		// hmm, nah.  This can be implemented some other time.
		throw new UnsupportedOperationException( "Remove on the parameters? Why?" );
	}
}


