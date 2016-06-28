package ch.liquidmind.inflection;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.google.common.io.Files;

import __java.io.__Closeable;
import __java.io.__PrintStream;
import __java.net.__URI;
import __org.apache.commons.io.__FileUtils;
import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.print.InflectionPrinter;
import ch.liquidmind.inflection.proxy.ProxyGenerator;

public class InflectionBuild
{
	public static void main( String[] args )
	{
		Map< String, List< String > > options = InflectionPrinter.parseOptions( args );
		
		String classpath = options.get( "-classpath" ).get( 0 );
		String sourcepath = options.get( "-sourcepath" ).get( 0 );
		List< String > modelSourcesRegex = options.get( "-modelSourcesRegex" );
		List< String > modelClassesRegex = options.get( "-modelClassesRegex" );
		String target = options.get( "-target" ).get( 0 );
		List< String > annotations = options.get( "-annotations" );
		annotations = ( annotations == null ? new ArrayList< String >() : annotations );
		List< String > modes = options.get( "-mode" );
		String mode = ( modes == null ? CompilationMode.NORMAL.name() : modes.get( 0 ) );

		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd-HHmmss" );
		String timestampSuffix = dateFormat.format( new Date() );
		
		String taxonomyTarget = new File( target, "taxonomy" ).getAbsolutePath();
		String proxyTarget = new File( target, "proxy" ).getAbsolutePath();
		File diagnosticDir = new File( target, "diagnostic/" + timestampSuffix );
		String diagnosticNormal = new File( diagnosticDir, "normal" ).getAbsolutePath();
		String diagnosticVerbose = new File( diagnosticDir, "verbose" ).getAbsolutePath();
		List< String > classpathModel = Arrays.asList( classpath.split( System.getProperty( "path.separator" ) ) );
		List< String > sourcepathModel = Arrays.asList( sourcepath.split( System.getProperty( "path.separator" ) ) );
		String[] modelSourcesRegexAsArray = modelSourcesRegex.toArray( new String[ modelSourcesRegex.size() ] );
		String[] modelClassesRegexAsArray = modelClassesRegex.toArray( new String[ modelClassesRegex.size() ] );
		Set< File > modelFiles = getMatchingFiles( sourcepathModel, modelSourcesRegexAsArray );
		Set< File > inflectFiles = getMatchingFiles( sourcepathModel, ".*\\.inflect" );
		String modelTargetDir = Files.createTempDir().getAbsolutePath();
		List< String > classpathInflection = new ArrayList< String >();
		classpathInflection.addAll( classpathModel );
		classpathInflection.add( modelTargetDir );
		
		compileModelFiles( modelFiles, modelTargetDir, classpath );
		compileInflectionFiles( classpathInflection, modelClassesRegexAsArray, taxonomyTarget, inflectFiles, mode );
		generateProxies( classpathInflection, proxyTarget, taxonomyTarget, annotations );
		printTaxonomies( classpathInflection, taxonomyTarget, diagnosticNormal, true, false );
		printTaxonomies( classpathInflection, taxonomyTarget, diagnosticVerbose, true, true );
	}
	
	private static void printTaxonomies( List< String > classpath, String taxonomyTarget, String diagnosticTarget, boolean showSimpleNames, boolean showInherited )
	{
		List< String > taxonomyNames = getTaxonomyNames( taxonomyTarget );
		TaxonomyLoader loader = getTaxonomyLoader( classpath, taxonomyTarget );
		
		for ( String taxonomyName : taxonomyNames )
		{
			File diagnosticFile = new File( diagnosticTarget, taxonomyName.replace( ".tax", "" ).replace( ".", File.separator ) + ".txt" );
			__FileUtils.forceMkdir( null, new File( diagnosticFile.getParent() ) );
			PrintStream printStream = __PrintStream.__new( diagnosticFile );
			InflectionPrinter.printTaxonomy( loader, taxonomyName, printStream, showSimpleNames, showInherited );
			__Closeable.close( printStream );
		}
	}
	
	private static void generateProxies( List< String > classpath, String proxyTarget, String taxonomyTarget, List< String > annotations )
	{
		ProxyGenerator.generateProxies( getTaxonomyLoader( classpath, taxonomyTarget ), proxyTarget, getTaxonomyNames( taxonomyTarget ), annotations );
	}
	
