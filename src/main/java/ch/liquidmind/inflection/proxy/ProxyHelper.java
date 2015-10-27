package ch.liquidmind.inflection.proxy;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;

public class ProxyHelper
{
	public static < T extends Proxy > T getProxy( String taxonomyName, Object viewableObject )
	{
		Taxonomy fullTaxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( taxonomyName );
		T proxy = ProxyRegistry.getContextProxyRegistry().getProxy( fullTaxonomy, viewableObject );
		
		return proxy;
	}
}
