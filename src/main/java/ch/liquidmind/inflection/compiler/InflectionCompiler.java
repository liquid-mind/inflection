package ch.liquidmind.inflection.compiler;

import java.io.FileInputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import __java.io.__Closeable;
import __java.io.__FileInputStream;
import __org.antlr.v4.runtime.__ANTLRInputStream;
import ch.liquidmind.inflection.grammar.InflectionLexer;
import ch.liquidmind.inflection.grammar.InflectionParser;

public class InflectionCompiler
{
	public static void compile( CompilationJob job )
	{
		parse( job );
		
		if ( !job.hasCompilationErrors() )
			compilePass1( job );
		
		if ( !job.hasCompilationErrors() )
			compilePass2( job );
		
		if ( !job.hasCompilationErrors() )
			save( job );
	}
	
	private static void parse( CompilationJob job )
	{
		for ( CompilationUnit compilationUnit : job.getCompilationUnits() )
		{
			FileInputStream compilationUnitIs = __FileInputStream.__new( compilationUnit.getCompilationUnitRaw().getSourceFile() );
			
			try
			{
				parse( compilationUnit, compilationUnitIs );
			}
			finally
			{
				__Closeable.close( compilationUnitIs );
			}
		}
	}

	private static void parse( CompilationUnit compilationUnit, FileInputStream compilationUnitIs )
	{
		ANTLRInputStream antlrInputStream = __ANTLRInputStream.__new( compilationUnitIs );
		InflectionLexer lexer = new InflectionLexer( antlrInputStream );
		CommonTokenStream tokens = new CommonTokenStream( lexer );
		InflectionParser parser = new InflectionParser( tokens );
		parser.removeErrorListeners();
		InflectionErrorListener errorListener = new InflectionErrorListener( compilationUnit );
		parser.addErrorListener( errorListener );
		ParseTree tree = parser.compilationUnit();
		compilationUnit.getCompilationUnitParsed().setParseTree( tree );
		compilationUnit.getCompilationUnitParsed().setTokens( tokens );
	}
	
	private static void compilePass1( CompilationJob job )
	{}
	
	private static void compilePass2( CompilationJob job )
	{}
	
	private static void save( CompilationJob job )
	{}
}
