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
import ch.liquidmind.inflection.grammar.InflectionLexer;
import ch.liquidmind.inflection.grammar.InflectionParser;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class InflectionCompiler
{
	private File[] compilationUnits;
	private File targetLocation;
	private TaxonomyLoader taxonomyLoader;
	private Map< String, TaxonomyCompiled > taxonomiesCompiled;
	private List< ParsedCompilationUnit > parsedCompilationUnits;
	
	public InflectionCompiler( File[] compilationUnits, File targetLocation )
	{
		this( compilationUnits, targetLocation, TaxonomyLoader.getContextTaxonomyLoader() );
	}
	
	public InflectionCompiler( File[] compilationUnits, File targetLocation, TaxonomyLoader taxonomyLoader )
	{
		super();
		this.compilationUnits = compilationUnits;
		this.targetLocation = targetLocation;
		this.taxonomyLoader = taxonomyLoader;
	}
	
	@SuppressWarnings( "unused" )
	public void compile()
	{
		taxonomiesCompiled = new HashMap< String, TaxonomyCompiled >();
		parsedCompilationUnits = new ArrayList< ParsedCompilationUnit >();
		
		parse( compilationUnits );
		
		for ( ParsedCompilationUnit parsedCompilationUnit : parsedCompilationUnits )
			compilePass1( parsedCompilationUnit );
		
		for ( ParsedCompilationUnit parsedCompilationUnit : parsedCompilationUnits )
			compilePass2( parsedCompilationUnit, taxonomyLoader );
		
		for ( TaxonomyCompiled taxonomyCompiled : taxonomiesCompiled.values() )
			;	// TODO save taxonomyCompiled
	}
	
	private void compilePass1( ParsedCompilationUnit parsedCompilationUnit )
	{
		ParseTreeWalker walker = new ParseTreeWalker();
		CompilePass1Listener listener = new CompilePass1Listener( parsedCompilationUnit.getCompilationUnit(), parsedCompilationUnit.getTokens(), parsedCompilationUnit.getPackageName(), taxonomiesCompiled, getBootstrap() );
		walker.walk( listener, parsedCompilationUnit.getTree() );
	}
	
	private void compilePass2( ParsedCompilationUnit parsedCompilationUnit, TaxonomyLoader taxonomyLoader )
	{
		ParseTreeWalker walker = new ParseTreeWalker();
		CompilePass2Listener listener = new CompilePass2Listener( parsedCompilationUnit.getCompilationUnit(), parsedCompilationUnit.getTokens(), parsedCompilationUnit.getPackageName(), taxonomiesCompiled, taxonomyLoader, getBootstrap() );
		walker.walk( listener, parsedCompilationUnit.getTree() );
	}
	
	private void parse( File[] compilationUnits )
	{
		for ( File compilationUnit : compilationUnits )
		{
			FileInputStream compilationUnitIs = __FileInputStream.__new( compilationUnit );
			
			ParsedCompilationUnit parsedCompilationUnit = new ParsedCompilationUnit();
			parsedCompilationUnits.add( parsedCompilationUnit );
			
			try
			{
				parse( compilationUnit, compilationUnitIs, parsedCompilationUnit );
			}
			finally
			{
				__Closeable.close( compilationUnitIs );
			}
		}
	}
	
	private void parse( File compilationUnit, InputStream inputStream, ParsedCompilationUnit parsedCompilationUnit )
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
		PackageListener packageListener = new PackageListener( compilationUnit, tokens, getBootstrap() );
		walker.walk( packageListener, tree );
		
		parsedCompilationUnit.setCompilationUnit( compilationUnit );
		parsedCompilationUnit.setTokens( tokens );
		parsedCompilationUnit.setTree( tree );
		parsedCompilationUnit.setPackageName( packageListener.getPackageName() );
	}
	
	private boolean getBootstrap()
	{
		return this instanceof BootstrapCompiler;
	}
}
