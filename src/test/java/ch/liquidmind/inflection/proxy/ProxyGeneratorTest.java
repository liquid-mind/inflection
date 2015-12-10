package ch.liquidmind.inflection.proxy;

import java.net.URLClassLoader;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class ProxyGeneratorTest
{

	private static TaxonomyLoader taxonomyLoader;
	private static URLClassLoader modelClassLoader;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder javaContent = new StringBuilder();
		javaContent.append( "package v.w.x;" );
		javaContent.append( "public class V { private int id; public int getId() { return id; }; }" );
		JavaFileMock javaFileMock = new JavaFileMock( "V.java", "v.w.x", javaContent.toString() );
		modelClassLoader = InflectionCompilerTestUtility.compileJava( new JavaFileMock[] { javaFileMock } );
		
		
		StringBuilder builder = new StringBuilder();

		builder.append( "package a.b.c;" );
		builder.append( "import v.w.x.*;" );

		builder.append( "taxonomy ProxyGeneratorSuperTaxonomy" );
		builder.append( "{" );
		builder.append( "	view V { *; }" );
		builder.append( "}" );

		builder.append( "taxonomy ProxyGeneratorChildTaxonomy extends ProxyGeneratorSuperTaxonomy" );
		builder.append( "{" );
		builder.append( "	view * { *; }" );
		builder.append( "}" );

		taxonomyLoader = InflectionCompilerTestUtility.compileInflection( modelClassLoader, new InflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGenerate() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.ProxyGeneratorSuperTaxonomy" );
		ProxyGenerator generator = new ProxyGenerator( Files.createTempDir(), taxonomy );
		generator.generateTaxonomy();
	}

}
