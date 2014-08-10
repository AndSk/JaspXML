/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package se.andsk.jaspxml.parser;

import se.andsk.jaspxml.exceptions.TypeConversionException;

/**
 * A function object used to convert data form one type to another.
 *  
 * @param <FromType> the type to convert from
 * @param <ToType> the type to convert to
 */
public interface TypeConverter<FromType, ToType>
{
	/**
	 * Convert the argument {@code d} from type {@code FromType} to the type {@code ToType}.
	 *    
	 * @param d data to be converted
	 * @return a new object of type {@code ToType}
	 * @throws TypeConversionException
	 */
	ToType convert(FromType d) throws TypeConversionException;
	
	public static final TypeConverter<String, Integer> StringToInt = new TypeConverter<String, Integer>()
	{

		@Override
		public Integer convert(String from) throws TypeConversionException
		{
			try
			{
				return Integer.valueOf(from);
			}
			catch (NumberFormatException e)
			{
				throw new TypeConversionException(e);
			}

		}
	};
	
	public static final TypeConverter<String, Long> StringToLong = new TypeConverter<String, Long>()
	{

		@Override
		public Long convert(String from) throws TypeConversionException
		{
			try
			{
				return Long.valueOf(from);
			}
			catch (NumberFormatException e)
			{
				throw new TypeConversionException(e);
			}

		}
	};
	
	public static final TypeConverter<String, Float> StringToFloat = new TypeConverter<String, Float>()
	{

		@Override
		public Float convert(String from) throws TypeConversionException
		{
			try
			{
				return Float.valueOf(from);
			}
			catch (NumberFormatException e)
			{
				throw new TypeConversionException(e);
			}

		}
	};
	
	public static final TypeConverter<String, Double> StringToDouble = new TypeConverter<String, Double>()
	{

		@Override
		public Double convert(String from) throws TypeConversionException
		{
			try
			{
				return Double.valueOf(from);
			}
			catch (NumberFormatException e)
			{
				throw new TypeConversionException(e);
			}

		}
	};
	
	public static final TypeConverter<String, Boolean> StringToBoolean = new TypeConverter<String, Boolean>()
	{

		@Override
		public Boolean convert(String from) throws TypeConversionException
		{
			try
			{
				return Boolean.valueOf(from);
			}
			catch (NumberFormatException e)
			{
				throw new TypeConversionException(e);
			}

		}
	};

	public static final TypeConverter<String, String> Identity = new TypeConverter<String, String>()
	{
		@Override
		public String convert(String from) throws TypeConversionException
		{
			return from;
		}
	};
	
	public static final TypeConverter<String, String> LowerCase = new TypeConverter<String, String>()
	{
		@Override
		public String convert(String from) throws TypeConversionException
		{
			return from.toLowerCase();
		}
	};
	
	public static final TypeConverter<String, String> UpperCase = new TypeConverter<String, String>()
	{
		@Override
		public String convert(String from) throws TypeConversionException
		{
			return from.toUpperCase();
		}
	};
}
