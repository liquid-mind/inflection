package ch.liquidmind.inflection.proxy;

import java.lang.reflect.Method;

import __java.lang.__Class;
import __java.lang.__NoSuchMethodException;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

public class Proxy
{
	// Note that the view's taxonomy (View.getTaxonomy()) may be distinct from
	// this taxonomy.
	private Taxonomy taxonomy;
	private View view;

	// Constructor for collection proxies.
	protected Proxy( String taxonomyName )
	{
		this( taxonomyName, null );
	}
	
	// Constructor for normal proxies.
	protected Proxy( String taxonomyName, String viewName )
	{
		super();
        taxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( taxonomyName );
        this.view = taxonomy.getView( viewName );
	}

	Taxonomy getTaxonomy()
	{
		return taxonomy;
	}

	View getView()
	{
		return view;
	}

	protected Method getMethod( String name, Class< ? >[] paramTypes )
	{
		return __Class.getDeclaredMethod( this.getClass(), name, paramTypes );
	}
	
	@SuppressWarnings( "unchecked" )
	protected < T extends Object > T invoke( String methodName, Class< ? >[] paramTypes, Object[] params ) throws Throwable
	{
		Method method = getDeclaredMethodRecursive( view.getViewedClass(), methodName, paramTypes );
		
		if ( method == null )
		{
			for ( Class< ? > usedClass : view.getUsedClasses() )
			{
				for ( Method usedMethod : usedClass.getMethods() )
				{
					// TODO: This is a very ugly work-around: we really should be looking for the method
					// by its full signature, not just by the name (which can be overloaded). I'm
					// only leaving this in for now because the proxy generator and associated classes
					// need to be completely rewritten anyway.
					if ( usedMethod.getName().equals( methodName ) )
					{
						method = usedMethod;
						break;
					}
				}
				
				if ( method != null )
					break;
			}
		}
		
		if ( method == null )
			throw new IllegalStateException( "Unable to resolve method: " + methodName );
		
		Object retVal = ProxyHandler.getContextProxyHandler().invoke( this, method, params );
		
		return (T)retVal;
	}
	
	// TODO: refactor this and the above method and/or the entire class to fit
	// better with the two distinct cases: (non-collection) object proxy and collection proxy
	@SuppressWarnings( "unchecked" )
	protected < T extends Object > T invokeOnCollection( String methodName, Class< ? >[] paramTypes, Object[] params ) throws Throwable
	{
		Method method = getDeclaredMethodRecursive( this.getClass(), methodName, paramTypes );
		Object retVal = CollectionProxyHandler.getContextCollectionProxyHandler().invoke( this, method, params );
		
		return (T)retVal;
	}
	
	private Method getDeclaredMethodRecursive( Class< ? > aClass, String methodName, Class< ? >[] paramTypes )
	{
		Method method = null;
		
		for ( Method declaredMethod : aClass.getDeclaredMethods() )
		{
			// TODO: Another ugly work-around: as I've already written, we will have to completely
			// rewrite the proxy generator and associated classes soon.
			if ( declaredMethod.getName().equals( methodName ) )
			{
				method = declaredMethod;
				break;
			}
		}
		
		if ( method == null && aClass.getSuperclass() != null )
			method = getDeclaredMethodRecursive( aClass.getSuperclass(), methodName, paramTypes );
		
		return method;
	}
}
