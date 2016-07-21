package ch.liquidmind.inflection.proxy.memory;

import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.proxy.memory.TaxonomySpecificMemoryManager.ObjectType;

public class GarbageCollectingMemoryManager extends MemoryManager
{
	@Override
	public < T > T getObject( Taxonomy taxonomy, ObjectType objectType, Object key )
	{
		return null;
	}

	@Override
	protected TaxonomySpecificMemoryManager createTaxonomySpecificMemoryManager( Taxonomy taxonomy )
	{
		return new GarbageCollectingTaxonomySpecificMemoryManager( taxonomy );
	}
}
