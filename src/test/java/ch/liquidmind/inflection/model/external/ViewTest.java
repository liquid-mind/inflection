package ch.liquidmind.inflection.model.external;

import static ch.liquidmind.inflection.test.mock.AbstractFileMock.UNNAMED_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class ViewTest extends AbstractInflectionTest
{
	private JavaFileMock[] createSimpleJavaModel()
	{
		StringBuilder classV = new StringBuilder();
		classV.append( "public class V {}" );
		JavaFileMock v = createJavaFileMock( "V.java", UNNAMED_PACKAGE, classV.toString() );

		return new JavaFileMock[] { v };
	}
	
	private JavaFileMock[] createSimpleHierarchicalJavaModel()
	{
		StringBuilder javaSuperClass = new StringBuilder();
		javaSuperClass.append( "package v.w.x;" );
		javaSuperClass.append( "public class V {}" );
		JavaFileMock javaSuperClassFileMock = createJavaFileMock( "V.java", "v.w.x", javaSuperClass.toString() );

		StringBuilder javaChildClass = new StringBuilder();
		javaChildClass.append( "package v.w.x;" );
		javaChildClass.append( "public class W extends V {}" );
		JavaFileMock javaChildClassFileMock = createJavaFileMock( "W.java", "v.w.x", javaChildClass.toString() );

		return new JavaFileMock[] { javaSuperClassFileMock, javaChildClassFileMock };
	}
	
	private JavaFileMock[] createOverlappingJavaModel()
	{
		StringBuilder classV1 = new StringBuilder();
		classV1.append( "public class V1 {}" );
		JavaFileMock v1 = createJavaFileMock( "V1.java", UNNAMED_PACKAGE, classV1.toString() );

		StringBuilder classV2 = new StringBuilder();
		classV2.append( "public class V2 {}" );
		JavaFileMock v2 = createJavaFileMock( "V2.java", UNNAMED_PACKAGE, classV2.toString() );

		return new JavaFileMock[] { v1, v2 };
	}

	@Test
	public void testViewIncludeExclude_IncludeViewWithoutInclude_ViewExists() throws Exception
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewIncludeExclude_IncludeViewWithInclude_ViewExists() throws Exception
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewIncludeExclude_ExcludeView_ViewDoesNotExist() throws Exception
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewIncludeExclude_IncludeExcludeOrderIncludeFirst_ViewDoesNotExist() throws Exception
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewIncludeExclude_IncludeExcludeOrderExcludeFirst_ViewDoesNotExist() throws Exception
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewIncludeExclude_IncludeWithSelector_ViewDoesExist() throws Exception
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewIncludeExclude_ExcludeWithSelector_ViewDoesNotExist() throws Exception
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewParentTaxonomy_ChildView_ParentExists() throws Exception
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewAlias_ViewWithDifferentAlias_AliasExistsAndNameCorrect() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A {" );
		builder.append( "	view V as X { *; }" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			View view = taxonomy.getView( "X" );
			assertNotNull( view );
			assertEquals( "v.w.x.V", view.getName() );
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewAlias_ViewWithSameAlias_AliasExistsAndNameCorrect() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A {" );
		builder.append( "	view V as V { *; }" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			View view = taxonomy.getView( "V" );
			assertNotNull( view );
			assertEquals( "v.w.x.V", view.getName() );
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testViewAlias_SelectorWithAlias_CompilationFailure() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A {" );
		builder.append( "	view V* as X { *; }" );
		builder.append( "}" );

		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job ); 
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}
	
	@Test
	public void testViewAlias_DuplicateAlias_CompilationFailure() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A {" );
		builder.append( "	view V as V1 {} view W as V1 {}" );
		builder.append( "}" );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); // <inflection-error/> should not compile as alias is duplicate
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", builder.toString() ) );
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
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
		} , createSimpleHierarchicalJavaModel(), null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}
	
	@Test
	public void testViewSelector_OverlappingSetsSingleViewDeclaration_PrecedenceSetsAlias() throws Exception
	{
		StringBuilder taxonomy = new StringBuilder();
		taxonomy.append( "package a.b.c; " );
		taxonomy.append( "taxonomy A { view V, V as View1 {} } " );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			View view = taxomomy.getView( "View1" );
			assertNotNull( view );
			view = taxomomy.getView( "V" );
			assertNotNull( view );
		} , createSimpleJavaModel(), null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}
	
	@Test
	public void testViewSelector_OverlappingSetsSingleViewDeclaration_PrecedenceRemovesAlias() throws Exception
	{
		StringBuilder taxonomy = new StringBuilder();
		taxonomy.append( "package a.b.c; " );
		taxonomy.append( "taxonomy A { view V as View1, V {} } " );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			View view = taxomomy.getView( "View1" );
			assertNull( view );
			view = taxomomy.getView( "V" );
			assertNotNull( view );
		} , createSimpleJavaModel(), null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}

	@Test
	public void testViewSelector_OverlappingSetsMultipleViewDeclarations_PrecedenceSetsAlias() throws Exception
	{
		StringBuilder taxonomy = new StringBuilder();
		taxonomy.append( "package a.b.c; " );
		taxonomy.append( "taxonomy A { view V* {} view V1 as View1 {} view V2 as View2 {} } " );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			View view = taxomomy.getView( "View1" );
			assertNotNull( view );
			view = taxomomy.getView( "View2" );
			assertNotNull( view );
			view = taxomomy.getView( "V1" );
			assertNotNull( view );
			view = taxomomy.getView( "V2" );
			assertNotNull( view );
		} , createOverlappingJavaModel(), null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}
	
	@Test
	public void testViewSelector_OverlappingSetsMultipleViewDeclarations_PrecedenceIgnoresAlias() throws Exception
	{
		StringBuilder taxonomy = new StringBuilder();
		taxonomy.append( "package a.b.c; " );
		taxonomy.append( "taxonomy A { view V1 as View1 {} view V2 as View2 {} view V* {} } " );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			View view = taxomomy.getView( "View1" );
			assertNull( view );
			view = taxomomy.getView( "View2" );
			assertNull( view );
			view = taxomomy.getView( "V1" );
			assertNotNull( view );
			view = taxomomy.getView( "V2" );
			assertNotNull( view );
		} , createOverlappingJavaModel(), null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}
	
	@Test
	public void testViewSelector_EmptyResult_NoViews() throws Exception
	{
		StringBuilder taxonomy = new StringBuilder();
		taxonomy.append( "package a.b.c; " );
		taxonomy.append( "taxonomy A { view NONE* {} } " );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.b.c.A" );
			assertTrue( taxomomy.getViews().isEmpty() );
		} , createSimpleJavaModel(), null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}

}
