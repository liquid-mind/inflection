package ch.liquidmind.inflection.proxy;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class ProxyGeneratorTest extends AbstractInflectionTest
{

	private static JavaFileMock[] javaModel;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder javaContent = new StringBuilder();
		javaContent.append( "package v.w.x;" );
		javaContent.append( "public class V { private int id; public int getId() { return id; }; }" );
		JavaFileMock javaFileMock = new JavaFileMock( "V.java", "v.w.x", javaContent.toString() );
		javaModel = new JavaFileMock[] { javaFileMock };
	}

	@Test
	public void testGenerate_ProxyJavaFile_FileExists() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c;" );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A" );
		builder.append( "{" );
		builder.append( "	view V { *; }" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			File targetDirectory = Files.createTempDir();
			ProxyGenerator generator = new ProxyGenerator( targetDirectory, taxonomy );
			generator.generateTaxonomy();
			String epectedSubPath = "a" + File.separatorChar + "b" + File.separatorChar + "c" + File.separatorChar + "A" + File.separatorChar + "v" + File.separatorChar + "w" + File.separatorChar + "x" + File.separatorChar + "A_V.java";
			assertTrue( "Proxy file generated in directory " + targetDirectory.getAbsolutePath(), new File( targetDirectory, epectedSubPath ).exists() );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

}
