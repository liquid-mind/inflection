package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
		StringBuilder javaSuperClass = new StringBuilder();
		javaSuperClass.append( "package v.w.x;" );
		javaSuperClass.append( "public class V {" );
		javaSuperClass.append( TestUtility.generateMember( "int", "id" ) );
		javaSuperClass.append( "}" );
		JavaFileMock javaSuperClassFileMock = new JavaFileMock( "V.java", "v.w.x", javaSuperClass.toString() );

		StringBuilder javaChildClass = new StringBuilder();
		javaChildClass.append( "package v.w.x;" );
		javaChildClass.append( "public class W extends V {" );
		javaChildClass.append( TestUtility.generateMember( "long", "longMember" ) );
		javaChildClass.append( "}" );
		JavaFileMock javaChildClassFileMock = new JavaFileMock( "W.java", "v.w.x", javaChildClass.toString() );

		javaModel = new JavaFileMock[] { javaSuperClassFileMock, javaChildClassFileMock };
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
	
	@Test
	public void testGetMember_DeclaredMember_MemberExists() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A {" );
		builder.append( "	view V { *; }" );
		builder.append( "}" );
		builder.append( "taxonomy B extends A" );
		builder.append( "{" );
		builder.append( "	view W { *; }	" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.B" );
			View view = taxonomy.getView( "v.w.x.W" );
			assertNotNull( view.getMember( "longMember" ) );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetDeclaredMember_DeclaredMember_MemberExists() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A {" );
		builder.append( "	view V { *; }" );
		builder.append( "}" );
		builder.append( "taxonomy B extends A" );
		builder.append( "{" );
		builder.append( "	view W { *; }	" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.B" );
			View view = taxonomy.getView( "v.w.x.W" );
			assertNotNull( view.getDeclaredMember( "longMember" ) );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetMember_InheritedMember_MemberExists() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A {" );
		builder.append( "	view V { *; }" );
		builder.append( "}" );
		builder.append( "taxonomy B extends A {}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.B" );
			View view = taxonomy.getView( "v.w.x.V" );
			assertNotNull( view.getMember( "id" ) );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}	

}
