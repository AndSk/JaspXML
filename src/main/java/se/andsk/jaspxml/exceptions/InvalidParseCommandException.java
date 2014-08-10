/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.exceptions;

/**
 * Thrown when the parser is asked to perform a task that isn't allowed at this point of the parsing process.
 */
public class InvalidParseCommandException extends ParsingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9019964732237539761L;

	public InvalidParseCommandException(String msg, int line, int column) {
		super(msg, line, column);
	}

}
