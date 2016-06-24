package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.io.FileInputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import __java.io.__Closeable;
import __java.io.__FileInputStream;
import __org.antlr.v4.runtime.__ANTLRInputStream;
import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.grammar.InflectionLexer;
import ch.liquidmind.inflection.grammar.InflectionParser;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class InflectionCompiler
{
	public static final String INFLECT_SUFFIX = ".inflect";
	
	private static ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
	
	// TODO Introduce apache commons cli, analogous to deflector.
	public static void main( String[] args )
	{
		File targetDir = new File( args[ 0 ] );
		CompilationMode compilationMode = Enum.valueOf( CompilationMode.class, args[ 1 ] );
		
		File[] sourceFiles = new File[ args.length - 2 ];
		
		for ( int i = 0 ; i < sourceFiles.length ; ++i )
			sourceFiles[ i ] = new File( args[ i + 2 ] );
		
		
		compile( targetDir, compilationMode, sourceFiles );
	}

	public static void compile( File targetDir, CompilationMode compilationMode, File[] sourceFiles )
	{
		compile( TaxonomyLoader.getSystemTaxonomyLoader(), targetDir, compilationMode, sourceFiles );
	}
	
	public static void compile( TaxonomyLoader taxonomyLoader, File targetDir, CompilationMode compilationMode, File[] sourceFiles )
	{
		TaxonomyLoader previousTaxonomyLoader = TaxonomyLoader.getContextTaxonomyLoader();
		ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		
		try
		{
			TaxonomyLoader.setContextTaxonomyLoader( taxonomyLoader );
			Thread.currentThread().setContextClassLoader( taxonomyLoader.getClassLoader() );
			
			compileWithContextLoaders( taxonomyLoader, targetDir, compilationMode, sourceFiles );
		}
		finally
		{
			TaxonomyLoader.setContextTaxonomyLoader( previousTaxonomyLoader );
			Thread.currentThread().setContextClassLoader( previousClassLoader );
		}
	}

	public static void compileWithContextLoaders( TaxonomyLoader loader, File targetDir, CompilationMode compilationMode, File[] sourceFiles )
	{
		long timeBefore = System.currentTimeMillis();
		CompilationJob job = new CompilationJob( loader, targetDir, compilationMode, sourceFiles );
		
		try
		{
			InflectionCompiler.compile( job );
		}
		finally
		{
			job.printFaults();
		}
		
		if ( job.hasCompilationErrors() )
			return;
		
		long timeAfter = System.currentTimeMillis();
		float timeDeltaInSecs = ( timeAfter - timeBefore ) / 1000F;
		int compilationUnitCount = job.getCompilationUnits().size();
		int taxonomyCount = job.getKnownTaxonomiesCompiled().size();
		
		System.out.format( "Compiled %d taxonomies from %d file(s) in %.3f seconds.\n", taxonomyCount, compilationUnitCount, timeDeltaInSecs );
	}
	
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
		parser.addErrorListener( compilationUnit.getErrorListener() );
		ParseTree tree = parser.compilationUnit();
		compilationUnit.getCompilationUnitParsed().setParseTree( tree );
		compilationUnit.getCompilationUnitParsed().setTokens( tokens );
	}
	
	private static void compilePass1( CompilationJob job )
	{
		for ( CompilationUnit compilationUnit : job.getCompilationUnits() )
			parseTreeWalker.walk( new Pass1Listener( compilationUnit ), compilationUnit.getCompilationUnitParsed().getParseTree() );
	}
	
	private static void compilePass2( CompilationJob job )
	{
		for ( CompilationUnit compilationUnit : job.getCompilationUnits() )
			parseTreeWalker.walk( new Pass2Listener( compilationUnit ), compilationUnit.getCompilationUnitParsed().getParseTree() );
	}
	
	private static void save( CompilationJob job )
	{
		for ( CompilationUnit compilationUnit : job.getCompilationUnits() )
			for ( TaxonomyCompiled taxonomyCompiled : compilationUnit.getCompilationUnitCompiled().getTaxonomiesCompiled() )
				taxonomyCompiled.save( job.getTargetDirectory() );
	}
}
