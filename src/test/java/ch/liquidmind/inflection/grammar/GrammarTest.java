package ch.liquidmind.inflection.grammar;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.InflectionFileMock;

public class GrammarTest
{

	@Test
	// TODO compiler outputs line 1:0 token recognition error at: '// comment'
	public void testSingleLineComment_CommentOnlyFile_SuccessfulCompilation() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "// comment" );
		
		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( builder.toString() ) );
		InflectionCompiler.compile( job );
		InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
	}
	
	@Test
	public void testMultiLineCommentOneLine_CommentOnlyFile_SuccessfulCompilation() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "/* comment" );
		
		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( builder.toString() ) );
		InflectionCompiler.compile( job );
		// TODO should fail because end of comment is missing
		// InflectionCompilerTestUtility.assertCompilationFailure( job );
		InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
	}
	
	@Test
	public void testMultiLineCommentOneLine_CommentOnlyFile_UnexpectedEndOfComment() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "/* comment" );
		
		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( builder.toString() ) );
		InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
	}
		
	@Test
	public void testMultiLineCommentMultipleLines_CommentOnlyFile_SuccessfulCompilation() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "/* comment begin" );
		builder.append( System.lineSeparator() );
		builder.append( "comment end */" );
		
		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( builder.toString() ) );
		InflectionCompiler.compile( job );
		InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
	}	
	
	@Test
	public void testMultiLineCommentMultipleLines_CommentOnlyFile_UnexpectedEndOfComment() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "/* comment begin" );
		builder.append( System.lineSeparator() );
		builder.append( "comment end" );
		
		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( builder.toString() ) );
		InflectionCompiler.compile( job );
		// TODO should fail because end of comment is missing
		// InflectionCompilerTestUtility.assertCompilationFailure( job );
		InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
	}	
		
}
