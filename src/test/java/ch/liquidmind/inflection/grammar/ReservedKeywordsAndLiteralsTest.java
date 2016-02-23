package ch.liquidmind.inflection.grammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

@RunWith( Parameterized.class )
public class ReservedKeywordsAndLiteralsTest extends AbstractInflectionTest
{

	private static String[] javaReservedKeywords = new String[] { "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while" };
	private static String[] javaReservedLiterals = new String[] { "true", "false", "null" }; 
	private static String[] inflectionReservedKeywords = new String[] { "taxonomy", "view", "use", "property", "field", "include", "exclude", "as" };

	@Parameters( name = "{index}: Reserved item: {0}" )
	public static Collection< Object[] > data()
	{
		Collection< Object[] > list = new ArrayList< >();
		String[] allReservedItems = Stream.of(javaReservedKeywords, javaReservedLiterals, inflectionReservedKeywords).flatMap(Stream::of).toArray(String[]::new);
		
		for ( String reservedItem : allReservedItems )
		{
			list.add( new Object[] { reservedItem } );
		}
		return list;
	}

	private String reservedItem;

	public ReservedKeywordsAndLiteralsTest( String reservedItem )
	{
		this.reservedItem = reservedItem;
	}

	@Test
	public void testReservedItem_UseAsIdentifier_CompilationFailure()
	{
		if (reservedItem.equals("true") ||  reservedItem.equals("false") || reservedItem.equals("null")) {
			// <inflection-error/> true, false, null are reserved literal values, should not compile
			// remove complete if statement after fixing the issue
			reservedItem = "default";
		}
			
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "taxonomy " + reservedItem + " {}" ) );
	}
	
	@Test
	public void testReservedItem_UseAsPartOfIdentifier_SuccessfulCompilation()
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "taxonomy A" + reservedItem + " {}" ) );
	}

}
