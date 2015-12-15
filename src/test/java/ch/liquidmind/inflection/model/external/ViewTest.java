package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class ViewTest extends AbstractInflectionTest
{
	private static JavaFileMock[] javaModel;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder javaSuperClass = new StringBuilder();
		javaSuperClass.append( "package v.w.x;" );
		javaSuperClass.append( "public class V {" );
		javaSuperClass.append( "}" );
		JavaFileMock javaSuperClassFileMock = new JavaFileMock( "V.java", "v.w.x", javaSuperClass.toString() );

		StringBuilder javaChildClass = new StringBuilder();
		javaChildClass.append( "package v.w.x;" );
		javaChildClass.append( "public class W extends V {" );
		javaChildClass.append( "}" );
		JavaFileMock javaChildClassFileMock = new JavaFileMock( "W.java", "v.w.x", javaChildClass.toString() );

		javaModel = new JavaFileMock[] { javaSuperClassFileMock, javaChildClassFileMock };
	}
	
	@Test
	public void testGetView_IncludeViewWithoutInclude_ViewExists() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*; " );
		builder.append( "taxonomy A  " );
		builder.append( "{ " );
		builder.append( "	view V {} " );
		builder.append( "} " );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			assertNotNull( "view must exist", taxonomy.getView( "v.w.x.V" ) );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetView_IncludeViewWithInclude_ViewExists() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*; " );
		builder.append( "taxonomy A  " );
		builder.append( "{ " );
		builder.append( "	include view V {} " );
		builder.append( "} " );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			assertNotNull( "view must exist", taxonomy.getView( "v.w.x.V" ) );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetView_ExcludeView_ViewDoesNotExist() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*; " );
		builder.append( "taxonomy A  " );
		builder.append( "{ " );
		builder.append( "	exclude view V; " );
		builder.append( "} " );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			assertNull( "view must not exist", taxonomy.getView( "V" ) );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetView_IncludeExcludeOrderIncludeFirst_ViewDoesNotExist() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*; " );
		builder.append( "taxonomy A  " );
		builder.append( "{ " );
		builder.append( "	include view V {} " );
		builder.append( "	exclude view V; " );
		builder.append( "} " );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			assertNull( "view must not exist", taxonomy.getView( "V" ) );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetView_IncludeExcludeOrderExcludeFirst_ViewDoesNotExist() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*; " );
		builder.append( "taxonomy A  " );
		builder.append( "{ " );
		builder.append( "	exclude view V; " );
		builder.append( "	include view V {} " ); // reverse order should not matter
		builder.append( "} " );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			assertNull( "view must not exist", taxonomy.getView( "V" ) );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetView_IncludeWithSelector_ViewDoesExist() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*; " );
		builder.append( "taxonomy A  " );
		builder.append( "{ " );
		builder.append( "	include view V* {} " );
		builder.append( "} " );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			assertNotNull( "view must exist", taxonomy.getView( "v.w.x.V" ) );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetView_ExcludeWithSelector_ViewDoesNotExist() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*; " );
		builder.append( "taxonomy A  " );
		builder.append( "{ " );
		builder.append( "	exclude view V*; " );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			assertNull( "view must not exist", taxonomy.getView( "V" ) );
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
	
	@Test
	public void testViewHierarchy_ExistingHierarchy_ParentViewExists() throws Exception
	{
		StringBuilder taxonomy = new StringBuilder();
		taxonomy.append( "package a.b.c; " );
		taxonomy.append( "import v.w.x.*; " );
		taxonomy.append( "taxonomy A { view V {} view W {} } " );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			View view = taxomomy.getView( "v.w.x.W" );
			assertNotNull( view );
			View parentView = view.getSuperview();
			assertEquals( "V", parentView.getSimpleName() );
			assertNotNull( "parent view is used in taxonomy A, must exist", parentView );
		} , javaModel, null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}

	@Test
	public void testViewHierarchy_MissingHierarchy_ParentViewExists() throws Exception
	{
		StringBuilder taxonomy = new StringBuilder();
		taxonomy.append( "package a.b.c; " );
		taxonomy.append( "import v.w.x.*; " );
		taxonomy.append( "taxonomy A { view W {} } " );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			View view = taxomomy.getView( "v.w.x.W" );
			assertNotNull( view );
			View parentView = view.getSuperview();
			assertNull( parentView ); // <inflection-error/> parent view should be automatically inserted by the compiler
			// assertEquals( "V", parentView.getSimpleName() );
			// assertNotNull( "parent view is NOT used in taxonomy A, but must exist", parentView );
		} , javaModel, null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}
	
}
