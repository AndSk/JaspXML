/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import se.andsk.jaspxml.exceptions.InvalidParseCommandException;
import se.andsk.jaspxml.exceptions.ParsingException;
import se.andsk.jaspxml.exceptions.TypeConversionException;
import se.andsk.jaspxml.parser.Element;
import se.andsk.jaspxml.parser.ElementStAX;
import se.andsk.jaspxml.parser.XMLParser;

public class XMLParserStAXTest
{
	
	private XMLParser parser;

	public void setUp(String fileName) throws XMLStreamException, FileNotFoundException
	{
		URL url = this.getClass().getResource(fileName);
		File file = new File(url.getFile());
		InputStream in = new FileInputStream(file);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = factory.createXMLEventReader(in);

		parser = new XMLParserStAX(eventReader);
	}

	/**
	 * Step through the whole document and make sure that everything is parsed correctly.
	 * 
	 * @throws ParsingException
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 * @throws TypeConversionException 
	 */
	@Test
	public void testStructure01() throws ParsingException, FileNotFoundException, XMLStreamException, TypeConversionException
	{
		setUp("/test01.xml");
		
		assertTrue(parser.getElement() == null);

		Element e = parser.next();
		assertEquals("level0", e.getName().getLocalPart());
		assertTrue(e.hasAttribute(new QName("a")));
		assertTrue(e.hasAttribute(new QName("b")));
		assertFalse(e.hasAttribute(new QName("c")));
		assertEquals("1", e.getAttribute(new QName("a")));
		assertEquals("2.3", e.getAttribute(new QName("b")));
		assertEquals(null, e.getAttribute(new QName("c")));
		
		assertEquals(Integer.valueOf(1), e.getAttribute(new QName("a"), TypeConverter.StringToInt));
		assertEquals(Double.valueOf(2.3), e.getAttribute(new QName("b"), TypeConverter.StringToDouble));
		assertEquals(null, e.getAttribute(new QName("c"), TypeConverter.StringToInt));
		
		try
		{
			e.getAttribute(new QName("b"), TypeConverter.StringToInt);
					fail("Should throw exception");
		} catch(TypeConversionException exception)	{}
		
		assertTrue(e.equals(parser.getElement()));
		assertTrue(e.hasChildren());
		
		Map<QName, String> attributes = e.getAllAttributes();
		assertEquals("1", attributes.get(new QName("a")));
		assertEquals("2.3", attributes.get(new QName("b")));
		assertEquals(2, attributes.size());
		
		Map<QName, Double> attributes_double = e.getAllAttributes(TypeConverter.StringToDouble);
		assertEquals(Double.valueOf(1.0), attributes_double.get(new QName("a")));
		assertEquals(Double.valueOf(2.3), attributes_double.get(new QName("b")));
		
		assertFalse(e.isText());
		assertEquals(null, e.getText());
		
		int level = parser.down();
		assertEquals(1, level);
		assertEquals(null, parser.getElement());

		e = parser.next();
		assertEquals("level1_1", e.getName().getLocalPart());
		assertFalse(e.hasChildren());

		e = parser.next();
		assertEquals("level1_2", e.getName().getLocalPart());
		assertEquals("a", e.getAttribute(new QName("a")));
		assertFalse(e.hasChildren());

		e = parser.next();
		assertEquals("level1_text", e.getName().getLocalPart());
		assertTrue(e.hasChildren());
		
		level = parser.down();
		assertEquals(2,level);
		
		e = parser.next();
		assertEquals(null, e.getName());
		assertFalse(e.hasChildren());
		assertEquals("some text", e.getText());
		
		level = parser.up();
		assertEquals(1, level);
		assertEquals("level1_text", parser.getElement().getName().getLocalPart());
		
		e = parser.next();
		assertEquals("level1_3", e.getName().getLocalPart());
		assertTrue(e.hasChildren());

		level = parser.down();
		assertEquals(2, level);

		e = parser.next();
		assertEquals("level2_1", e.getName().getLocalPart());
		assertFalse(e.hasChildren());
		
		e = parser.next();
		assertEquals("level2_2", e.getName().getLocalPart());
		assertTrue(e.hasChildren());

		level = parser.down();
		assertEquals(3, level);

		e = parser.next();
		assertEquals("level3_1", e.getName().getLocalPart());
		assertFalse(e.hasChildren());

		e = parser.next();
		assertEquals("level3_2", e.getName().getLocalPart());
		assertFalse(e.hasChildren());
		
		try
		{
			level = parser.down();
			fail("Should throw exception");
		} catch(InvalidParseCommandException exception)	{}
		
		assertEquals(3, level);
		assertEquals("level3_2", parser.getElement().getName().getLocalPart());

		assertEquals(null, parser.next());
		assertEquals(null, parser.getElement());

		level = parser.up();
		assertEquals(2, level);
		assertEquals("level2_2", parser.getElement().getName().getLocalPart());
		
		e = parser.next();
		assertEquals("level2_3", e.getName().getLocalPart());

		level = parser.up();
		assertEquals(1, level);
		assertEquals("level1_3", parser.getElement().getName().getLocalPart());

		e = parser.next();
		assertEquals("level1_copy", e.getName().getLocalPart());
		assertFalse(e.hasChildren());

		e = parser.next();
		assertEquals("level1_copy", e.getName().getLocalPart());
		assertFalse(e.hasChildren());
		
		level = parser.up();
		assertEquals("level0", parser.getElement().getName().getLocalPart());
		
		assertEquals(null, parser.next());
		assertEquals(null, parser.getElement());
		
		assertEquals(null, parser.next());
		assertEquals(null, parser.getElement());
	}

