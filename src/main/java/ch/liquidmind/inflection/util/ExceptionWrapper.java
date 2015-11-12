package ch.liquidmind.inflection.util;

import java.io.IOException;

import com.google.common.reflect.ClassPath;

import __java.io.__IOException;

// TODO Replace with deflected libraries.
public class ExceptionWrapper
{
	public static ClassPath ClassPath_from( ClassLoader classLoader )
	{
		try
		{
			return ClassPath.from( classLoader );
		}
		catch ( IOException e )
		{
			throw new __IOException( e );
		}
	}
}
