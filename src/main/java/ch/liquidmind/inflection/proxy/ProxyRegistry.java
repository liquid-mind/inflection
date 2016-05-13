package ch.liquidmind.inflection.proxy;

import java.util.HashMap;
import java.util.Map;

import ch.liquidmind.inflection.Inflection;
import ch.liquidmind.inflection.model.external.Taxonomy;

// TODO: look into using weak-/soft references to manage memory.
public class ProxyRegistry
{
	
	private static ThreadLocal< ProxyRegistry > contextProxyRegistry = new ThreadLocal< ProxyRegistry >();
	
	private Map< Taxonomy, Tuples > tuplesByTaxonomy = new HashMap< Taxonomy, Tuples >();

	public static ProxyRegistry getContextProxyRegistry()
	{
		if ( contextProxyRegistry.get() == null )
			contextProxyRegistry.set( new ProxyRegistry() );
		
		return contextProxyRegistry.get();
	}
	
	public < T > T getObject( Class< T > targetClass, Proxy key )
	{
		return getObject( targetClass, Inflection.getTaxonomy( key ), key );
	}
	
	public < T > T getObject( Class< T > targetClass, Taxonomy taxonomy, Object key )
	{
		Tuples tuples = getTuples( taxonomy );
		T targetObject = tuples.getObject( targetClass, key );
		
		return (T)targetObject;
	}
	
	private Tuples getTuples( Taxonomy taxonomy )
	{
		Tuples tuples = tuplesByTaxonomy.get( taxonomy );
		
		if ( tuples == null )
		{
			tuples = new Tuples( taxonomy );
			tuplesByTaxonomy.put( taxonomy, tuples );
		}
		
		return tuples;
	}	
}
