package ch.liquidmind.inflection.model.external.util;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.test.TestUtility;

public final class TaxonomyTestUtility
{

	public static Taxonomy getTestTaxonomy( File compiledTaxonomyDirectory, String packageName, String taxonomyName ) throws IOException
	{
		TaxonomyLoader taxonomyLoader = createTaxonomyLoader( ClassLoader.getSystemClassLoader(), compiledTaxonomyDirectory );
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( packageName + "." + taxonomyName );
		return taxonomy;
	}

	public static TaxonomyLoader createTaxonomyLoader( ClassLoader classLoader, File... compiledTaxonomyDirectories )
	{
		URLClassLoader taxonomyClassLoader = new URLClassLoader( TestUtility.convertToURLArray( compiledTaxonomyDirectories ), classLoader );
		TaxonomyLoader taxonomyLoader = new TaxonomyLoader( TaxonomyLoader.getSystemTaxonomyLoader(), taxonomyClassLoader );
		return taxonomyLoader;
	}

}
