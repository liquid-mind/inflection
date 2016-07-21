package ch.liquidmind.inflection.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import __java.lang.__Class;
import ch.liquidmind.inflection.Inflection;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.proxy.memory.ProxyOwnedVirtualObjectReference;

public class Proxy
{
	// Used by memory management sub-system.
	@SuppressWarnings( "unused" )
	private ProxyOwnedVirtualObjectReference virtualObjectReference;
	
	protected Proxy()
	{
		super();
	}

	protected Method getMethod( String name, Class< ? >[] paramTypes )
	{
		return __Class.getDeclaredMethod( this.getClass(), name, paramTypes );
	}
	
	@SuppressWarnings( "unchecked" )
	protected < T extends Object > T invoke( String methodName, Class< ? >[] paramTypes, Object[] params ) throws Throwable
	{
		Method method = getDeclaredMethodRecursive( Inflection.getView( this ).getViewedClass(), methodName, paramTypes );
		
		if ( method == null )
			method = getUsedMethod( Inflection.getView( this ), methodName );
		
		if ( method == null )
			throw new IllegalStateException( "Unable to resolve method: " + methodName );
		
		Object retVal = ProxyHandler.getContextProxyHandler().invoke( this, method, params );
		
		return (T)retVal;
	}
	
	private Method getUsedMethod( View view, String methodName )
	{
		Method foundMethod = null;
		
		for ( Class< ? > usedClass : getUsedClassesRecursive( view ) )
		{
			for ( Method usedMethod : usedClass.getMethods() )
			{
				// TODO: This is a very ugly work-around: we really should be looking for the method
				// by its full signature, not just by the name (which can be overloaded). I'm
				// only leaving this in for now because the proxy generator and associated classes
				// need to be completely rewritten anyway.
				if ( usedMethod.getName().equals( methodName ) )
				{
					foundMethod = usedMethod;
					break;
				}
			}
			
			if ( foundMethod != null )
				break;
		}
		
		if ( foundMethod == null && Inflection.getTaxonomy( this ).getSuperview( view ) != null )
			foundMethod = getUsedMethod( Inflection.getTaxonomy( this ).getSuperview( view ), methodName );
		
		return foundMethod;
	}
	
	private List< Class< ? > > getUsedClassesRecursive( View view )
	{
		List< Class< ? > > usedClassesRecursive = new ArrayList< Class< ? > >();
		
		if ( view.getUsedClass() != null )
			usedClassesRecursive.add( view.getUsedClass() );
		
		View superView = Inflection.getTaxonomy( this ).getSuperview( view );
		
		if ( superView != null )
			usedClassesRecursive.addAll( getUsedClassesRecursive( superView ) );
		
		return usedClassesRecursive;
	}
	
	// TODO: refactor this and the above method and/or the entire class to fit
	// better with the two distinct cases: (non-collection) object proxy and collection proxy
	@SuppressWarnings( "unchecked" )
	protected < T extends Object > T invokeOnCollection( String methodName, Class< ? >[] paramTypes, Object[] params )
	{
		Method method = getDeclaredMethodRecursive( this.getClass(), methodName, paramTypes );
		Object retVal;
		
		try
		{
			retVal = CollectionProxyHandler.getContextCollectionProxyHandler().invoke( this, method, params );
		}
        catch ( RuntimeException | Error ex )
        {
            throw ex;
        }
        catch ( java.lang.Throwable ex )
        {
            throw new IllegalStateException( "Unexpected exception type: " + ex.getClass().getName(), ex );
        }
		
		return (T)retVal;
	}
	
	private static class DeclaredMethodKey
	{
		private Class< ? > aClass;
		private String methodName;
		private Class< ? >[] paramTypes;
		
		public DeclaredMethodKey( Class< ? > aClass, String methodName, Class< ? >[] paramTypes )
		{
			super();
			this.aClass = aClass;
			this.methodName = methodName;
			this.paramTypes = paramTypes;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ( ( aClass == null ) ? 0 : aClass.hashCode() );
			result = prime * result + ( ( methodName == null ) ? 0 : methodName.hashCode() );
			result = prime * result + Arrays.hashCode( paramTypes );
			return result;
		}

		@Override
		public boolean equals( Object obj )
		{
			if ( this == obj )
				return true;
			if ( obj == null )
				return false;
			if ( getClass() != obj.getClass() )
				return false;
			DeclaredMethodKey other = (DeclaredMethodKey)obj;
			if ( aClass == null )
			{
				if ( other.aClass != null )
					return false;
			}
			else if ( !aClass.equals( other.aClass ) )
				return false;
			if ( methodName == null )
			{
				if ( other.methodName != null )
					return false;
			}
			else if ( !methodName.equals( other.methodName ) )
				return false;
			if ( !Arrays.equals( paramTypes, other.paramTypes ) )
				return false;
			return true;
		}
	}
	
	private static Map< DeclaredMethodKey, Method > declaredMethods = new ConcurrentHashMap< DeclaredMethodKey, Method >();
	
	private Method getDeclaredMethodRecursive( Class< ? > aClass, String methodName, Class< ? >[] paramTypes )
	{
		Method method = declaredMethods.get( new DeclaredMethodKey( aClass, methodName, paramTypes ) );
		
		if ( method == null )
		{
			method = getDeclaredMethodRecursiveUncached( aClass, methodName, paramTypes );
			
			if ( method != null )
				declaredMethods.put( new DeclaredMethodKey( aClass, methodName, paramTypes ), method );
		}
		
		return method;
	}
	
	private Method getDeclaredMethodRecursiveUncached( Class< ? > aClass, String methodName, Class< ? >[] paramTypes )
	{
		Method method = null;
		
		for ( Method declaredMethod : aClass.getDeclaredMethods() )
		{
			// TODO: Another ugly work-around: as I've already written, we will have to completely
			// rewrite the proxy generator and associated classes soon.
			// Check for aClasses implementing Collection Interface: check also parameter types, 
			// because otherwise wrong method will be randomly taken (e.g. java.util.List.add(int, E) vs. java.util.List.add(E))
			boolean isCollectionType = Collection.class.isAssignableFrom(aClass);
			if ( declaredMethod.getName().equals( methodName ) && ( !isCollectionType || Arrays.deepEquals( declaredMethod.getParameterTypes(), paramTypes )))
			{
				method = declaredMethod;
				break;
			}
		}
		
		if ( method == null && aClass.getSuperclass() != null )
			method = getDeclaredMethodRecursiveUncached( aClass.getSuperclass(), methodName, paramTypes );
		
		return method;
	}

	@Override
	public int hashCode()
	{
		return invokeOnCollection( "hashCode", new Class< ? >[] {}, new Object[] {} );
	}

	@Override
	public boolean equals( Object obj )
	{
		return invokeOnCollection( "equals", new Class< ? >[] { Object.class }, new Object[] { obj } );
	}
}
