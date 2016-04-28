package ch.liquidmind.inflection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import __java.lang.__Class;
import __java.lang.reflect.__Field;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.proxy.Proxy;
import ch.liquidmind.inflection.proxy.ProxyRegistry;

public class Inflection
{
	private static Map< Class< ? >, Taxonomy > taxonomyCache = new HashMap< Class< ? >, Taxonomy >();
	private static Map< Class< ? >, View > viewCache = new HashMap< Class< ? >, View >();
	
	public static Taxonomy getTaxonomy( Class< ? extends Proxy > proxyClass )
	{
		Taxonomy taxonomy = taxonomyCache.get( proxyClass );
		
		if ( taxonomy == null )
		{
			Field field = __Class.getDeclaredField( proxyClass, "TAXONOMY" );
			field.setAccessible( true );
			taxonomy = (Taxonomy)__Field.get( field, null );
			taxonomyCache.put( proxyClass, taxonomy );
		}
		
		return taxonomy;
	}
	
	public static View getView( Class< ? extends Proxy > proxyClass )
	{
		View view = viewCache.get( proxyClass );
		
		if ( view == null )
		{
			Field field = __Class.getDeclaredField( proxyClass, "VIEW" );
			field.setAccessible( true );
			view = (View)__Field.get( field, null );
			viewCache.put( proxyClass, view );
		}
		
		return view;
	}
	
	public static Taxonomy getTaxonomy( Proxy proxy )
	{
		return getTaxonomy( proxy.getClass() );
	}

	public static View getView( Proxy proxy )
	{
		return getView( proxy.getClass() );
	}

	public static < T extends Proxy > T cast( Class< ? extends Proxy > proxyClass, Object viewableObject )
	{
		return cast( getTaxonomy( proxyClass ), viewableObject );
	}

	public static < T extends Proxy > T cast( String taxonomyName, Object viewableObject )
	{
		return cast( TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( taxonomyName ), viewableObject );
	}

	public static < T extends Proxy > T cast( Taxonomy taxonomy, Object viewableObject )
	{
		return ProxyRegistry.getContextProxyRegistry().getProxy( taxonomy, viewableObject );
	}

	public static < T extends Proxy > T cast( Class< ? extends Proxy > proxyClass, Proxy proxy )
	{
		return cast( getTaxonomy( proxyClass ), proxy );
	}

	public static < T extends Proxy > T cast( String taxonomyName, Proxy proxy )
	{
		return cast( TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( taxonomyName ), proxy );
	}

	public static < T extends Proxy > T cast( Taxonomy taxonomy, Proxy proxy )
	{
		return cast( taxonomy, cast( proxy ) );
	}
	
	public static < T extends Object > T cast( Proxy proxy )
	{
		return ProxyRegistry.getContextProxyRegistry().getObject( proxy );
	}
}
