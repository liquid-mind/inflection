package ch.liquidmind.inflection;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import __java.lang.__Class;
import __java.lang.reflect.__Field;
import ch.liquidmind.inflection.exception.ExceptionWrapper;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.print.InflectionPrinter;
import ch.liquidmind.inflection.proxy.Proxy;
import ch.liquidmind.inflection.proxy.memory.ManualMemoryManager;
import ch.liquidmind.inflection.proxy.memory.TaxonomySpecificMemoryManager;
import ch.liquidmind.inflection.proxy.memory.TaxonomySpecificMemoryManager.ObjectType;

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
	
	public static < T > T cast( Taxonomy taxonomy, Class< T > theClass, Object object )
	{
		ObjectType targetObjectType = determineObjectType( theClass );
		T targetObject = ManualMemoryManager.getContextMemoryManager().getObject( taxonomy, targetObjectType, object );
		
		return targetObject;
	}
	
	public static < T > T cast( Class< T > theClass, Object object )
	{
		Taxonomy targetTaxonomy = determineTaxonomy( theClass );
		Taxonomy sourceTaxonomy = determineTaxonomy( object );
		Taxonomy taxonomy = ( targetTaxonomy == null ? sourceTaxonomy : targetTaxonomy );
		
		if ( taxonomy == null )
			throw new IllegalStateException( "taxonomy should never be null." );
		
		return cast( taxonomy, theClass, object );
	}
	
	private static Taxonomy determineTaxonomy( Object object )
	{
		Taxonomy taxonomy;

		if ( Auxiliary.class.isAssignableFrom( object.getClass() ) )
			taxonomy = (Taxonomy)__Field.get( TaxonomySpecificMemoryManager.AUXILIARY_TAXONOMY, object );
		else
			taxonomy = determineTaxonomy( object.getClass() );
		
		return taxonomy;
	}
	
	@SuppressWarnings( "unchecked" )
	private static Taxonomy determineTaxonomy( Class< ? > aClass )
	{
		Taxonomy taxonomy;
		
		if ( Proxy.class.isAssignableFrom( aClass ) )
			taxonomy = getTaxonomy( (Class< Proxy >)aClass );
		else
			taxonomy = null;
		
		return taxonomy;
	}

	private static ObjectType determineObjectType( Class< ? > aClass )
	{
		ObjectType objectType;
		
		if ( Proxy.class.isAssignableFrom( aClass ) )
			objectType = ObjectType.Proxy;
		else if ( Auxiliary.class.isAssignableFrom( aClass ) )
			objectType = ObjectType.Auxiliary;
		else
			objectType = ObjectType.Object;
		
		return objectType;
	}
	
	public static < T extends Object > T cast( String taxonomy, Object object )
	{
		return cast( TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( taxonomy ), ObjectType.Proxy, object );
	}
	
	public static < T extends Object > T cast( Taxonomy taxonomy, Object object )
	{
		return cast( taxonomy, ObjectType.Proxy, object );
	}
	
	public static < T extends Object > T cast( Taxonomy taxonomy, ObjectType objectType, Object object )
	{
		return ManualMemoryManager.getContextMemoryManager().getObject( taxonomy, objectType, object );
	}
	
	public static < T extends Object > T cast( Proxy proxy )
	{
		return cast( ObjectType.Object, proxy );
	}
	
	public static < T extends Object > T cast( ObjectType objectType, Proxy proxy )
	{
		return ManualMemoryManager.getContextMemoryManager().getObject( getTaxonomy( proxy ), objectType, proxy );
	}

	public static String viewToString( Proxy proxy )
	{
		StringWriter stringWriter = new StringWriter();
		InflectionPrinter.printView( getTaxonomy( proxy ), getView( proxy ), stringWriter, true, false );
		String s = stringWriter.toString();
		
		return s;
	}
	
	public static String toJson( Proxy proxy )
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		String s = ExceptionWrapper.ObjectWriter_writeValueAsString( writer, proxy );
		
		return s;
	}
}
