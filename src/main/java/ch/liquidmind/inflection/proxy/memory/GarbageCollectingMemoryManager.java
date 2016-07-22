package ch.liquidmind.inflection.proxy.memory;

import ch.liquidmind.inflection.model.external.Taxonomy;

public class GarbageCollectingMemoryManager extends MemoryManager
{
	@Override
	protected TaxonomySpecificMemoryManager createTaxonomySpecificMemoryManager( Taxonomy taxonomy )
	{
		return new GarbageCollectingTaxonomySpecificMemoryManager( taxonomy );
	}
}
