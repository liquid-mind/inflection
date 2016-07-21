package ch.liquidmind.inflection.proxy.memory;

import ch.liquidmind.inflection.model.external.Taxonomy;

public class ManualMemoryManager extends MemoryManager
{
	@Override
	protected TaxonomySpecificMemoryManager createTaxonomySpecificMemoryManager( Taxonomy taxonomy )
	{
		return new ManualTaxonomySpecificMemoryManager( taxonomy );
	}

	public void clear()
	{
		getTaxonomySpecificMemoryManagers().clear();
	}
}
