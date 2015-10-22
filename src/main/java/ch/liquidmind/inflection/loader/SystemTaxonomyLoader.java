package ch.liquidmind.inflection.loader;

import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;
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
