package ch.liquidmind.inflection.model.external.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.test.TestUtility;

public final class TaxonomyTestUtility
{

	public static Taxonomy getTestTaxonomy( File compiledTaxonomyDir, String packageName, String taxonomyName ) throws IOException 
	{
		URL[] compiledTaxonomyDirs = TestUtility.convertToURLArray( compiledTaxonomyDir );
		URLClassLoader taxonomyClassLoader = new URLClassLoader( compiledTaxonomyDirs , ClassLoader.getSystemClassLoader() );
		TaxonomyLoader taxonomyLoader = new TaxonomyLoader( TaxonomyLoader.getSystemTaxonomyLoader(), taxonomyClassLoader );
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( packageName + "." + taxonomyName );
		taxonomyClassLoader.close();
		return taxonomy;
	}
	
	

}
