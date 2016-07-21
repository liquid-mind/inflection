package ch.liquidmind.inflection.proxy.memory;

import ch.liquidmind.inflection.loader.TaxonomyLoader;

public class AgentTaxonomyLoader extends TaxonomyLoader
{
	public AgentTaxonomyLoader()
	{
		super( getExtensionsTaxonomyLoader(), new AgentClassLoader() );
	}
}
