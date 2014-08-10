/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

import se.andsk.jaspxml.exceptions.TypeConversionException;

public class ElementStAX implements Element {
	private HashMap<QName, String> attributes = new HashMap<QName, String>();
	private QName name;
	private String text;
	private boolean hasChildren;
	
	/**
	 * Constructor for normal elements.
	 * 
	 * @param name element name
	 * @param attributeIterator element attributes
	 * @param hasChildren true if this element has children
	 */
	public ElementStAX(QName name, Iterator<Attribute> attributeIterator, boolean hasChildren)
	{
		this.name = name;
		this.hasChildren = hasChildren;
		
		while(attributeIterator.hasNext())
		{
			Attribute a = attributeIterator.next();
			attributes.put(a.getName(), a.getValue());
		}
	}

	/**
	 * Constructor for text elements
	 * 
	 * @param text content of this element
	 */
	public ElementStAX(String text)
	{
		this.name = null;
		this.text = text;
		this.hasChildren = false;
	}
	
	@Override
	public boolean hasAttribute(QName attribute)
	{
		return attributes.containsKey(attribute);
	}
	
	@Override
	public String getAttribute(QName attribute)
	{
		return attributes.get(attribute);
	}
	
	@Override
	public <To> To getAttribute(QName attribute, TypeConverter<String, To> converter) throws TypeConversionException
	{
		String a = getAttribute(attribute);
		
		if(a != null)
		{
			return converter.convert(a);
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public Map<QName, String> getAllAttributes()
	{
		return attributes;
	}
	

	@Override
	public <To> Map<QName, To> getAllAttributes(TypeConverter<String, To> converter) throws TypeConversionException
	{
		Map<QName, To> result = new HashMap<QName, To>();
		
		for(Entry<QName, String> entry : attributes.entrySet())
		{
			result.put(entry.getKey(), converter.convert(entry.getValue()));
		}
		
		return result;
	}
	
	@Override
	public QName getName()
	{
		if(isText())
			return null;
		else
			return name;
	}
	
	@Override
	public boolean isText()
	{
		return text != null;
	}
	

	@Override
	public String getText()
	{
		if(!isText())
			return null;
		else
			return text;
	}
	
	@Override
	public <To> To getText(TypeConverter<String, To> converter) throws TypeConversionException
	{
		if(!isText())
			return null;
		else
			return converter.convert(getText());
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ElementStAX))
		{
			return false;
		}
		else
		{
			ElementStAX i = (ElementStAX) o;
			
			if(isText() != i.isText())
			{
				return false;
			}
			
			if(isText())
			{
				return i.text.equals(this.text);
			}
			else
			{
				return i.attributes.equals(this.attributes) && i.name.equals(this.name) && i.hasChildren == hasChildren;
			}
		}
	}
	
	@Override
	public int hashCode()
	{
		int result = 17;
		result = 37 * result + name.hashCode();
		result = 37 * result + attributes.hashCode();
		result = 37 * result + (hasChildren ? 0 : 1);
		
		if(isText())
		{
			result = 37 * result + text.hashCode();
		}
		
		return result;
	}
	
	@Override
	public String toString()
	{
		StringBuilder strB = new StringBuilder();

		
		if(isText())
		{
			strB.append("Text Element\n");
			
			strB.append("[");
			strB.append(text);
			strB.append("]");
		}
		else
		{
			strB.append("Element <");
			strB.append(name);
			strB.append(">\n");
		
			for(Entry<QName, String> e : attributes.entrySet())
			{
				strB.append("[");
				strB.append(e.getKey());
				strB.append(":");
				strB.append(e.getValue());
				strB.append("] ");
			}
		}

		return strB.toString();
	}

	@Override
	public boolean hasChildren()
	{
		return hasChildren;
	}

}
