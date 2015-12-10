package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class ViewTest extends AbstractInflectionTest
{

	private static TaxonomyLoader taxonomyLoader;
	private static ClassLoader modelClassLoader;

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
		
		modelClassLoader = InflectionCompilerTestUtility.compileJava( new JavaFileMock[] { javaFileMock, javaFileMock2 } );
		
		
		StringBuilder builder = new StringBuilder();

		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*;" );

		builder.append( "taxonomy ViewTestTaxonomy {" );
		builder.append( "	view V { *; }" );
		builder.append( "}" );

		builder.append( "taxonomy ViewTest_GetMember_DeclaredMemberTaxonomy extends ViewTestTaxonomy" );
		builder.append( "{" );
		builder.append( "	view W { *; }	" );
		builder.append( "}" );

		builder.append( "taxonomy ViewTest_GetDeclaredMember_DeclaredMemberTaxonomy extends ViewTestTaxonomy" );
		builder.append( "{" );
		builder.append( "	view W { *; }	" );
		builder.append( "}" );

		builder.append( "taxonomy ViewTest_GetMember_InheritedMemberTaxonomy extends ViewTestTaxonomy {}" );

		builder.append( "taxonomy ViewTest_GetParentTaxonomy extends ViewTestTaxonomy" );
		builder.append( "{" );
		builder.append( "	view W { *; }	" );
		builder.append( "}" );

		taxonomyLoader = InflectionCompilerTestUtility.compileInflection( modelClassLoader, new InflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetMember_DeclaredMember_MemberExists() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.ViewTest_GetMember_DeclaredMemberTaxonomy" );
		View view = taxonomy.getView( "v.w.x.W" );
		assertNotNull( view.getMember( "longMember" ) );
	}

	@Test
	public void testGetDeclaredMember_DeclaredMember_MemberExists() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.ViewTest_GetDeclaredMember_DeclaredMemberTaxonomy" );
		View view = taxonomy.getView( "v.w.x.W" );
		assertNotNull( view.getDeclaredMember( "longMember" ) );
	}

	@Test
	public void testMember_InheritedMember_MemberExists() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.ViewTest_GetMember_InheritedMemberTaxonomy" );
		View view = taxonomy.getView( "v.w.x.V" );
		assertNotNull( view.getMember( "id" ) );
	}

	@Test
	public void testGetParentTaxonomy_ChildView_ParentExists() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.ViewTest_GetParentTaxonomy" );
		View view = taxonomy.getView( "v.w.x.W" );
		Taxonomy currentParent = view.getParentTaxonomy();
		assertNotNull( currentParent );
		assertEquals( taxonomy, currentParent );
	}

}
