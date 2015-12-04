package ch.liquidmind.inflection.test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import __java.io.__File;
import __java.lang.__Class;
import __java.lang.reflect.__Method;

public class TestUtility
{

	public static URL[] convertToURLArray( File... files ) 
	{
		List<URL> urls = new ArrayList<>();
		for (File file : files) {
			URL url = __File.toURL( file );
			urls.add( url );
		}
		return urls.toArray(new URL[urls.size()]);
	}
	
	public static Object invokeMethod(Object p, String methodName, Object... params) {
		List<Class<?>> paramClasses = new ArrayList<>();
		for (Object param : params) {
			paramClasses.add( param.getClass() );
		}
		Method method = __Class.getMethod( p.getClass(), methodName, paramClasses.toArray( new Class<?>[paramClasses.size()] ) );
		return __Method.invoke( method, p, params );
	}
		
}
