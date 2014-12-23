package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import __java.io.__Closeable;
import __java.io.__FileInputStream;
import __org.antlr.v4.runtime.__ANTLRInputStream;
import ch.liquidmind.inflection.InflectionResourceLoader;
import ch.liquidmind.inflection.grammar.InflectionLexer;
import ch.liquidmind.inflection.grammar.InflectionParser;

public class InflectionCompiler
{
	private static class ParsedCompilationUnit
	{
		private File compilationUnit;
		private CommonTokenStream tokens;
		private ParseTree tree;
		private String packageName;
		
		public File getCompilationUnit()
		{
			return compilationUnit;
		}

		public void setCompilationUnit( File compilationUnit )
		{
			this.compilationUnit = compilationUnit;
		}

		public CommonTokenStream getTokens()
		{
			return tokens;
		}
		
		public void setTokens( CommonTokenStream tokens )
		{
			this.tokens = tokens;
		}
		
		public ParseTree getTree()
		{
			return tree;
		}
		
		public void setTree( ParseTree tree )
		{
			this.tree = tree;
		}
		
		public String getPackageName()
		{
			return packageName;
		}
		
		public void setPackageName( String packageName )
		{
			this.packageName = packageName;
		}
	}
	
	private static Map< String, InflectionResourceCompiled > inflectionResourcesCompiled;
	private static List< ParsedCompilationUnit > parsedCompilationUnits;

	public static void compile( File[] compilationUnits, File targetLocation )
	{
		compile( compilationUnits, targetLocation, InflectionResourceLoader.getContextInflectionResourceLoader(), false );
	}

	public static void compile( File[] compilationUnits, File targetLocation, InflectionResourceLoader inflectionResourceLoader )
	{
		compile( compilationUnits, targetLocation, inflectionResourceLoader, false );
	}
	
	static void compile( File[] compilationUnits, File targetLocation, InflectionResourceLoader inflectionResourceLoader, boolean bootstrap )
	{
		inflectionResourcesCompiled = new HashMap< String, InflectionResourceCompiled >();
		parsedCompilationUnits = new ArrayList< ParsedCompilationUnit >();
		
		parse( compilationUnits, bootstrap );
		
		for ( ParsedCompilationUnit parsedCompilationUnit : parsedCompilationUnits )
			compilePass1( parsedCompilationUnit, bootstrap );
		
		for ( ParsedCompilationUnit parsedCompilationUnit : parsedCompilationUnits )
			compilePass2( parsedCompilationUnit, inflectionResourceLoader, bootstrap );
		
		for ( InflectionResourceCompiled inflectionResourceCompiled : inflectionResourcesCompiled.values() )
			InflectionResourceCompiled.save( targetLocation, inflectionResourceCompiled );
	}
	
	private static void compilePass1( ParsedCompilationUnit parsedCompilationUnit, boolean bootstrap )
	{
		ParseTreeWalker walker = new ParseTreeWalker();
		CompilePass1Listener listener = new CompilePass1Listener( parsedCompilationUnit.getCompilationUnit(), parsedCompilationUnit.getTokens(), parsedCompilationUnit.getPackageName(), inflectionResourcesCompiled, bootstrap );
		walker.walk( listener, parsedCompilationUnit.getTree() );
	}
	
	private static void compilePass2( ParsedCompilationUnit parsedCompilationUnit, InflectionResourceLoader inflectionResourceLoader, boolean bootstrap )
	{
		ParseTreeWalker walker = new ParseTreeWalker();
		CompilePass2Listener listener = new CompilePass2Listener( parsedCompilationUnit.getCompilationUnit(), parsedCompilationUnit.getTokens(), parsedCompilationUnit.getPackageName(), inflectionResourcesCompiled, inflectionResourceLoader, bootstrap );
		walker.walk( listener, parsedCompilationUnit.getTree() );
	}
	
	private static void parse( File[] compilationUnits, boolean bootstrap )
	{
		for ( File compilationUnit : compilationUnits )
		{
			FileInputStream compilationUnitIs = __FileInputStream.__new( compilationUnit );
			
			ParsedCompilationUnit parsedCompilationUnit = new ParsedCompilationUnit();
			parsedCompilationUnits.add( parsedCompilationUnit );
			
			try
			{
				parse( compilationUnit, compilationUnitIs, parsedCompilationUnit, bootstrap );
			}
			finally
			{
				__Closeable.close( compilationUnitIs );
			}
		}
	}
	
	private static void parse( File compilationUnit, InputStream inputStream, ParsedCompilationUnit parsedCompilationUnit, boolean bootstrap )
	{
		ANTLRInputStream antlrInputStream = __ANTLRInputStream.__new( inputStream );

		InflectionLexer lexer = new InflectionLexer( antlrInputStream );
		CommonTokenStream tokens = new CommonTokenStream( lexer );
		InflectionParser parser = new InflectionParser( tokens );
		parser.removeErrorListeners();
		InflectionErrorListener errorListener = new InflectionErrorListener( compilationUnit );
		parser.addErrorListener( errorListener );
		ParseTree tree = parser.compilationUnit();
		
		if ( errorListener.syntaxErrorOccured() )
			throw new RuntimeException( "Syntax error in input stream: cannot parse view definitions." );
		
		ParseTreeWalker walker = new ParseTreeWalker();
		PackageListener packageListener = new PackageListener( compilationUnit, tokens, bootstrap );
		walker.walk( packageListener, tree );
		
		parsedCompilationUnit.setCompilationUnit( compilationUnit );
		parsedCompilationUnit.setTokens( tokens );
		parsedCompilationUnit.setTree( tree );
		parsedCompilationUnit.setPackageName( packageListener.getPackageName() );
	}
}
