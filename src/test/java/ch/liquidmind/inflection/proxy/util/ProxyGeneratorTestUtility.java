package ch.liquidmind.inflection.proxy.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.google.common.io.Files;

import __java.lang.__Class;
import __java.lang.__ClassLoader;
import __java.net.__URLClassLoader;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.proxy.Proxy;
import ch.liquidmind.inflection.proxy.ProxyGenerator;
import ch.liquidmind.inflection.test.TestUtility;

public final class ProxyGeneratorTestUtility
{

	public static File createProxy( Taxonomy taxonomy, View view )
	{
		// Generate proxy
		File proxyDirs = Files.createTempDir();
		ProxyGenerator gen = new ProxyGenerator( proxyDirs, taxonomy );
		gen.generateTaxonomy();

		String path = proxyDirs.toURI().getPath() + ProxyGenerator.getFullyQualifiedViewName( taxonomy, view ).replace( ".", File.separator ) + ".java";
		File file = new File( path );

		// Compile proxy
		File outputDir = Files.createTempDir();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector< JavaFileObject > diagnostics = new DiagnosticCollector< JavaFileObject >();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnostics, null, null );
		Iterable< ? extends JavaFileObject > compilationUnits = fileManager.getJavaFileObjects( file );
		Iterable< String > options = Arrays.asList( "-d", outputDir.getAbsolutePath(), "-classpath", System.getProperty( "java.class.path" ) );
		JavaCompiler.CompilationTask task = compiler.getTask( null, fileManager, diagnostics, options, null, compilationUnits );
		Boolean result = task.call();
		assertTrue( "Successful Proxy Compilation: " + diagnostics.getDiagnostics().toString(), result );
		return outputDir;
	}

	/**
	 * Creates a proxy instance. You may need to set a ContextTaxonomyLoader using {@link TaxonomyLoader#setContextTaxonomyLoader} before calling this method.
	 * 
	 * @param compiledProxyDir location of compiled proxy files
	 * @param fullyQualifiedClassName fully qualified proxy class name
	 * @return a proxy instance
	 */
	public static Proxy loadProxy( File compiledProxyDir, String fullyQualifiedClassName )
	{
		// Load proxy
		URLClassLoader proxyClassLoader = new URLClassLoader( TestUtility.convertToURLArray( compiledProxyDir ), ClassLoader.getSystemClassLoader() );
		Class< ? > proxy = __ClassLoader.loadClass( proxyClassLoader, fullyQualifiedClassName );
		__URLClassLoader.close( proxyClassLoader );

		// Instantiate proxy
		Object proxyObject = __Class.newInstance( proxy );
		assertTrue( "must be instance of proxy", proxyObject instanceof Proxy );

		return (Proxy)proxyObject;
	}

}
