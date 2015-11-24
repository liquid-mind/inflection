package ch.liquidmind.inflection.test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestUtility
{

	public static URL[] convertToURLArray( File... files ) throws MalformedURLException
	{
		List<URL> urls = new ArrayList<>();
		for (File file : files) {
			URL url = file.toURI().toURL();
			urls.add( url );
		}
		return urls.toArray(new URL[urls.size()]);
	}
	
	public static Object invokeMethod(Object p, String methodName, Object... params) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<Class<?>> paramClasses = new ArrayList<>();
		for (Object param : params) {
			paramClasses.add( param.getClass() );
		}
		Method method = p.getClass().getMethod( methodName, paramClasses.toArray( new Class<?>[paramClasses.size()] ) );
		return method.invoke( p, params );
	}
		
}
