package ch.liquidmind.inflection.proxy;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;
import ch.liquidmind.inflection.test.InflectionFileMock;

public class ProxyGeneratorTest
{

	private static File compiledTaxonomyDir;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append( "package ch.liquidmind.inflection.proxy;" );
		builder.append( "import ch.liquidmind.inflection.test.model.*;" );
		builder.append( "taxonomy ProxyGeneratorSuperTaxonomy" );
		builder.append( "{" );
		builder.append( "	view A { *; }" );
		builder.append( "}" );

		builder.append( "taxonomy ProxyGeneratorChildTaxonomy extends ProxyGeneratorSuperTaxonomy" );
		builder.append( "{" );
		builder.append( "	view * { *; }" );
		builder.append( "}" );

		compiledTaxonomyDir = InflectionCompilerTestUtility.compileInflection( new InflectionFileMock( "ch.liquidmind.inflection.proxy", builder.toString() ) );
	}

	@Test
	public void testGenerate() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "ProxyGeneratorSuperTaxonomy" );
		ProxyGenerator generator = new ProxyGenerator( Files.createTempDir(), taxonomy );
		generator.generateTaxonomy();
	}

}
