package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;

import java.net.URLClassLoader;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class MemberTest
{

	private static TaxonomyLoader taxonomyLoader;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder javaContent = new StringBuilder();
		javaContent.append( "package v.w.x;" );
		javaContent.append( "public class V {" );
		javaContent.append( TestUtility.generateMember( "int", "id" ) );
		javaContent.append( "}" );
		ClassLoader modelClassLoader = InflectionCompilerTestUtility.compileJava( new JavaFileMock[] { new JavaFileMock( "V.java", "v.w.x", javaContent.toString() ) } );

		StringBuilder inflectContent = new StringBuilder();
		inflectContent.append( "package a.b.c;" );
		inflectContent.append( "import v.w.x.*;" );
		inflectContent.append( "taxonomy MemberTestTaxonomy {}" );
		inflectContent.append( "taxonomy MemberTest_GetSelectionTypeTaxonomy extends MemberTestTaxonomy" );
		inflectContent.append( "{" );
		inflectContent.append( "	view V { *; }" );
		inflectContent.append( "}" );

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( modelClassLoader, new InflectionFileMock( "a.b.c", inflectContent.toString() ) );
		InflectionCompiler.compile( job );

		taxonomyLoader = new TaxonomyLoader( TaxonomyLoader.getSystemTaxonomyLoader(), new URLClassLoader( TestUtility.convertToURLArray( job.getTargetDirectory() ), modelClassLoader ) );
	}

	@Test
	public void testGetSelectionType_Include_CorrectType() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.MemberTest_GetSelectionTypeTaxonomy" );
		View view = taxonomy.getView( "v.w.x.V" );
		Member member = view.getDeclaredMember( "id" );
		assertEquals( SelectionType.INCLUDE, member.getSelectionType() );
	}

}
