/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.exceptions;

/**
 * Generic XML parsing exception. Parent of all exceptions thrown by the XML parser.
 */
public class ParsingException extends Exception
{
	private static final long serialVersionUID = 5079810767508169421L;

	public ParsingException(String msg, int line, int column)
	{
		super(msg + " at " + line + ":" + column);
	}

	public ParsingException(Exception e)
	{
		super(e);
	}
}
