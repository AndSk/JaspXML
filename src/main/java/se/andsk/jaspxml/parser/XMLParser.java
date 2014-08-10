/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.parser;

import se.andsk.jaspxml.exceptions.ParsingException;

/**
 * JaspXML is just a simple XML parser that behaves as an iterator that iterates horizontally and vertically though an
 * XML file.
 * <p>
 * Each time {@code next} is called the parser iterates forwards to the next element at the current depth in the XML
 * file and returns it, ignoring any child elements that the current element may have. When the last element at the
 * current depth is reached {@code next} will return null.
 * <p>
 * Each time {@code down} is called the parser iterates downwards to the children of the current element. The current
 * element is then set to null and {@code next} must be called to get the first child element.
 * <p>
 * Each time {@code up} is called the parser iterates upwards and goes back to the last element parsed at this depth. It
 * is not possible to call {@code down} after {@code up} to return to already parsed elements. The parser can only go
 * forwards.
 */
public interface XMLParser
{

	/**
	 * Iterate one step forward at the current level of the XML file.
	 * 
	 * @return the new current element, or null if there are no more elements at this depth
	 * @throws ParsingException
	 */
	public Element next() throws ParsingException;

	/**
	 * Returns the last element that was parsed using {@code next}. If there are no more elements available, or no
	 * element has been parsed at the current depth, then it will return null.
	 * 
	 * @return the last element that was parsed, or null if no such elements are available
	 */
	public Element getElement();

	/**
	 * Returns the current parsing depth.
	 * 
	 * @return the current parsing depth
	 */
	public int getDepth();

	/**
	 * Iterate downwards to the children of the current element. This will set the current element to null.
	 * 
	 * @return the new depth that the parser has reached
	 * @throws ParsingException
	 */
	public int down() throws ParsingException;

	/**
	 * Iterate upwards to the parent of the current element. The parent element will become the current element,
	 * however, it is not possible to step back down to the children the parent afterwards.
	 * <p>
	 * If the parser is already at the root element then it will do nothing.
	 *
	 * @return the new depth that the parser has reached
	 * @throws ParsingException
	 */
	public int up() throws ParsingException;
}
