/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.examples;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import se.andsk.jaspxml.exceptions.ParsingException;
import se.andsk.jaspxml.parser.Element;
import se.andsk.jaspxml.parser.XMLParser;
import se.andsk.jaspxml.parser.XMLParserStAX;

/**
 * This example application iterates through each element in an XML file and prints its contents. Two methods for doing this is shown; one using an {@link Iterator} and one using a loop.
 */
public class ParseAndPrint
{
	/**
	 * An example implementation of a Java Iterator using JaspXML. The iterator has to do some additional work since JaspXML does not have a {@code hasNext} method.
	 */
	static class XMLIterator implements Iterator<Element>
	{
		private XMLParser parser;
		private boolean foundNext = false;
		
		public XMLIterator(XMLParser parser)
		{
			this.parser = parser;
		}

		@Override
		public boolean hasNext()
		{
			if(parser.getElement() == null)
				findNextElement();

			return foundNext;
		}

		@Override
		public Element next()
		{
			if(!hasNext())
			{
				return null;
			}
			else
			{
				Element result = parser.getElement();
				findNextElement();
				return result;
			}
		}
		
		private void findNextElement()
		{			
			try
			{
				if(parser.getElement() != null && parser.getElement().hasChildren())
					parser.down();
				
				while(true)
				{
					parser.next();

					if(parser.getElement() != null)
					{
						foundNext = true;
						return;
					}
					else
					{
						if(parser.getDepth() == 0)
						{
							foundNext = false;
							return;
						}
						else
						{
							parser.up();
						}
					}
				}
			}
			catch (ParsingException e)
			{
				throw new RuntimeException(e);
			}
		}

		@Override
		public void remove()
		{
			throw new RuntimeException("Can't remove from a read only iterator");
		}
		
	}
	public static void main(String[] args) throws ParsingException, IOException, XMLStreamException
	{
		if(args.length == 0 || args.length > 3)
		{
			System.err.println("Incorrect number of arguments");
			printHelp();
			System.exit(1);
		}
		
		boolean compressed = false;
		String fileName = null;
		
		for(String arg : args)
		{
			if(arg.equals("-h"))
			{
				printHelp();
				System.exit(0);
			} else if(arg.equals("-c"))
			{
				compressed = true;
			} else
			{
				if(fileName == null)
				{
					fileName = arg;
				}
				else
				{
					System.err.println("Bad argument");
					printHelp();
					System.exit(1);
				}
			}
		}
		
		if(fileName == null)
		{
			System.err.println("No file name given");
			printHelp();
			System.exit(1);
		}
						
		InputStream in = new FileInputStream(fileName);
		
		if(compressed)
		{
			in = new GZIPInputStream(in);
		}
		
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = factory.createXMLEventReader(in);

		XMLParserStAX parser = new XMLParserStAX(eventReader);
		
		XMLIterator itr = new XMLIterator(parser);
		
		//Parse using iterator
		while(itr.hasNext())
			printElement(itr.next());
		
		//Parse using loop
		//parseSiblings(parser);
	}

	/**
	 * Print out the contents of an XML file using a recursive loop
	 * 
	 * @param parser
	 * @throws ParsingException
	 */
	@SuppressWarnings("unused")
	private static void parseSiblings(XMLParserStAX parser) throws ParsingException
	{
		Element e;
		while((e = parser.next()) != null)
		{
			printElement(e);
			
			if(e.hasChildren())
			{
				parser.down();
				parseSiblings(parser);
				parser.up();
			}
		}
	}

	private static void printElement(Element e) throws ParsingException
	{
		if(e.isText())
		{
			System.out.println("------------------------------------TEXT----------------------------------------");
			System.out.println(e.getText());
		}
		else
		{
			System.out.println("---------------------------------ELEMENT----------------------------------------");
			System.out.println(e.getName());
			
			Map<QName, String> attributes = e.getAllAttributes();
			if(attributes.size() > 0)
			{
				System.out.println();
				for(java.util.Map.Entry<QName, String> attr : attributes.entrySet())
				{
					System.out.println(attr.getKey() + ":" + attr.getValue());
				}
			}
		}
		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println();
	}

	private static void printHelp()
	{
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("ParseAndPrint");
		System.out.println("");
		System.out.println("Parses an XML file and prints its content.");
		System.out.println("");
		System.out.println("-h	Show this help message");
		System.out.println("-c	The input file is compressed using Gzip");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("parseandprint [-h] [-c] FILE");
		System.out.println("--------------------------------------------------------------------------------");
	}
}