	private static List< String > getTaxonomyNames( String taxonomyTarget )
	{
		File taxonomyTargetAsFile = new File( taxonomyTarget );
		Set< File > taxonomyFiles = getMatchingFiles( Arrays.asList( new String[] { taxonomyTarget } ), ".*\\.tax" );
		List< String > taxonomyNames = new ArrayList< String >();
		
		for ( File taxonomyFile : taxonomyFiles )
		{
			String relativeFileName = taxonomyFile.getAbsolutePath().substring( taxonomyTargetAsFile.getAbsolutePath().length() + 1 );
			taxonomyNames.add( relativeFileName.replaceAll( "/|\\\\", "." ).replace( ".tax", "" ) );
		}
		
		return taxonomyNames;
	}
	
	private static void compileInflectionFiles( List< String > classpath, String[] classFilters, String taxonomyTarget, Set< File > sourceFiles, String mode )
	{
		File taxonomyTargetAsFile = new File( taxonomyTarget );
		CompilationMode compilationMode = Enum.valueOf( CompilationMode.class, mode );
		File[] sourceFilesAsArray = sourceFiles.toArray( new File[ sourceFiles.size() ] );
		
		if ( !taxonomyTargetAsFile.exists() )
			taxonomyTargetAsFile.mkdirs();
		
		InflectionCompiler.compile( getTaxonomyLoader( classpath ), classFilters, taxonomyTargetAsFile, compilationMode, sourceFilesAsArray );
	}

	private static TaxonomyLoader getTaxonomyLoader( List< String > classpath, String taxonomyTarget )
	{
		URL[] urls = new URL[] { __URI.toURL( new File( taxonomyTarget ).toURI() ) };
		TaxonomyLoader parentLoader = getTaxonomyLoader( classpath );
		TaxonomyLoader loader = new TaxonomyLoader( parentLoader, new URLClassLoader( urls, parentLoader.getClassLoader() ) );
		
		return loader;
	}
	
	private static TaxonomyLoader getTaxonomyLoader( List< String > classpath )
	{
		List< URL > classpathUrls = new ArrayList< URL >();
		
		for ( String classpathEntry : classpath )
			classpathUrls.add( __URI.toURL( new File( classpathEntry ).toURI() ) );
		
		URL[] classpathUrlsAsArray = classpathUrls.toArray( new URL[ classpathUrls.size() ] );
		TaxonomyLoader loader = new TaxonomyLoader( TaxonomyLoader.getContextTaxonomyLoader(), new URLClassLoader( classpathUrlsAsArray ) );

		return loader;
	}
	
	private static Set< File > getMatchingFiles( List< String > classpath, String ... regexes )
	{
		Set< File > modelFiles = new HashSet< File >();
		
		for ( String cpEntry : classpath )
		{
			File cpEntryAsDir = new File( cpEntry );
			
			if ( !cpEntryAsDir.isDirectory() )
				continue;
			
			for ( File potentialMatch : Files.fileTreeTraverser().preOrderTraversal( new File( cpEntry ) ) )
			{
				for ( String modelRegex : regexes )
				{
					if ( potentialMatch.getAbsolutePath().matches( modelRegex ) )
						modelFiles.add( potentialMatch );
				}
			}
		}
		
		return modelFiles;
	}
	
	// modelFiles must use absolute names
	// destDir should be a temp directory
	private static boolean compileModelFiles( Collection< File > modelFiles, String destDir, String classpath )
	{
		Iterable< String > compilationOptions = Arrays.asList( new String[] {
			"-d", destDir,
			"-classpath", classpath,
			"-encoding", "UTF8"
		} );
		
		JavaCompiler compiler = getJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager( null, Locale.getDefault(), null );
		DiagnosticCollector< ? super JavaFileObject > diagnostics = new DiagnosticCollector< JavaFileObject >();
	
		Iterable< ? extends JavaFileObject > compilationUnits = fileManager.getJavaFileObjectsFromFiles( modelFiles );
		CompilationTask compilerTask = compiler.getTask( null, fileManager, diagnostics, compilationOptions, null, compilationUnits );
		boolean status = compilerTask.call();

		if ( !status )
		{
			for ( Diagnostic< ? > diagnostic : diagnostics.getDiagnostics() )
				System.out.format( "Error on line %d in %s", diagnostic.getLineNumber(), diagnostic );
		}
		
		return status;
	}

	private static JavaCompiler getJavaCompiler()
	{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		if ( compiler == null )
			throw new RuntimeException( "No Java compiler found; tools.jar not on classpath. Make sure that the 'java' command is from a JDK and not from a JRE." );
		
		return compiler;
	}
	
}
