/*
 * ChannelLimitError.java
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

/**
 * Code: 471 ERR_CHANNELISFULL
 * &lt;channel&gt; :Cannot join channel (+l)
 * @author <a href="mailto:martyr@mog.se">Morgan Christiansson</a>
 * @version $Id: ChannelLimitError.java 85 2007-08-02 18:26:59Z jadestorm $
 * TODO: Rename to ChannelIsFullError to match style of others?
 */
public class ChannelLimitError extends GenericJoinError
{
	public ChannelLimitError()
	{
		// This one's for registering.
	}

	protected ChannelLimitError( String chan, String comment )
	{
		super(chan, comment);
	}

	public String getIrcIdentifier()
    {
		return "471";
	}

	protected InCommand create(String channel, String comment)
    {
		return new ChannelLimitError( channel, comment );
	}
}
