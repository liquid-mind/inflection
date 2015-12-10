package ch.liquidmind.inflection.test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import __java.io.__File;
import __java.lang.__Class;
import __java.lang.reflect.__Method;
import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.loader.TaxonomyLoader;

public final class TestUtility
{

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

	public static Object invokeMethod( Object p, String methodName, Object... params )
	{
		List< Class< ? > > paramClasses = new ArrayList< >();
		for ( Object param : params )
		{
			paramClasses.add( param.getClass() );
		}
		Method method = __Class.getMethod( p.getClass(), methodName, paramClasses.toArray( new Class< ? >[ paramClasses.size() ] ) );
		return __Method.invoke( method, p, params );
	}
	
	public static TaxonomyLoader getTaxonomyLoader(CompilationJob job) {
		return new TaxonomyLoader( TaxonomyLoader.getSystemTaxonomyLoader(), new URLClassLoader( TestUtility.convertToURLArray( job.getTargetDirectory() ), job.getTaxonomyLoader().getClassLoader() ) );
	}

	public static String generateMember( String type, String name )
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
