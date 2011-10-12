/*
 * GenericJoinError.java
 *
 *   Copyright (C) 2000, 2001, 2002, 2003 Ben Damm
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   See: http://www.fsf.org/copyleft/lesser.txt
 */
package f00f.net.irc.martyr.errors;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;
import f00f.net.irc.martyr.util.ParameterIterator;

/**
 * @author <a href="mailto:martyr@mog.se">Morgan Christiansson</a>
 * @version $Id: GenericJoinError.java 31 2004-04-01 22:02:33Z bdamm $
 */
public abstract class GenericJoinError extends GenericError {
	private String channel;
	private String comment;

	public GenericJoinError() {
	}

	protected GenericJoinError(String chan, String comment) 
	{
		this.channel = chan;
		this.comment = comment;
	}

	protected abstract InCommand create(String channel, String comment);

	public String getChannel()
	{
		return channel;
	}
	
	public String getComment()
	{
		return comment;
	}

	public State getState() {
		return State.UNKNOWN;
	}

	public InCommand parse( String prefix, String identifier, String params )
	{
		ParameterIterator pI = new ParameterIterator( params );
	
		pI.next(); // We know what our name is.
		String channel = (String)pI.next();
		String comment = (String)pI.next();
		
		return create( channel, comment );
	}
}
