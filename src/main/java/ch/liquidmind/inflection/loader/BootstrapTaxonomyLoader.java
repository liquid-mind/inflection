package ch.liquidmind.inflection.loader;

import java.security.SecureClassLoader;

import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;
import ch.liquidmind.inflection.model.external.Taxonomy;

public class BootstrapTaxonomyLoader extends TaxonomyLoader
{
	public static final String CH_LIQUIDMIND_INFLECTION = "ch.liquidmind.inflection";
	public static final String TAXONOMY = CH_LIQUIDMIND_INFLECTION + ".Taxonomy";
	
	private static TaxonomyCompiled taxonomy;
	
	static
	{
		taxonomy = new TaxonomyCompiled( TAXONOMY );
		taxonomy.setDefaultAccessType( AccessType.PROPERTY );
	}
	
	private static class VirtualBootstrapClassLoader extends SecureClassLoader
	{
		public VirtualBootstrapClassLoader()
		{
			super( null );
		}
	}
	
	BootstrapTaxonomyLoader()
	{
		super( null, new VirtualBootstrapClassLoader() );
	}
	
	@Override
	public Taxonomy findTaxonomy( String name )
	{
		Taxonomy foundTaxonomy = null;
		
		if ( name.equals( TAXONOMY ) )
			foundTaxonomy = defineTaxonomy( taxonomy );
		else
			foundTaxonomy = super.findTaxonomy( name );
		
		return foundTaxonomy;
	}
}
