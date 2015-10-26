package ch.liquidmind.inflection.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyHandler implements InvocationHandler
{
	private static ThreadLocal< ProxyHandler > contextProxyHandler = new ThreadLocal< ProxyHandler >();
	
	public static ProxyHandler getContextProxyHandler()
	{
		if ( contextProxyHandler.get() == null )
			contextProxyHandler.set( new ProxyHandler() );
		
		return contextProxyHandler.get();
	}

	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		return null;
	}
}
