package ch.liquidmind.inflection.compiler;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class InflectionCompilerTest extends AbstractInflectionTest
{

	private static InflectionFileMock parentTaxonomy;
	private static JavaFileMock[] javaModel;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder viewBuilder = new StringBuilder();
		viewBuilder.append( "package v.w.x;" );
		viewBuilder.append( "public class V {}" );
		javaModel = new JavaFileMock[] { new JavaFileMock( "V.java", "v.w.x", viewBuilder.toString() ) };

		StringBuilder parentTaxonomyBuilder = new StringBuilder();
		parentTaxonomyBuilder.append( "package a.b.c;" );
		parentTaxonomyBuilder.append( "import v.w.x.*;" );
		parentTaxonomyBuilder.append( "taxonomy A" );
		parentTaxonomyBuilder.append( "{" );
		parentTaxonomyBuilder.append( "	view V {}" );
		parentTaxonomyBuilder.append( "}" );
		parentTaxonomy = new InflectionFileMock( "a.b.c", parentTaxonomyBuilder.toString() );

	}

	@Test
	public void testCompile_InheritanceAndImports_IllegalImport() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append( "package a.b.c;" );
		builder.append( "taxonomy B extends A" );
		builder.append( "{" );
		builder.append( "	exclude view V;	" ); // illegal, since A cannot be resolved (despite being included in super taxonomy)
		builder.append( "}" );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); // <inflection-error/> Should not compile because child does not import view A
		} , javaModel, null, parentTaxonomy, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testCompile_InheritanceAndImports_SuccessfulCompilation() throws Exception
	{
		StringBuilder builder = new StringBuilder();

		builder.append( "package a.b.c;" );
		builder.append( "taxonomy B extends A" );
		builder.append( "{" );
		builder.append( "	view * {}" ); // legal, but doesn’t refer to A (but rather to all classes in this package)
		builder.append( "}" );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , javaModel, null, parentTaxonomy, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	@Ignore( "IndexOutOfBoundsException is thrown when creating fault message (probably only on Windows using Unix style newlines)" )
	// TODO <inflection-error/> failing test
	public void testCompile_InvalidFile_SuccessfulFaultMessageGeneration() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c;" );
		builder.append( "taxonomy B extends A_TYPO" ); // typo when extending: invalid file
		builder.append( "{" );
		builder.append( "	view * { *; }" );
		builder.append( "}" );

		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

}
