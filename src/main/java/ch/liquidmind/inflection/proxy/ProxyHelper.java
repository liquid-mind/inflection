package ch.liquidmind.inflection.proxy;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

public class ProxyHelper
{
	public static Taxonomy getTaxonomy( Proxy proxy )
	{
		return proxy.getTaxonomy();
	}

	public static View getView( Proxy proxy )
	{
		return proxy.getView();
	}

	public static < T extends Proxy > T getProxy( String taxonomyName, Object viewableObject )
	{
		Taxonomy taxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( taxonomyName );
		T proxy = getProxy( taxonomy, viewableObject );
		
		return proxy;
	}

	public static < T extends Proxy > T getProxy( Taxonomy taxonomy, Object viewableObject )
	{
		T proxy = ProxyRegistry.getContextProxyRegistry().getProxy( taxonomy, viewableObject );
		
		return proxy;
	}
	
	public static < T extends Object > T getObject( Proxy proxy )
	{
		return ProxyRegistry.getContextProxyRegistry().getObject( proxy );
	}
}
