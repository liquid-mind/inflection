package ch.liquidmind.inflection.proxy;

import java.util.HashMap;
import java.util.Map;

import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.proxy.Tuples.ObjectType;

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
	
	public < T > T getObject( Taxonomy taxonomy, ObjectType objectType, Object key )
	{
		T targetObject;
		
		if ( key == null )
		{
			targetObject = null;
		}
		else
		{
			Tuples tuples = getTuples( taxonomy );
			targetObject = tuples.getObject( objectType, key );
		}
		
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
