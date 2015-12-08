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
public class GrammarReservedKeywordsTest extends AbstractInflectionTest
{

	private static String[] reservedKeywords = new String[] { "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", 
			"synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", 
			"import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", 
			"final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while" };

	@Parameters( name = "{index}: Reserved keyword: {0}" )
	public static Collection< Object[] > data()
	{
		Collection< Object[] > list = new ArrayList< >();
		for ( String keyword : reservedKeywords )
		{
			list.add( new Object[] { keyword } );
		}
		return list;
	}

	private final String reservedKeyword;

	public GrammarReservedKeywordsTest( String reservedKeyword )
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
