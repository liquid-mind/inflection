package ch.liquidmind.inflection.loader;

import java.util.Date;
import java.util.List;

import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;
import ch.liquidmind.inflection.model.compiled.ViewCompiled;
import ch.liquidmind.inflection.model.external.Taxonomy;

public class SystemTaxonomyLoader extends TaxonomyLoader
{
	public static final String JAVA_LANG = "java.lang";
	public static final String CH_LIQUIDMIND_INFLECTION = "ch.liquidmind.inflection";
	public static final String TAXONOMY = CH_LIQUIDMIND_INFLECTION + ".Taxonomy";
	
	private static TaxonomyCompiled taxonomy;
	
	static
	{
		taxonomy = new TaxonomyCompiled( TAXONOMY );
		taxonomy.setDefaultAccessType( AccessType.PROPERTY );
		
		List< ViewCompiled > viewsCompiled = taxonomy.getViewsCompiled();
		
		// Basic types
		viewsCompiled.add( createViewCompiled( byte.class.getName(), taxonomy ) );
		viewsCompiled.add( createViewCompiled( short.class.getName(), taxonomy ) );
		viewsCompiled.add( createViewCompiled( int.class.getName(), taxonomy ) );
		viewsCompiled.add( createViewCompiled( long.class.getName(), taxonomy ) );
		viewsCompiled.add( createViewCompiled( float.class.getName(), taxonomy ) );
		viewsCompiled.add( createViewCompiled( double.class.getName(), taxonomy ) );
		viewsCompiled.add( createViewCompiled( boolean.class.getName(), taxonomy ) );
		viewsCompiled.add( createViewCompiled( char.class.getName(), taxonomy ) );

		// Other common (terminal) types
		viewsCompiled.add( createViewCompiled( String.class.getName(), taxonomy ) );
		viewsCompiled.add( createViewCompiled( Date.class.getName(), taxonomy ) );
	}
	
	private static ViewCompiled createViewCompiled( String name, TaxonomyCompiled parentTaxonomyCompiled )
	{
		ViewCompiled viewCompiled = new ViewCompiled( name );
		viewCompiled.setSelectionType( SelectionType.INCLUDE );
		viewCompiled.setParentTaxonomyCompiled( parentTaxonomyCompiled );
		
		return viewCompiled;
	}
	
	public SystemTaxonomyLoader()
	{
		super( null, Thread.currentThread().getContextClassLoader() );
	}

	public SystemTaxonomyLoader( TaxonomyLoader parentTaxonomyLoader, ClassLoader classLoader )
	{
		super( parentTaxonomyLoader, classLoader );
	}
	
	@Override
	public Taxonomy findTaxonomy( String name )
	{
		Taxonomy foundTaxonomy = null;
		
		if ( name.equals( TAXONOMY ) )
			foundTaxonomy = defineTaxonomy( taxonomy );
		
		return foundTaxonomy;
	}
}
