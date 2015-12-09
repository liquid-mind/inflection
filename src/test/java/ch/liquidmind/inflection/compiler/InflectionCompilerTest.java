package ch.liquidmind.inflection.compiler;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.InflectionFileMock;

public class InflectionCompilerTest
{

	private static String parentTaxonomy;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder parentTaxonomyBuilder = new StringBuilder();
		parentTaxonomyBuilder.append( "package a.b.c;" );
		parentTaxonomyBuilder.append( "import ch.liquidmind.inflection.test.model.*;" );
		parentTaxonomyBuilder.append( "taxonomy InflectionCompilerTest_InheritanceAndImports_ParentTaxonomy" );
		parentTaxonomyBuilder.append( "{" );
		parentTaxonomyBuilder.append( "	view A {}" );
		parentTaxonomyBuilder.append( "}" );
		parentTaxonomy = parentTaxonomyBuilder.toString();
	}

	@Test
	public void testCompile_ValidFile_SuccessfulCompilation() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c;" );
		builder.append( "import ch.liquidmind.inflection.test.model.*;" );
		
		builder.append( "taxonomy InflectionCompilerTest_ValidFileTaxonomy" );
		builder.append( "{" );
		builder.append( "	view A { *; }" );
		builder.append( "}" );
		
		builder.append( "taxonomy InflectionCompilerTest_ValidFileChildTaxonomy extends InflectionCompilerTest_ValidFileTaxonomy" );
		builder.append( "{" );
		builder.append( "	view * { *; }" );
		builder.append( "}" );

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( "a.b.c", builder.toString() ) );
		InflectionCompiler.compile( job );
		InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
	}

	@Test
	@Ignore( "IndexOutOfBoundsException is thrown when creating fault message (probably only on Windows using Unix style newlines)" )
	// TODO <inflection-error/> failing test
	public void testCompile_InvalidFile_SuccessfulFaultMessageGeneration() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c;" );
		builder.append( "import ch.liquidmind.inflection.test.model.*;" );
		
		builder.append( "taxonomy InflectionCompilerTest_InvalidFileTaxonomy" );
		builder.append( "{" );
		builder.append( "	view A { *; }" );
		builder.append( "}" );
		
		builder.append( "taxonomy InflectionCompilerTest_InvalidFileChildTaxonomy extends InflectionCompilerTestTYPO_InvalidFileTaxonomy" ); // typo when extending: invalid file
		builder.append( "{" );
		builder.append( "	view * { *; }" );
		builder.append( "}" );

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( "a.b.c", builder.toString() ) );
		InflectionCompiler.compile( job );
		InflectionCompilerTestUtility.assertCompilationFailure( job );
	}

	@Test
	public void testCompile_InheritanceAndImports_IllegalImport() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append( "package a.b.c;" );
		builder.append( "taxonomy InflectionCompilerTest_InheritanceAndImports_IllegalChildImportTaxonomy extends InflectionCompilerTest_InheritanceAndImports_ParentTaxonomy" );
		builder.append( "{" );
		builder.append( "	exclude view A;	" ); // illegal, since A cannot be resolved (despite being included in super taxonomy)
		builder.append( "}" );

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( "a.b.c", parentTaxonomy ), new InflectionFileMock( "a.b.c", builder.toString() ) );
		InflectionCompiler.compile( job );
		InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); // <inflection-error/> Should not compile because child does not import view A
	}

	@Test
	public void testCompile_InheritanceAndImports_SuccessfulCompilation() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append( "package a.b.c;" );
		builder.append( "taxonomy InflectionCompilerTest_InheritanceAndImports_LegalChildImportTaxonomy extends InflectionCompilerTest_InheritanceAndImports_ParentTaxonomy" );
		builder.append( "{" );
		builder.append( "	view * {}" ); // legal, but doesn’t refer to A (but rather to all classes in this package)
		builder.append( "}" );

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( "a.b.c", parentTaxonomy ), new InflectionFileMock( "a.b.c", builder.toString() ) );
		InflectionCompiler.compile( job );
		assertTrue( "successful compilation expected", job.getCompilationFaults().isEmpty() );
	}

}
