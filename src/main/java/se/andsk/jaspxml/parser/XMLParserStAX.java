/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.parser;

import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import se.andsk.jaspxml.exceptions.InvalidParseCommandException;
import se.andsk.jaspxml.exceptions.ParsingException;

/**
 * A StAX based implementation of JaspXML. It was designed to enable parsing of very large XML
 * files using a simpler DOM like interface without the high memory cost of a DOM parser.
 * <p>
 * This implementation has not been properly tested against malformed XML documents. It should
 * ignore almost all errors that does not interfere with its ability to parse a document. 
 */
public class XMLParserStAX implements XMLParser
{
	private int currentDepth = 0;
	private int targetDepth = 0;
	
	private ElementStAX currentElement;
	
	private XMLEvent lastEvent;
	private XMLEventReader eventReader;
	
	private LinkedList<ElementStAX> parseStack = new LinkedList<ElementStAX>();
	private boolean canStepDown = false;

	public XMLParserStAX(XMLEventReader reader)
	{
		this.eventReader = reader;
	}

	/**
	 * Check if the current element has any child elements.
	 * 
	 * @return True if the current element has children.
	 * @throws ParsingException
	 */
	private boolean checkForChildren() throws ParsingException
	{
		XMLEvent e;
		try
		{
			do
			{
				e = eventReader.peek();

				if (e.isCharacters())
				{
					Characters c = e.asCharacters();

					// Ignore empty content
					if (c.isWhiteSpace())
					{
						eventReader.nextEvent();
					}
					else
					{
						// The next element is text.
						return true;
					}
				}
				else
				{
					// Found something that isn't text.
					break;
				}
			}
			while (true);
		}
		catch (XMLStreamException exception)
		{
			throw new ParsingException(exception);
		}

		// If the next event is an end event, then the current element is empty. Otherwise there is at least one
		// child element.
		return !e.isEndElement();
	}

	/**
	 * Parse a text element.
	 * 
	 * @return The content of a text element.
	 * @throws ParsingException
	 */
	private String getText() throws ParsingException
	{
		Characters c = lastEvent.asCharacters();

		// Ignore empty content
		if (!c.isWhiteSpace())
		{
			return c.toString();
		}
		else
		{
			return "";
		}
	}

	/**
	 * Step forward to the next element at the target depth
	 * 
	 * @throws ParsingException
	 */
	private boolean stepForward() throws ParsingException
	{
		try
		{
			while (true)
			{
				XMLEvent nextEvent = eventReader.peek();

				if (nextEvent.isStartElement() || (nextEvent.isCharacters() && !nextEvent.asCharacters().isWhiteSpace()))
				{
					if(targetDepth == currentDepth)
					{
						if(!nextEvent.isCharacters())
							++currentDepth;
						//Found what we were looking for
						return true;
					}
					
					if(!nextEvent.isCharacters())
						++currentDepth;
				}
				else if (nextEvent.isEndElement())
				{
					if(currentDepth == targetDepth)
					{
						return false;
					}
						
					--currentDepth;
				}
				else if (nextEvent.isEndDocument())
				{
					return false;
				}
				
				lastEvent = eventReader.nextEvent();
			}
		}
		catch (XMLStreamException e)
		{
			throw new ParsingException(e);
		}
	}

	@Override
	public int getDepth()
	{
		return currentDepth;
	}

	@Override
	public Element getElement()
	{
		return currentElement;
	}
	
	@Override
	public Element next() throws ParsingException
	{
		canStepDown = false;
		
		if(!stepForward())
		{
			currentElement = null;
			return null;
		}
		
		try
		{
			lastEvent = eventReader.nextEvent();
		}
		catch (XMLStreamException e)
		{
			throw new ParsingException(e);
		}
		
		if(lastEvent.isStartElement())
		{
			return createElement();
		} else if(lastEvent.isCharacters())
		{
			return createTextElement();
		}
		
		throw new Error("Parser reached bad state! " + lastEvent.getLocation().getLineNumber() + ":" + lastEvent.getLocation().getColumnNumber());
	}

	private ElementStAX createElement() throws ParsingException
	{
		StartElement start = lastEvent.asStartElement();
		boolean hasChildren = checkForChildren();
		
		if(hasChildren)
			canStepDown = true;
		
		@SuppressWarnings("unchecked")
		ElementStAX element = new ElementStAX(start.getName(), (Iterator<Attribute>) start.getAttributes(),
				hasChildren);
		currentElement = element;

		return currentElement;
	}
	
	private ElementStAX createTextElement() throws ParsingException
	{
		String text = getText();
		ElementStAX element = new ElementStAX(text);
		currentElement = element;

		return currentElement;
	}

	@Override
	public int down() throws ParsingException
	{
		if (!canStepDown)
		{
			throw new InvalidParseCommandException("Can't step down. Element " + currentElement.getName()
					+ " does not have any unparsed child elements", lastEvent.getLocation().getLineNumber(), lastEvent
					.getLocation().getColumnNumber());
		}

		// Add the current element to the stack, then tell the parser that it should parse the children of this element
		// next time.
		parseStack.push(currentElement);
		++targetDepth;
		currentElement = null;

		canStepDown = false;

		return parseStack.size();
	}

	@Override
	public int up() throws ParsingException
	{
		// In case stepUp is called too many times
		if (!parseStack.isEmpty())
		{
			// Restore the last element (i.e. the parent of the current element), then tell the parser that it should
			// parse the siblings of the parent next time.
			currentElement = parseStack.pop();
			--targetDepth;

			canStepDown = false;
		}
		return parseStack.size();
	}
}
