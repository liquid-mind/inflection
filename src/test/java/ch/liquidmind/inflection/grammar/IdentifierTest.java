package ch.liquidmind.inflection.grammar;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

@RunWith( Parameterized.class )
public class IdentifierTest extends AbstractInflectionTest
{

	// unicode characters are not tested, although java would allow most of them 
	private static String legalFirstCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_$";
	private static String illegalFirstCharacters = "0123456789";
	private static String illegalCharacters = "*.;:.,{}("; // /)=+"%&? <inflection-error/> check why the commented characters are not treated as illegal characters

	private static String allIllegalCharacters = illegalFirstCharacters + illegalCharacters;

	@Parameters( name = "{index}: Identifier: {0}, expected compilation: {1}" )
	public static Collection< Object[] > data()
	{
		Collection< Object[] > data = new ArrayList< >();

		for ( char c : legalFirstCharacters.toCharArray() )
		{
			// check "valid first characters" compile
			addTestCase( data, String.valueOf( c ), true );
			for ( char d : illegalFirstCharacters.toCharArray() )
			{
				// check "valid first characters + illegal first characters" compile
				addTestCase( data, String.valueOf( new char[] { c, d } ), true );
			}
			for ( char d : illegalCharacters.toCharArray() )
			{
				// check "valid first characters + illegal characters" do *not* compile
				addTestCase( data, String.valueOf( new char[] { c, d } ), false );
			}
		}
		for ( char c : allIllegalCharacters.toCharArray() )
		{
			// check "illegal characters" do *not* compile
			addTestCase( data, String.valueOf( c ), false );
		}

		return data;
	}

	private static void addTestCase( Collection< Object[] > data, String identifierUnderTest,
			boolean successfulCompilation )
	{
		data.add( new Object[] { identifierUnderTest, successfulCompilation } );
	}

	private final String identifier;
	private final boolean successful;

	public IdentifierTest( String identifier, boolean successful )
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
		} , createInflectionFileMock( "taxonomy " + identifier + " {}" ) );
	}

}
