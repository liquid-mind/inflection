package ch.liquidmind.inflection.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

@RunWith( Parameterized.class )
public class ReservedKeywordsTest extends AbstractInflectionTest
{

	private static String[] javaReservedKeywords = new String[] { "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while" };
	private static String[] inflectionReservedKeywords = new String[] { "taxonomy", "view", "use", "property", "field", "include", "exclude", "as" };

	@Parameters( name = "{index}: Reserved keyword: {0}" )
	public static Collection< Object[] > data()
	{
		Collection< Object[] > list = new ArrayList< >();
		String[] allReservedKeywords = Stream.concat(Arrays.stream(javaReservedKeywords), Arrays.stream(inflectionReservedKeywords)).toArray(String[]::new);
		
		for ( String keyword : allReservedKeywords )
		{
			list.add( new Object[] { keyword } );
		}
		return list;
	}

	private final String reservedKeyword;

	public ReservedKeywordsTest( String reservedKeyword )
	{
		this.reservedKeyword = reservedKeyword;
	}

	@Test
	public void testReservedKeyword()
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "taxonomy " + reservedKeyword + " {}" ) );

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "taxonomy A" + reservedKeyword + " {}" ) );
	}

}
