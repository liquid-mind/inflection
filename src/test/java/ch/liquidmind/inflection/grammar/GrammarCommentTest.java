package ch.liquidmind.inflection.grammar;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.InflectionFileMock;

@RunWith( Parameterized.class )
public class GrammarCommentTest
{

	@Parameters( name = "{index}: File content: {0}, expected compilation: {1}" )
	public static Collection< Object[] > data()
	{
		return Arrays.asList( new Object[][] { { 
			    "// comment", true },

				{ "/* comment */", true },

				{ "/* comment", true }, // TODO should be false

				{ "/* comment begin" + System.lineSeparator() + " comment end */", true },

				{ "/* comment begin" + System.lineSeparator() + " comment end", true }, // TODO should be false
				
				{ "/* // */", true },
				
				{ "/* //", true }, // TODO should be false
				
				{ "// /*", true },
				
				{ "// */", true },
				
				{ "taxonomy A { /* }", false },
				
				{ "taxonomy A { /* */ }", true },
				
				{ "taxonomy A { /* " + System.lineSeparator() + " */ }", true },
				
				{ "taxonomy A { /* " + System.lineSeparator()+ "}", false },
				
				{ "taxonomy A { // }", false },
				
				{ "taxonomy A { // " + System.lineSeparator() + "}", true }
		} );
	}

	private final String fileContent;
	private final boolean successful;

	public GrammarCommentTest( String fileContent, boolean successful )
	{
		this.fileContent = fileContent;
		this.successful = successful;
	}

	@Test
	public void test()
	{
		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( new InflectionFileMock( fileContent.toString() ) );
		InflectionCompiler.compile( job );
		if ( successful )
		{
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		}
		else
		{
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		}
	}

}
