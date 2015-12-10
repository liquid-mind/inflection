package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class TaxonomyTest extends AbstractInflectionTest
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
		JavaFileMock javaFileMock = new JavaFileMock( "V.java", "v.w.x", javaContent.toString() );
		ClassLoader modelClassLoader = InflectionCompilerTestUtility.compileJava( new JavaFileMock[] { javaFileMock } );
		
		StringBuilder builder = new StringBuilder();

		builder.append( "package a.b.c; " );
		builder.append( "import v.w.x.*; " );

		builder.append( "taxonomy TaxonomyTestTaxonomy " );
		builder.append( "{ " );
		builder.append( "include view V {} " );
		builder.append( "} " );

		builder.append( "taxonomy TaxonomyTest_GetView_IncludeViewWithoutIncludeTaxonomy  " );
		builder.append( "{ " );
		builder.append( "	view V {} " );
		builder.append( "} " );

		builder.append( "taxonomy TaxonomyTest_GetView_IncludeViewWithIncludeTaxonomy  " );
		builder.append( "{ " );
		builder.append( "	include view V {} " );
		builder.append( "} " );

		builder.append( "taxonomy TaxonomyTest_GetView_ExcludeViewTaxonomy extends TaxonomyTestTaxonomy " );
		builder.append( "{ " );
		builder.append( "	exclude view V; " );
		builder.append( "} " );

		builder.append( "taxonomy TaxonomyTest_GetView_IncludeExcludeOrderIncludeFirstTaxonomy  " );
		builder.append( "{ " );
		builder.append( "	include view V {} " );
		builder.append( "	exclude view V; " );
		builder.append( "} " );

		builder.append( "taxonomy TaxonomyTest_GetView_IncludeExcludeOrderExcludeFirstTaxonomy  " );
		builder.append( "{ " );
		builder.append( "	exclude view V; " );
		builder.append( "	include view V {} " ); // reverse order should not matter
		builder.append( "} " );

		builder.append( "taxonomy TaxonomyTest_GetView_IncludeWithSelectorTaxonomy " );
		builder.append( "{ " );
		builder.append( "	include view V* {} " );
		builder.append( "} " );

		builder.append( "taxonomy TaxonomyTest_GetView_ExcludeWithSelectorTaxonomy extends TaxonomyTestTaxonomy " );
		builder.append( "{ " );
		builder.append( "	exclude view V*; " );
		builder.append( "}		 " );

		taxonomyLoader = InflectionCompilerTestUtility.compileInflection( modelClassLoader , new InflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testGetView_IncludeViewWithoutInclude_ViewExists() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.TaxonomyTest_GetView_IncludeViewWithoutIncludeTaxonomy" );
		assertNotNull( "view must exist", taxonomy.getView( "v.w.x.V" ) );
	}

	@Test
	public void testGetView_IncludeViewWithInclude_ViewExists() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.TaxonomyTest_GetView_IncludeViewWithIncludeTaxonomy" );
		assertNotNull( "view must exist", taxonomy.getView( "v.w.x.V" ) );
	}

	@Test
	public void testGetView_ExcludeView_ViewDoesNotExist() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.TaxonomyTest_GetView_ExcludeViewTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( "V" ) );
	}

	@Test
	public void testGetView_IncludeExcludeOrderIncludeFirst_ViewDoesNotExist() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.TaxonomyTest_GetView_IncludeExcludeOrderIncludeFirstTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( "V" ) );
	}

	@Test
	public void testGetView_IncludeExcludeOrderExcludeFirst_ViewDoesNotExist() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.TaxonomyTest_GetView_IncludeExcludeOrderExcludeFirstTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( "V" ) );
	}

	@Test
	public void testGetView_IncludeWithSelector_ViewDoesExist() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.TaxonomyTest_GetView_IncludeWithSelectorTaxonomy" );
		assertNotNull( "view must exist", taxonomy.getView( "v.w.x.V" ) );
	}

	@Test
	public void testGetView_ExcludeWithSelector_ViewDoesNotExist() throws Exception
	{
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.TaxonomyTest_GetView_ExcludeWithSelectorTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( "V" ) );
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

}
