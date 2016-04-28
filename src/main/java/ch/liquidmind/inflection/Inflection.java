package ch.liquidmind.inflection;

import java.lang.reflect.Method;

import __java.lang.__Class;
import __java.lang.reflect.__Method;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.proxy.Proxy;
import ch.liquidmind.inflection.proxy.ProxyRegistry;

@SuppressWarnings( "unchecked" )
public class Inflection
{
	private static Method GET_TAXONOMY = __Class.getDeclaredMethod( Proxy.class, "getTaxonomy", new Class[] {} );
	private static Method GET_VIEW = __Class.getDeclaredMethod( Proxy.class, "getView", new Class[] {} );
	
	static
	{
		GET_TAXONOMY.setAccessible( true );
		GET_VIEW.setAccessible( true );
	}
	
	public static Taxonomy getTaxonomy( Proxy proxy )
	{
		return (Taxonomy)__Method.invoke( GET_TAXONOMY, proxy, new Object[] {} );
	}

	public static View getView( Proxy proxy )
	{
		return (View)__Method.invoke( GET_VIEW, proxy, new Object[] {} );
	}

	public static < T extends Proxy > T cast( String taxonomyName, Object viewableObject )
	{
		Taxonomy taxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( taxonomyName );
		T proxy = cast( taxonomy, viewableObject );
		
		return proxy;
	}

	public static < T extends Proxy > T cast( Taxonomy taxonomy, Object viewableObject )
	{
		T proxy = ProxyRegistry.getContextProxyRegistry().getProxy( taxonomy, viewableObject );
		
		return proxy;
	}
	
	public static < T extends Object > T cast( Proxy proxy )
	{
		return ProxyRegistry.getContextProxyRegistry().getObject( proxy );
	}
}
