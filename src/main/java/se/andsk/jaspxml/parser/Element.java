/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.parser;

import java.util.Map;

import javax.xml.namespace.QName;

import se.andsk.jaspxml.exceptions.TypeConversionException;

/**
 * An XML element.
 * <p>
 * There are two types of elements; normal elements and text elements. Normal elements can have attributes, a name, and children. A text element can only have text content.
 */
public interface Element
{
	/**
	 * Returns a map of all attributes with their values as strings.
	 * 
	 * @return a map of all attributes and their values
	 */
	public Map<QName, String> getAllAttributes();
	
	/**
	 * Returns a map of all attributes with their values converted to the type {@code To} using a {@link TypeConverter}.
	 * 
	 * @param converter type converter used to convert all attribute values to the type {@code To} 
	 * @return a map of all attributes and their values
	 * @throws TypeConversionException when the attributes could not be converted to the specified type
	 */
	public <To> Map<QName, To> getAllAttributes(TypeConverter<String, To> converter) throws TypeConversionException;

	/**
	 * Returns the name of the element. Text elements do not have true element names and will return null.
	 * 
	 * @return the name of the element, or null if it is a text element
	 */
	public QName getName();
	
	/**
	 * Returns true if this element has children. This will always be false for text elements.
	 * 
	 * @return true if the element has children
	 */
	public boolean hasChildren();
	
	/**
	 * Returns true if this element has an attribute with name {@code attribute}. This will always be false for text elements.
	 * 
	 * @param attribute name of an attribute
	 * @return true if the attribute exists
	 */
	public boolean hasAttribute(QName attribute);
	
	/**
	 * Returns the value of an attribute.
	 * 
	 * @param attribute name of the attribute to return
	 * @return the value of the attribute, or null if no such attribute exists
	 */
	public <To> String getAttribute(QName attribute);
	
	/**
	 * Returns the value of an attribute after it has been converted to type {@code To} using a {@link TypeConverter}.
	 * 
	 * @param attribute name of the attribute to return
	 * @param converter type converter used to convert the value of the attribute to type {@code To}
	 * @return the value of the attribute, or null if no such attribute exists
	 * @throws TypeConversionException when the attribute could not be converted to the specified type
	 */
	public <To> To getAttribute(QName attribute, TypeConverter<String, To> converter) throws TypeConversionException;
	
	/**
	 * Returns true if this is a text element.
	 * 
	 * @return true if this is a text element
	 */
	public boolean isText();
	
	/**
	 * Returns the content of this element.
	 * 
	 * @return the text in this element, or null if this is not a text element
	 */
	public <To> String getText();
	
	
	/**
	 * Returns the content of this element, converted to type {@code To} using a {@link TypeConverter}.
	 * 
	 * @param converter type converter used to convert the contents of this element to type {@code To}
	 * @return the content of this element, or null if this is not a text element
	 * @throws TypeConversionException when the contents could not be converted to the specified type
	 */
	public <To> To getText(TypeConverter<String, To> converter) throws TypeConversionException;
}