	/**
	 * Go down one level and call {@code next} until the end is reached.
	 * 
	 * @throws ParsingException
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	@Test
	public void testStructure02() throws ParsingException, FileNotFoundException, XMLStreamException
	{
		setUp("/test01.xml");
		
		assertTrue(parser.getElement() == null);

		Element e = parser.next();
		assertEquals("level0", e.getName().getLocalPart());
		assertEquals("1", e.getAttribute(new QName("a")));
		assertEquals("2.3", e.getAttribute(new QName("b")));

		int level = parser.down();
		assertEquals(1, level);

		e = parser.next();
		assertEquals("level1_1", e.getName().getLocalPart());

		e = parser.next();
		assertEquals("level1_2", e.getName().getLocalPart());
		assertEquals("a", e.getAttribute(new QName("a")));

		e = parser.next();
		assertEquals("level1_text", e.getName().getLocalPart());

		e = parser.next();
		assertEquals("level1_3", e.getName().getLocalPart());

		e = parser.next();
		assertEquals("level1_copy", e.getName().getLocalPart());

		e = parser.next();
		assertEquals("level1_copy", e.getName().getLocalPart());
		
		e = parser.next();
		assertEquals(null, e);
		assertEquals(null, parser.getElement());
		
		level = parser.up();
		assertEquals(0, level);
		
		e = parser.next();
		assertEquals(null, e);
		assertEquals(null, parser.getElement());
	}
	
	@Test
	public void testCompare01() throws ParsingException, FileNotFoundException, XMLStreamException
	{
		setUp("/test01.xml");
		
		ElementStAX l0 = (ElementStAX) parser.next();

		parser.down();

		ElementStAX l1_1 = (ElementStAX) parser.next();
		ElementStAX l1_2 = (ElementStAX) parser.next();
		ElementStAX l1_text = (ElementStAX) parser.next();
		ElementStAX l1_3 = (ElementStAX) parser.next();
		ElementStAX l1_copy1 = (ElementStAX) parser.next();
		ElementStAX l1_copy2 = (ElementStAX) parser.next();
		
		assertFalse(l0.equals(l1_1));
		assertFalse(l0.hashCode() == l1_1.hashCode());
		
		assertTrue(l1_2.equals(l1_2));		
		assertTrue(l1_2.hashCode() == l1_2.hashCode());
		
		assertFalse(l1_text.equals(l1_3));
		assertTrue(l1_text.equals(l1_text));		

		assertTrue(l1_copy1.equals(l1_copy2));
		assertTrue(l1_copy1.hashCode() == l1_copy2.hashCode());
	}
	
	/**
	 * Attempt to perform parsing actions when they are not allowed.
	 *  
	 * @throws ParsingException
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	@Test
	public void testExceptions01() throws ParsingException, FileNotFoundException, XMLStreamException
	{
		setUp("/test01.xml");
	
		assertEquals(null, parser.getElement());

		parser.next();
		assertEquals("level0", parser.getElement().getName().getLocalPart());

		int level = parser.down();
		assertEquals(1, level);

		parser.next();
		assertEquals("level1_1", parser.getElement().getName().getLocalPart());

		try
		{
			level = parser.down();
			fail("Should throw exception");
		} catch(InvalidParseCommandException e)	{}

		assertEquals(1, level);
		assertEquals("level1_1", parser.getElement().getName().getLocalPart());

		parser.next();
		parser.next();
		parser.next();

		assertEquals("level1_3", parser.getElement().getName().getLocalPart());

		level = parser.down();
		assertEquals(2, level);

		parser.next();
		assertEquals("level2_1", parser.getElement().getName().getLocalPart());

		level = parser.up();
		assertEquals(1, level);
		assertEquals("level1_3", parser.getElement().getName().getLocalPart());

		try
		{
			level = parser.down();
			fail("Should throw exception");
		} catch(InvalidParseCommandException e) {}

		assertEquals(1, level);
		assertEquals("level1_3", parser.getElement().getName().getLocalPart());

		level = parser.up();

		assertEquals(0, level);
		assertEquals("level0", parser.getElement().getName().getLocalPart());

		try
		{
			level = parser.down();
			fail("Should throw exception");
		} catch(InvalidParseCommandException e) {}

		assertEquals(0, level);
		assertEquals("level0", parser.getElement().getName().getLocalPart());
		
		level = parser.up();

		assertEquals(0, level);
		assertEquals("level0", parser.getElement().getName().getLocalPart());
	}
	
	@Test
	public void testMixedContent01() throws ParsingException, FileNotFoundException, XMLStreamException
	{
		setUp("/mixed.xml");
		
		assertTrue(parser.getElement() == null);

		Element e = parser.next();
		assertEquals("mixed", e.getName().getLocalPart());
		
		int level = parser.down();
		assertEquals(1, level);
		
		e = parser.next();
		assertEquals(null, e.getName());
		assertTrue(e.isText());
		assertEquals("\n  First line ", e.getText());
		
		e = parser.next();
		assertEquals("tag1", e.getName().getLocalPart());
		assertFalse(e.isText());
		assertTrue(e.hasChildren());
		
		level = parser.down();
		assertEquals(2, level);
		
		e = parser.next();
		assertEquals(null, e.getName());
		assertTrue(e.isText());
		assertEquals("inside tag 1", e.getText());
		
		e = parser.next();
		
		assertTrue(e == null);
		
		level = parser.up();
		assertEquals(1, level);
		
		e = parser.next();
		assertEquals(null, e.getName());
		assertTrue(e.isText());
		assertEquals(" after tag.\n  Second line ", e.getText());
		
		e = parser.next();
		assertEquals("tag2", e.getName().getLocalPart());
		assertFalse(e.isText());
		assertTrue(e.hasChildren());
		
		level = parser.down();
		assertEquals(2, level);
		
		e = parser.next();
		assertEquals(null, e.getName());
		assertTrue(e.isText());
		assertEquals("inside tag 2", e.getText());
		
		e = parser.next();
		
		assertTrue(e == null);
		
		level = parser.up();
		assertEquals(1, level);
		
		e = parser.next();
		assertEquals("tag3", e.getName().getLocalPart());
		assertFalse(e.isText());
		assertTrue(e.hasChildren());
		
		level = parser.down();
		assertEquals(2, level);
		
		e = parser.next();
		assertEquals(null, e.getName());
		assertTrue(e.isText());
		assertEquals("inside tag 3", e.getText());
		
		e = parser.next();
		
		assertTrue(e == null);
		
		level = parser.up();
		assertEquals(1, level);
		
		e = parser.next();
		assertEquals(null, e.getName());
		assertTrue(e.isText());
		assertEquals(" third line\n", e.getText());
		
		e = parser.next();
		
		assertTrue(e == null);
	}
	
	@Test
	public void testNamespace01() throws FileNotFoundException, XMLStreamException, ParsingException, TypeConversionException
	{
		setUp("/namespace.xml");
		
		Element e = parser.next();
		assertEquals("level0", e.getName().getLocalPart());
		assertEquals("n1", e.getName().getPrefix());
		assertEquals("http://www.example.com/namespace1", e.getName().getNamespaceURI());
		
		assertEquals(1, parser.down());
		
		e = parser.next();
		assertEquals("level1.1", e.getName().getLocalPart());
		assertEquals("n1", e.getName().getPrefix());

		assertEquals(2, parser.down());
		
		e = parser.next();
		assertEquals("level2.1", e.getName().getLocalPart());
		assertEquals("n1", e.getName().getPrefix());
		
		assertEquals(3, parser.down());
		
		e = parser.next();
		assertTrue(e.isText());
		assertEquals("Some text", e.getText());
		
		assertEquals(2, parser.up());
		assertEquals(1, parser.up());
		
		e = parser.next();
		assertEquals("level1.2", e.getName().getLocalPart());
		assertEquals("n1", e.getName().getPrefix());
		
		assertEquals(2, parser.down());
		
		e = parser.next();
		assertEquals("level2.1", e.getName().getLocalPart());
		assertEquals("n2", e.getName().getPrefix());
		assertEquals("http://www.example.com/namespace2", e.getName().getNamespaceURI());
		
		assertEquals(3, parser.down());
		
		e = parser.next();
		assertEquals("level3.1", e.getName().getLocalPart());
		assertEquals("n2", e.getName().getPrefix());
		assertEquals("Attribute A", e.getAttribute(new QName("http://www.example.com/namespace1", "a")));

		assertEquals(4, parser.down());
		
		e = parser.next();
		assertTrue(e.isText());
		assertEquals(Double.valueOf(9.9), e.getText(TypeConverter.StringToDouble));
		
		assertEquals(3, parser.up());
		assertEquals(2, parser.up());
		assertEquals(1, parser.up());
		assertEquals(0, parser.up());
	}
}
