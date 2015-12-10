package ch.liquidmind.inflection.proxy;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;

public class ProxyGeneratorTest
{

	private static File compiledTaxonomyDir;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append( "package a.b.c;" );
		builder.append( "import ch.liquidmind.inflection.test.model.*;" );

		builder.append( "taxonomy ProxyGeneratorSuperTaxonomy" );
		builder.append( "{" );
		builder.append( "	view A { *; }" );
		builder.append( "}" );

		builder.append( "taxonomy ProxyGeneratorChildTaxonomy extends ProxyGeneratorSuperTaxonomy" );
		builder.append( "{" );
		builder.append( "	view * { *; }" );
		builder.append( "}" );

		compiledTaxonomyDir = InflectionCompilerTestUtility.compileInflection( new InflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGenerate() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, "a.b.c", "ProxyGeneratorSuperTaxonomy" );
		ProxyGenerator generator = new ProxyGenerator( Files.createTempDir(), taxonomy );
		generator.generateTaxonomy();
	}

}
