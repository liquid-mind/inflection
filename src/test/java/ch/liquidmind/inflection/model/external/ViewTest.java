package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class ViewTest extends AbstractInflectionTest
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
		JavaFileMock javaFileMock = new JavaFileMock( "V.java", "v.w.x", javaContent.toString() );
		
		StringBuilder javaContent2 = new StringBuilder();
		javaContent2.append( "package v.w.x;" );
		javaContent2.append( "public class W extends V {" );
		javaContent2.append( TestUtility.generateMember( "long", "longMember" ) );
		javaContent2.append( "}" );
		JavaFileMock javaFileMock2 = new JavaFileMock( "W.java", "v.w.x", javaContent2.toString() );
		
		javaModel = new JavaFileMock[] { javaFileMock, javaFileMock2 };
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
	public void testMember_InheritedMember_MemberExists() throws Exception
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

	@Test
	public void testGetParentTaxonomy_ChildView_ParentExists() throws Exception
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
			Taxonomy currentParent = view.getParentTaxonomy();
			assertNotNull( currentParent );
			assertEquals( taxonomy, currentParent );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

}
