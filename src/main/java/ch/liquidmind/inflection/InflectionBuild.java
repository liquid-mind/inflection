package ch.liquidmind.inflection;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import __java.net.__URI;
import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.proxy.ProxyGenerator;
import ch.liquidmind.inflection.util.InflectionPrinter;

public class InflectionBuild
{
	// -classpath: includes .infect root dirs, model file root dirs and model file dependencies
	// -regular expressions for filtering model classes
	// -taxonomies output location
	// -proxies output location
	
	// EXAMPLE
	// -classpath /Users/john/Documents/workspace-liquid-mind/inflection/examples/helloworld/src/main/java:/Users/john/Documents/workspace-liquid-mind/inflection/examples/helloworld/src/main/resources
	// -modelRegex ch/inflection/examples/helloworld/model/.*
	public static void main( String[] args )
	{
		Map< String, List< String > > options = InflectionPrinter.parseOptions( args );
		
		String classpath = options.get( "-classpath" ).get( 0 );
		String sourcepath = options.get( "-sourcepath" ).get( 0 );
		List< String > modelRegex = options.get( "-modelRegex" );
		String taxonomyTarget = options.get( "-taxonomyTarget" ).get( 0 );
		String proxyTarget = options.get( "-proxyTarget" ).get( 0 );
		List< String > annotations = options.get( "-annotations" );
		annotations = ( annotations == null ? new ArrayList< String >() : annotations );
		List< String > modes = options.get( "-mode" );
		String mode = ( modes == null ? CompilationMode.NORMAL.name() : modes.get( 0 ) );
		
		List< String > classpathModel = Arrays.asList( classpath.split( ":|;" ) );
		List< String > sourcepathModel = Arrays.asList( sourcepath.split( ":|;" ) );
		String[] modelRegexAsArray = modelRegex.toArray( new String[ modelRegex.size() ] );
		Set< File > modelFiles = getMatchingFiles( sourcepathModel, modelRegexAsArray );
		Set< File > inflectFiles = getMatchingFiles( sourcepathModel, ".*\\.inflect" );
		String modelTargetDir = Files.createTempDir().getAbsolutePath();
		List< String > classpathInflection = new ArrayList< String >();
		classpathInflection.addAll( classpathModel );
		classpathInflection.add( modelTargetDir );
		
		compileModelFiles( modelFiles, modelTargetDir, classpath );
		compileInflectionFiles( classpathInflection, taxonomyTarget, inflectFiles, mode );
		generateProxies( classpathInflection, proxyTarget, taxonomyTarget, annotations );
	}
	
	private static void generateProxies( List< String > classpath, String proxyTarget, String taxonomyTarget, List< String > annotations )
	{
		File taxonomyTargetAsFile = new File( taxonomyTarget );
		Set< File > taxonomyFiles = getMatchingFiles( Arrays.asList( new String[] { taxonomyTarget } ), ".*\\.tax" );
		List< String > taxonomyNames = new ArrayList< String >();
		
		for ( File taxonomyFile : taxonomyFiles )
		{
			String relativeFileName = taxonomyFile.getAbsolutePath().substring( taxonomyTargetAsFile.getAbsolutePath().length() + 1 );
			taxonomyNames.add( relativeFileName.replaceAll( "/|\\\\", "." ).replaceAll( "\\.tax", "" ) );
		}
		
		ProxyGenerator.generateProxies( getTaxonomyLoader( classpath, taxonomyTarget ), proxyTarget, taxonomyNames, annotations );
	}
	
	private static void compileInflectionFiles( List< String > classpath, String taxonomyTarget, Set< File > sourceFiles, String mode )
	{
		File taxonomyTargetAsFile = new File( taxonomyTarget );
		CompilationMode compilationMode = Enum.valueOf( CompilationMode.class, mode );
		File[] sourceFilesAsArray = sourceFiles.toArray( new File[ sourceFiles.size() ] );
		
		CompilationJob job = new CompilationJob( getTaxonomyLoader( classpath ), taxonomyTargetAsFile, compilationMode, sourceFilesAsArray );
		InflectionCompiler.compile( job );
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
