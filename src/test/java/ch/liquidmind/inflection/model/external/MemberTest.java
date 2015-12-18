package ch.liquidmind.inflection.model.external;

import static ch.liquidmind.inflection.test.mock.AbstractFileMock.UNNAMED_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class MemberTest extends AbstractInflectionTest
{
	
	private JavaFileMock[] createSimpleJavaModel() 
	{
		StringBuilder javaClass = new StringBuilder();
		javaClass.append( "public class V {" );
		javaClass.append( TestUtility.generateMember( "int", "member1" ) );
		javaClass.append( TestUtility.generateMember( "int", "member2" ) );
		javaClass.append( "}" );
		JavaFileMock javaFileMock = createJavaFileMock( "V.java", UNNAMED_PACKAGE, javaClass.toString() );

		return new JavaFileMock[] { javaFileMock };
	}

	private JavaFileMock[] createHierarchicalJavaModel() 
	{
		StringBuilder javaChildClass = new StringBuilder();
		javaChildClass.append( "public class W extends V {" );
		javaChildClass.append( TestUtility.generateMember( "int", "submember1" ) );
		javaChildClass.append( TestUtility.generateMember( "int", "submember2" ) );
		javaChildClass.append( "}" );
		JavaFileMock javaChildClassFileMock = createJavaFileMock( "W.java", UNNAMED_PACKAGE, javaChildClass.toString() );

		return new JavaFileMock[] { createSimpleJavaModel()[0], javaChildClassFileMock };
	}

	@Test
	public void testMemberSelectionType_Include_CorrectType() throws Exception
	{
		StringBuilder inflectContent = new StringBuilder();
		inflectContent.append( "package a;" );
		inflectContent.append( "import v.w.x.*;" );
		inflectContent.append( "taxonomy A {}" );
		inflectContent.append( "taxonomy B extends A" );
		inflectContent.append( "{" );
		inflectContent.append( "	view V { *; }" );
		inflectContent.append( "}" );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.B" );
			View view = taxonomy.getView( "V" );
			Member member = view.getDeclaredMember( "member1" );
			assertEquals( SelectionType.INCLUDE, member.getSelectionType() );
		} , createSimpleJavaModel(), null, createInflectionFileMock( "a", inflectContent.toString() ) );
	}
	
	@Test
	public void testGetMember_DeclaredMember_MemberExists() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "taxonomy A {" );
		builder.append( "	view V { *; }" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "A" );
			View view = taxonomy.getView( "V" );
			assertNotNull( view.getMember( "member1" ) );
		} , createSimpleJavaModel(), null, createInflectionFileMock( builder.toString() ) );
	}

	@Test
	public void testGetDeclaredMember_DeclaredMember_MemberExists() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "taxonomy A {" );
		builder.append( "	view W { *; }" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "A" );
			View view = taxonomy.getView( "W" );
			assertNull( view.getDeclaredMember( "member1" ) );
		} , createHierarchicalJavaModel(), null, createInflectionFileMock( builder.toString() ) );
	}
	
	@Test
	public void testGetDeclaredMember_DeclaredMember_MemberNotExists() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "taxonomy A {" );
		builder.append( "	view V { *; }" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "A" );
			View view = taxonomy.getView( "V" );
			assertNotNull( view.getDeclaredMember( "member1" ) );
		} , createHierarchicalJavaModel(), null, createInflectionFileMock( builder.toString() ) );
	}
	
}
