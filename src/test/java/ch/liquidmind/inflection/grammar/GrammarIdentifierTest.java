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
public class GrammarIdentifierTest extends AbstractInflectionTest
{

	@Parameters( name = "{index}: Identifier: {0}, expected compilation: {1}" )
	public static Collection< Object[] > data()
	{
		return Arrays.asList( new Object[][] { 
			    { "A", true },
			    
				{ "_A", true },
				
				{ "A_", true },
				
				{ "$A", true },
				
				{ "A$", true },
				
				{ "A1", true },
				
				{ "1A", true }, // <inflection-error/> 1A is an illegal identifier, should not compile
				
				{ "true", true }, // <inflection-error/> true is an illegal identifier, should not compile
				
				{ "false", true }, // <inflection-error/> false is an illegal identifier, should not compile
				
				{ "null", true } // <inflection-error/> null is an illegal identifier, should not compile
		} );
	}

	private final String identifier;
	private final boolean successful;

	public GrammarIdentifierTest( String identifier, boolean successful )
	{
		this.identifier = identifier;
		this.successful = successful;
	}

	@Test
	public void testIdentifier()
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
		} , createInflectionFileMock( "taxonomy " + identifier + " {}") );
	}

}
