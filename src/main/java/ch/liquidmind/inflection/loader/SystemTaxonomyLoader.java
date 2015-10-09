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
	public static final String  JAVA_LANG = "java.lang";
	public static final String  CH_LIQUIDMIND_INFLECTION = "ch.liquidmind.inflection";
	public static final String BASETAXONOMY = CH_LIQUIDMIND_INFLECTION + ".BaseTaxonomy";
	
	private static TaxonomyCompiled baseTaxonomy;
	
	static
	{
		baseTaxonomy = new TaxonomyCompiled( BASETAXONOMY );
		baseTaxonomy.setDefaultAccessType( AccessType.Property );
		
		List< ViewCompiled > viewsCompiled = baseTaxonomy.getViewsCompiled();
		
		// Basic types
		viewsCompiled.add( createViewCompiled( byte.class.getName(), baseTaxonomy ) );
		viewsCompiled.add( createViewCompiled( short.class.getName(), baseTaxonomy ) );
		viewsCompiled.add( createViewCompiled( int.class.getName(), baseTaxonomy ) );
		viewsCompiled.add( createViewCompiled( long.class.getName(), baseTaxonomy ) );
		viewsCompiled.add( createViewCompiled( float.class.getName(), baseTaxonomy ) );
		viewsCompiled.add( createViewCompiled( double.class.getName(), baseTaxonomy ) );
		viewsCompiled.add( createViewCompiled( boolean.class.getName(), baseTaxonomy ) );
		viewsCompiled.add( createViewCompiled( char.class.getName(), baseTaxonomy ) );

		// Other common (terminal) types
		viewsCompiled.add( createViewCompiled( String.class.getName(), baseTaxonomy ) );
		viewsCompiled.add( createViewCompiled( Date.class.getName(), baseTaxonomy ) );
	}
	
	private static ViewCompiled createViewCompiled( String name, TaxonomyCompiled parentTaxonomyCompiled )
	{
		ViewCompiled viewCompiled = new ViewCompiled( name );
		viewCompiled.setSelectionType( SelectionType.Include );
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
		
		if ( name.equals( BASETAXONOMY ) )
			foundTaxonomy = defineTaxonomy( baseTaxonomy );
		
		return foundTaxonomy;
	}
}
