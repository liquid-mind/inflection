package ch.liquidmind.inflection.grammar;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

@RunWith( Parameterized.class )
public class CommentTest extends AbstractInflectionTest
{

	@Parameters( name = "{index}: File content: {0}, expected compilation: {1}" )
	public static Collection< Object[] > data()
	{
		return Arrays.asList( new Object[][] { { 
			    "// comment", true },

				{ "/* comment */", true },

				{ "/* comment", true }, // TODO <inflection-error/> missing comment end, should not compile: true -> false

				{ "/* comment begin" + System.lineSeparator() + " comment end */", true },

				{ "/* comment begin" + System.lineSeparator() + " comment end", true }, // TODO <inflection-error/> missing comment end, should not compile: true -> false
				
				{ "/* // */", true },
				
				{ "/* //", true }, // TODO <inflection-error/> missing comment end, should not compile: true -> false
				
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

	public CommentTest( String fileContent, boolean successful )
	{
		this.fileContent = fileContent;
		this.successful = successful;
	}

	@Test
	public void testComment()
	{
		doTest( job -> {
			if ( successful )
			{
				InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			}
			else
			{
				InflectionCompilerTestUtility.assertCompilationFailure( job );
			}
		} , createInflectionFileMock( fileContent ) );
	}

}
