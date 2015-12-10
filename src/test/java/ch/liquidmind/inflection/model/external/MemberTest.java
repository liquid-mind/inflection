package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class MemberTest extends AbstractInflectionTest
{

	private static JavaFileMock[] javaModel;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder javaContent = new StringBuilder();
		javaContent.append( "package v.w.x;" );
		javaContent.append( "public class V {" );
		javaContent.append( TestUtility.generateMember( "int", "id" ) );
		javaContent.append( "}" );
		javaModel = new JavaFileMock[] { new JavaFileMock( "V.java", "v.w.x", javaContent.toString() ) };
	}

	@Test
	public void testGetSelectionType_Include_CorrectType() throws Exception
	{
		StringBuilder inflectContent = new StringBuilder();
		inflectContent.append( "package a.b.c;" );
		inflectContent.append( "import v.w.x.*;" );
		inflectContent.append( "taxonomy A {}" );
		inflectContent.append( "taxonomy B extends A" );
		inflectContent.append( "{" );
		inflectContent.append( "	view V { *; }" );
		inflectContent.append( "}" );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.B" );
			View view = taxonomy.getView( "v.w.x.V" );
			Member member = view.getDeclaredMember( "id" );
			assertEquals( SelectionType.INCLUDE, member.getSelectionType() );
		} , javaModel, null, createInflectionFileMock( "a.b.c", inflectContent.toString() ) );
	}

}
