package ch.liquidmind.inflection.compiler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
		parentTaxonomyBuilder.append( "package ch.liquidmind.inflection.compiler;" );
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
		builder.append( "package ch.liquidmind.inflection.compiler;" );
		builder.append( "import ch.liquidmind.inflection.test.model.*;" );
		
		builder.append( "taxonomy InflectionCompilerTest_ValidFileTaxonomy" );
		builder.append( "{" );
		builder.append( "	view A { *; }" );
		builder.append( "}" );
		
		builder.append( "taxonomy InflectionCompilerTest_ValidFileChildTaxonomy extends InflectionCompilerTest_ValidFileTaxonomy" );
		builder.append( "{" );
		builder.append( "	view * { *; }" );
		builder.append( "}" );

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( "ch.liquidmind.inflection.compiler", builder.toString() ) );
		InflectionCompiler.compile( job );
		assertFalse( "Compilation units must exist", job.getCompilationUnits().isEmpty() );
		assertTrue( "Compilation errors must not exist", job.getCompilationFaults().isEmpty() );
	}

	@Test
	@Ignore( "IndexOutOfBoundsException is thrown when creating fault message (probably only on Windows using Unix style newlines)" )
	// TODO failing test
	public void testCompile_InvalidFile_SuccessfulFaultMessageGeneration() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package ch.liquidmind.inflection.compiler;" );
		builder.append( "import ch.liquidmind.inflection.test.model.*;" );
		
		builder.append( "taxonomy InflectionCompilerTest_InvalidFileTaxonomy" );
		builder.append( "{" );
		builder.append( "	view A { *; }" );
		builder.append( "}" );
		
		builder.append( "taxonomy InflectionCompilerTest_InvalidFileChildTaxonomy extends InflectionCompilerTestTYPO_InvalidFileTaxonomy" ); // typo when extending: invalid file
		builder.append( "{" );
		builder.append( "	view * { *; }" );
		builder.append( "}" );

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( "ch.liquidmind.inflection.model.external", builder.toString() ) );
		InflectionCompiler.compile( job );
		assertFalse( "Compilation errors must not exist", job.getCompilationFaults().isEmpty() );
		String message = job.getCompilationFaults().get( 0 ).createFaultMessage();
		assertNotNull( "Fault message must exist", message != null );
		assertTrue( "", message.length() > 0 );
	}

	@Test
	@Ignore( "Should not compile because child does not import view A" )
	// TODO failing test
	public void testCompile_InheritanceAndImports_IllegalImport() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append( "package ch.liquidmind.inflection.compiler;" );
		builder.append( "taxonomy InflectionCompilerTest_InheritanceAndImports_IllegalChildImportTaxonomy extends InflectionCompilerTest_InheritanceAndImports_ParentTaxonomy" );
		builder.append( "{" );
		builder.append( "	exclude view A;	" ); // illegal, since A cannot be resolved (despite being included in super taxonomy)
		builder.append( "}" );

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( "ch.liquidmind.inflection.compiler", parentTaxonomy ), new InflectionFileMock( "ch.liquidmind.inflection.compiler", builder.toString() ) );
		InflectionCompiler.compile( job );
		assertFalse( "compile errors expected since A cannot be resolved", job.getCompilationFaults().isEmpty() );
	}

	@Test
	public void testCompile_InheritanceAndImports_SuccessfulCompilation() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append( "package ch.liquidmind.inflection.compiler;" );
		builder.append( "taxonomy InflectionCompilerTest_InheritanceAndImports_LegalChildImportTaxonomy extends InflectionCompilerTest_InheritanceAndImports_ParentTaxonomy" );
		builder.append( "{" );
		builder.append( "	view * {}" ); // legal, but doesn’t refer to A (but rather to all classes in this package)
		builder.append( "}" );

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( "ch.liquidmind.inflection.compiler", parentTaxonomy ), new InflectionFileMock( "ch.liquidmind.inflection.compiler", builder.toString() ) );
		InflectionCompiler.compile( job );
		assertTrue( "successful compilation expected", job.getCompilationFaults().isEmpty() );
	}

}
