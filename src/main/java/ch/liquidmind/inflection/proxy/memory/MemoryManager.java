package ch.liquidmind.inflection.proxy.memory;

import java.util.HashMap;
import java.util.Map;

import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.proxy.memory.TaxonomySpecificMemoryManager.ObjectType;

public abstract class MemoryManager
{
	public enum MemoryManagementStrategy { AUTOMATIC, MANUAL, GARBAGE_COLLECTING }
	
	private static MemoryManagementStrategy memoryManagementStrategy = MemoryManagementStrategy.AUTOMATIC;
	private static ThreadLocal< MemoryManager > contextMemoryManager = new ThreadLocal< MemoryManager >();
	
	private Map< Taxonomy, TaxonomySpecificMemoryManager > taxonomySpecificMemoryManagers = new HashMap< Taxonomy, TaxonomySpecificMemoryManager >();

	public static void setMemoryManagementStrategy( MemoryManagementStrategy memoryManagementStrategy )
	{
		MemoryManager.memoryManagementStrategy = memoryManagementStrategy;
	}
	
	@SuppressWarnings( "unchecked" )
	public static < T extends MemoryManager > T getContextMemoryManager()
	{
		if ( contextMemoryManager.get() == null )
			contextMemoryManager.set( createMemoryManager() );
		
		return (T)contextMemoryManager.get();
	}
	
	private static MemoryManager createMemoryManager()
	{
		MemoryManager memoryManager;
		
		if ( memoryManagementStrategy.equals( MemoryManagementStrategy.AUTOMATIC ) )
			memoryManager = ( MemoryManagementAgent.isAgentActive() ? new GarbageCollectingMemoryManager() : new ManualMemoryManager() );
		else if ( memoryManagementStrategy.equals( MemoryManagementStrategy.MANUAL ) )
			memoryManager = new ManualMemoryManager();
		else if ( memoryManagementStrategy.equals( MemoryManagementStrategy.GARBAGE_COLLECTING ) )
			if ( MemoryManagementAgent.isAgentActive() )
				memoryManager = new GarbageCollectingMemoryManager();
			else
				throw new RuntimeException( String.format( "The value of memoryManagementStrategy was %s but %s.isAgentActive() returned false.", memoryManagementStrategy, MemoryManagementAgent.class.getSimpleName() ) );
		else
			throw new IllegalStateException( "Unexpected value for memoryManagementStrategy: " + memoryManagementStrategy );
		
		return memoryManager;
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
