package ch.liquidmind.inflection.grammar;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

public class GrammarTest extends AbstractInflectionTest
{

	@Test
	// TODO <inflection-error/> compiler outputs line 1:0 token recognition error at: '// comment'
	public void testSingleLineComment_CommentOnlyFile_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "// comment" ) );
	}

	@Test
	public void testMultiLineCommentOneLine_CommentOnlyFile_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "/* comment */" ) );
	}

	@Test
	public void testMultiLineCommentOneLine_CommentOnlyFile_UnexpectedEndOfComment() throws Exception
	{
		// TODO <inflection-error/> should fail because end of comment is missing
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "/* comment" ) );
	}

	@Test
	public void testMultiLineCommentMultipleLines_CommentOnlyFile_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "/* comment begin" + System.lineSeparator() + "comment end */" ) );
	}

	@Test
	public void testMultiLineCommentMultipleLines_CommentOnlyFile_UnexpectedEndOfComment() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "/* comment begin" + System.lineSeparator() + "comment end" ) );
	}

}
