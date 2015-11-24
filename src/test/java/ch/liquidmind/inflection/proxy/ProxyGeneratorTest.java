package ch.liquidmind.inflection.proxy;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;

public class ProxyGeneratorTest
{
	
	private static File compiledTaxonomyDir;
	
	@BeforeClass
	public static void beforeClass() throws Exception
	{
		compiledTaxonomyDir = InflectionCompilerTestUtility.compileInflectionFile( ProxyGeneratorTest.class, "ProxyGeneratorTest.inflect" );
	}

	@Test
	public void testGenerate() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "ProxyGeneratorSuperTaxonomy" );
		ProxyGenerator generator = new ProxyGenerator( Files.createTempDir(), taxonomy );
		generator.generateTaxonomy();
	}

}
