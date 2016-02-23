package ch.liquidmind.inflection.test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import __java.io.__File;
import __java.lang.__Class;
import __java.lang.__NoSuchMethodException;
import __java.lang.reflect.__Method;
import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.loader.TaxonomyLoader;

public final class TestUtility
{

	public final static Map< Class< ? >, Class< ? > > primitiveConverter = new HashMap< Class< ? >, Class< ? > >();

	static
	{
		primitiveConverter.put( Boolean.class, boolean.class );
		primitiveConverter.put( Byte.class, byte.class );
		primitiveConverter.put( Short.class, short.class );
		primitiveConverter.put( Character.class, char.class );
		primitiveConverter.put( Integer.class, int.class );
		primitiveConverter.put( Long.class, long.class );
		primitiveConverter.put( Float.class, float.class );
		primitiveConverter.put( Double.class, double.class );
	}

	public static URL[] convertToURLArray( File... files )
	{
		List< URL > urls = new ArrayList< >();
		for ( File file : files )
		{
			URL url = __File.toURL( file );
			urls.add( url );
		}
		return urls.toArray( new URL[ urls.size() ] );
	}
	
	public static Object invokeMethod( Object p, String methodName ) {
		return invokeMethod( p, methodName, new Object[] { }, new Class<?>[] { } );
	}
	
	public static Object invokeMethod( Object p, String methodName, Object param, Class<?> paramType ) {
		return invokeMethod( p, methodName, new Object[] { param }, new Class<?>[] { paramType } );
	}

	public static Object invokeMethod( Object p, String methodName, Object[] params, Class<?>[] paramTypes )
	{
		Method method = null;
		try
		{
			method = __Class.getMethod( p.getClass(), methodName, paramTypes );
		}
		catch ( __NoSuchMethodException e )
		{
			// try to find method with primitve types instead of objects (e.g. with long instead of Long parameters)
			List< Class< ? > > convertedParamTypes = new ArrayList< >();
			for ( Class< ? > paramClass : paramTypes )
			{
				if ( primitiveConverter.containsKey( paramClass ) )
				{
					convertedParamTypes.add( primitiveConverter.get( paramClass ) );
				}
				else
				{
					convertedParamTypes.add( paramClass );
				}
			}
			method = __Class.getMethod( p.getClass(), methodName, convertedParamTypes.toArray( new Class< ? >[ convertedParamTypes.size() ] ) );
		}
		return __Method.invoke( method, p, params );
	}

	public static TaxonomyLoader getTaxonomyLoader( CompilationJob job )
	{
		URLClassLoader classLoader = new URLClassLoader( TestUtility.convertToURLArray( job.getTargetDirectory() ), job.getTaxonomyLoader().getClassLoader() );
		return new TaxonomyLoader( TaxonomyLoader.getSystemTaxonomyLoader(), classLoader );
	}

	/**
	 * Generate source code text for member, getter and setter
	 * 
	 * @param type
	 *            the desired type, e.g. {@link Long}
	 * @param name
	 *            the member name (starting with lowercase letters), e.g. member
	 * @return
	 */
	public static String generateMember( final String type, final String name )
	{
		String initCap = capitalize( name );
		String string = "private {0} {1}; public {0} get{2}() { return {1}; }; public void set{2}({0} {1}) { this.{1} = {1}; };";
		string = string.replace( "{0}", type );
		string = string.replace( "{1}", name );
		string = string.replace( "{2}", initCap );
		return string;
	}

	private static String capitalize( final String line )
	{
		return Character.toUpperCase( line.charAt( 0 ) ) + line.substring( 1 );
	}
}
