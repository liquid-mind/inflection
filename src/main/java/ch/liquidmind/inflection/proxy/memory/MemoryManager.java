package ch.liquidmind.inflection.proxy.memory;

import java.util.HashMap;
import java.util.Map;

import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.proxy.memory.TaxonomySpecificMemoryManager.ObjectType;

public abstract class MemoryManager
{
	private static ThreadLocal< MemoryManager > contextMemoryManager = new ThreadLocal< MemoryManager >();
	
	private Map< Taxonomy, TaxonomySpecificMemoryManager > taxonomySpecificMemoryManagers = new HashMap< Taxonomy, TaxonomySpecificMemoryManager >();

	@SuppressWarnings( "unchecked" )
	public static < T extends MemoryManager > T getContextProxyRegistry()
	{
		if ( contextMemoryManager.get() == null )
			contextMemoryManager.set( createMemoryManager() );
		
		return (T)contextMemoryManager.get();
	}
	
	private static MemoryManager createMemoryManager()
	{
		return ( MemoryManagementAgent.isAgentActive() ? new GarbageCollectingMemoryManager() : new ManualMemoryManager() );
	}
	
	private TaxonomySpecificMemoryManager getTaxonomySpecificMemoryManager( Taxonomy taxonomy )
	{
		TaxonomySpecificMemoryManager taxonomySpecificManager = taxonomySpecificMemoryManagers.get( taxonomy );
		
		if ( taxonomySpecificManager == null )
		{
			taxonomySpecificManager = createTaxonomySpecificMemoryManager( taxonomy );
			taxonomySpecificMemoryManagers.put( taxonomy, taxonomySpecificManager );
		}
		
		return taxonomySpecificManager;
	}
	
	protected abstract TaxonomySpecificMemoryManager createTaxonomySpecificMemoryManager( Taxonomy taxonomy );
	
	public < T > T getObject( Taxonomy taxonomy, ObjectType objectType, Object key )
	{
		T targetObject;
		
		if ( key == null )
		{
			targetObject = null;
		}
		else
		{
			TaxonomySpecificMemoryManager taxonomySpecificMemoryManager = getTaxonomySpecificMemoryManager( taxonomy );
			targetObject = taxonomySpecificMemoryManager.getObject( objectType, key );
		}
		
		return (T)targetObject;
	}
	

	public Map< Taxonomy, TaxonomySpecificMemoryManager > getTaxonomySpecificMemoryManagers()
	{
		return taxonomySpecificMemoryManagers;
	}
}
