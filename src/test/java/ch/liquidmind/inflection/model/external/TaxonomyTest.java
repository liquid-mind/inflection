package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class TaxonomyTest extends AbstractInflectionTest
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
	public void testTaxonomyUniqueness_DuplicateTaxonomyNamedPackageInSameFile_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {} taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyNamedPackageInDifferentFiles_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyUnnamedPackageInDifferentFiles_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "taxonomy A {}" ), createInflectionFileMock( "taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyUnnamedPackageInSameFile_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "taxonomy A {} taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyNamedAndUnnamedPackageInDifferentFiles_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), createInflectionFileMock( "taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyNamedPackageOnClasspath_CompilationFailure() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "a", "package a; taxonomy A {}" ) };

		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , null, classpath, createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyUnnamedPackageOnClasspath_SuccessfulCompilation() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "taxonomy A {}" ) };

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , null, classpath, createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesNamedPackageInSameFile_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {} taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesUnnamedPackageInSameFile_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "taxonomy A {} taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesNamedPackageInDifferentFiles_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), createInflectionFileMock( "a", "package a; taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesUnnamedPackageInDifferentFiles_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "taxonomy A {}" ), createInflectionFileMock( "taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesNamedPackageOnClasspath_SuccessfulCompilation() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "a", "package a; taxonomy A {}" ) };

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , null, classpath, createInflectionFileMock( "a", "package a; taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesUnnamedPackageOnClasspath_SuccessfulCompilation() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "taxonomy A {}" ) };

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , null, classpath, createInflectionFileMock( "taxonomy B {}" ) );
	}

	@Test
	public void testViewHierarchy_ExistingHierarchy_ParentViewExists() throws Exception
	{
		StringBuilder javaSuperClass = new StringBuilder();
		javaSuperClass.append( "package v.w.x;" );
		javaSuperClass.append( "public class V {}" );

		StringBuilder javaChildClass = new StringBuilder();
		javaChildClass.append( "package v.w.x;" );
		javaChildClass.append( "public class W extends V {}" );

		JavaFileMock[] javaFileMocks = new JavaFileMock[] { createJavaFileMock( "V.java", "v.w.x", javaSuperClass.toString() ), createJavaFileMock( "W.java", "v.w.x", javaChildClass.toString() ) };

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
		} , javaFileMocks, null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}

	@Test
	public void testViewHierarchy_MissingHierarchy_ParentViewExists() throws Exception
	{
		StringBuilder javaSuperClass = new StringBuilder();
		javaSuperClass.append( "package v.w.x;" );
		javaSuperClass.append( "public class V {}" );

		StringBuilder javaChildClass = new StringBuilder();
		javaChildClass.append( "package v.w.x;" );
		javaChildClass.append( "public class W extends V {}" );

		JavaFileMock[] javaFileMocks = new JavaFileMock[] { createJavaFileMock( "V.java", "v.w.x", javaSuperClass.toString() ), createJavaFileMock( "W.java", "v.w.x", javaChildClass.toString() ) };

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
		} , javaFileMocks, null, createInflectionFileMock( "a.b.c", taxonomy.toString() ) );
	}

	@Test
	public void testEmptyTaxonomy_NoViews_ViewsDoNotExist() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.A" );
			assertTrue( "Views must not exist", taxomomy.getViews().isEmpty() );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}

}
