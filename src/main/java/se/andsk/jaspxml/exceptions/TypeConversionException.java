/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.exceptions;

/**
 * Thrown when a type conversion function cannot convert data to a specified type.
 */
public class TypeConversionException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1747393375221163665L;

	
	public TypeConversionException(String msg)
	{
		super(msg);
	}
	
	public TypeConversionException(Exception e)
	{
		super(e);
	}
}
