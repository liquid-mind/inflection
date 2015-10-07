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
	private List< CompilationUnit > compilationUnits = new ArrayList< CompilationUnit >();
	@SuppressWarnings( "unused" )
	private File targetLocation;
	private TaxonomyLoader taxonomyLoader;
	private Map< String, TaxonomyCompiled > taxonomiesCompiled = new HashMap< String, TaxonomyCompiled >();
	
	public InflectionCompiler( String[] compilationUnitNames, String targetLocation )
	{
		this( compilationUnitNames, targetLocation, TaxonomyLoader.getContextTaxonomyLoader() );
	}
	
	public InflectionCompiler( String[] compilationUnitNames, String targetLocation, TaxonomyLoader taxonomyLoader )
	{
		super();
		
		for ( String compilationUnitName : compilationUnitNames )
			compilationUnits.add( new CompilationUnit( new File( compilationUnitName ) ) );

		this.targetLocation = new File( targetLocation );
		this.taxonomyLoader = taxonomyLoader;
	}
	
	@SuppressWarnings( "unused" )
	public void compile()
	{
		parse();
		
		for ( CompilationUnit compilationUnit : compilationUnits )
			compilePass1( compilationUnit );
		
		for ( CompilationUnit compilationUnit : compilationUnits )
			compilePass2( compilationUnit, taxonomyLoader );
		
		for ( TaxonomyCompiled taxonomyCompiled : taxonomiesCompiled.values() )
			;	// TODO save taxonomyCompiled
	}
	
	private void compilePass1( CompilationUnit compilationUnit )
	{
		ParseTreeWalker walker = new ParseTreeWalker();
		CompilePass1Listener listener = new CompilePass1Listener( compilationUnit, taxonomiesCompiled, getBootstrap() );
		walker.walk( listener, compilationUnit.getTree() );
	}
	
	private void compilePass2( CompilationUnit compilationUnit, TaxonomyLoader taxonomyLoader )
	{
		ParseTreeWalker walker = new ParseTreeWalker();
		CompilePass2Listener listener = new CompilePass2Listener( compilationUnit, taxonomiesCompiled, getBootstrap(), taxonomyLoader );
		walker.walk( listener, compilationUnit.getTree() );
	}
	
	private void parse()
	{
		for ( CompilationUnit compilationUnit : compilationUnits )
		{
			FileInputStream inputStream = __FileInputStream.__new( compilationUnit.getFile() );
			
			try
			{
				parse( compilationUnit, inputStream );
			}
			finally
			{
				__Closeable.close( inputStream );
			}
		}
	}
	
	private void parse( CompilationUnit compilationUnit, InputStream inputStream )
	{
		ANTLRInputStream antlrInputStream = __ANTLRInputStream.__new( inputStream );

		InflectionLexer lexer = new InflectionLexer( antlrInputStream );
		CommonTokenStream tokens = new CommonTokenStream( lexer );
		InflectionParser parser = new InflectionParser( tokens );
		parser.removeErrorListeners();
		InflectionErrorListener errorListener = new InflectionErrorListener( compilationUnit.getFile() );
		parser.addErrorListener( errorListener );
		ParseTree tree = parser.compilationUnit();
		
		if ( errorListener.syntaxErrorOccured() )
			throw new RuntimeException( "Syntax error in input stream: cannot parse view definitions." );
		
		compilationUnit.setTokens( tokens );
		compilationUnit.setTree( tree );
		ParseTreeWalker walker = new ParseTreeWalker();
		PackageListener packageListener = new PackageListener( compilationUnit, getBootstrap() );
		walker.walk( packageListener, tree );
	}
	
	private boolean getBootstrap()
	{
		return this instanceof BootstrapCompiler;
	}
}
