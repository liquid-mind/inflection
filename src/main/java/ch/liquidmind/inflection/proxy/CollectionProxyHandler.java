package ch.liquidmind.inflection.proxy;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import __java.lang.__Class;
import ch.liquidmind.inflection.model.external.Taxonomy;

public class CollectionProxyHandler implements InvocationHandler
{
	private static ThreadLocal< CollectionProxyHandler > contextCollectionProxyHandler = new ThreadLocal< CollectionProxyHandler >();
	
	public static CollectionProxyHandler getContextCollectionProxyHandler()
	{
		if ( contextCollectionProxyHandler.get() == null )
			contextCollectionProxyHandler.set( new CollectionProxyHandler() );
		
		return contextCollectionProxyHandler.get();
	}

	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		return invoke( (Proxy)proxy, method, args );
	}
	
	private Object invoke( Proxy proxy, Method method, Object[] args ) throws Throwable
	{
		Object targetObject = ProxyRegistry.getContextProxyRegistry().getObject( proxy );
		Object viewableRetVal = null;
		
		if ( targetObject.getClass().isArray() )
		{
			viewableRetVal = mapArrayOperation( proxy, method, args, targetObject );
		}
		else
		{
			List< Object > viewableArgs = getViewableObjects( args );
			Method viewableMethod = targetObject.getClass().getMethod( method.getName(), method.getParameterTypes() );
			viewableMethod.setAccessible( true );
			viewableRetVal = invokeWithExceptionHandling( viewableMethod, targetObject, viewableArgs.toArray() );
		}
		
		Object proxyRetVal = getProxyObject( ProxyHelper.getTaxonomy( proxy ), viewableRetVal );

		return proxyRetVal;
	}
	
	private static final Method ITERABLE_PROXY_ITERATOR;
	private static final Method LIST_PROXY_GET;
	
	static
	{
		ITERABLE_PROXY_ITERATOR = __Class.getDeclaredMethod( IterableProxy.class, "iterator", new Class< ? >[] {} );
		LIST_PROXY_GET = __Class.getDeclaredMethod( ListProxy.class, "get", new Class< ? >[] { int.class } );
	}
	
	private Object mapArrayOperation( Proxy proxy, Method method, Object[] args, Object targetObject )
	{
		Object viewableRetVal = null;
		
		if ( method.equals( ITERABLE_PROXY_ITERATOR ) )
		{
			viewableRetVal = new 
		}
		else if ( method.equals( LIST_PROXY_GET ) )
		{
			viewableRetVal = Array.get( targetObject, (int)args[ 0 ] );
		}
		
		return viewableRetVal;
	}
	
	private Object invokeWithExceptionHandling( Method method, Object object, Object ... args ) throws Throwable
	{
		try
		{
			return method.invoke( object, args );
		}
		catch ( InvocationTargetException e )
		{
			// Re-throw the target exception.
			throw e.getTargetException();
		}
	}
	
	// TODO: Create common super class for ProxyHandler and CollectionProxyHandler
	// and move these methods to there.
	private List< Object > getViewableObjects( Object[] rawObjects )
	{
		List< Object > viewableObjects = new ArrayList< Object >();
		
		for ( Object rawObject : rawObjects )
		{
			if ( rawObject instanceof Proxy )
				viewableObjects.add( ProxyRegistry.getContextProxyRegistry().getObject( (Proxy)rawObject ) );
			else
				viewableObjects.add( rawObject );
		}
		
		return viewableObjects;
	}
	
	private Object getProxyObject( Taxonomy taxonomy, Object viewableObject )
	{
		Object proxyObject = ProxyRegistry.getContextProxyRegistry().getProxy( taxonomy, viewableObject );
		
		if ( proxyObject == null )
			proxyObject = viewableObject;
		
		return proxyObject;
	}
}
