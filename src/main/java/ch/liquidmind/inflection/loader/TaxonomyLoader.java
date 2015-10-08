package ch.liquidmind.inflection.loader;

import java.net.URL;
import java.net.URLClassLoader;

import ch.liquidmind.inflection.model.external.Taxonomy;

public class TaxonomyLoader extends URLClassLoader
{
	public TaxonomyLoader( URL[] urls )
	{
		super( urls );
	}

	public static TaxonomyLoader getContextTaxonomyLoader()
	{
		return null;
	}
	
	public static TaxonomyLoader getSystemTaxonomyLoader()
	{
		return null;
	}

	protected Taxonomy findTaxonomy( String name )
	{
		throw new UnsupportedOperationException();
	}

	public Taxonomy loadTaxonomy( String name )
	{
		throw new UnsupportedOperationException();
	}

	protected Taxonomy loadTaxonomy( String name, boolean resolve )
	{
		throw new UnsupportedOperationException();
	}
	
	protected Taxonomy defineTaxonomy( String name, byte[] b, int off, int len )
	{
		throw new UnsupportedOperationException();
	}
}
