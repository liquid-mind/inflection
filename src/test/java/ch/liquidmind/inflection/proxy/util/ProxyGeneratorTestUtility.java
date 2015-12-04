package ch.liquidmind.inflection.proxy.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.google.common.io.Files;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.proxy.Proxy;
import ch.liquidmind.inflection.proxy.ProxyGenerator;
import ch.liquidmind.inflection.test.TestUtility;

public final class ProxyGeneratorTestUtility
{

	public static File createProxy(Taxonomy taxonomy, View view) {
		// Generate proxy
		File proxyDirs = Files.createTempDir();
		ProxyGenerator gen = new ProxyGenerator( proxyDirs, taxonomy );
		gen.generateTaxonomy();

	    String path = proxyDirs.toURI().getPath() + ProxyGenerator.getFullyQualifiedViewName( taxonomy, view ).replace( ".", File.separator ) + ".java";
	    File file = new File( path );
	    
	    // Compile proxy
	    File outputDir = Files.createTempDir();
	    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
	    Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects( file );
        Iterable<String> options = Arrays.asList("-d", outputDir.getAbsolutePath());
	    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
        Boolean result = task.call();
        assertTrue("Successful Proxy Compilation",  result );
        return outputDir;
	}
	
	public static Proxy loadProxy(File compileTaxonomyDir, File compiledProxyDir, String fullyQualifiedClassName) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		URLClassLoader taxonomyClassLoader = new URLClassLoader( TestUtility.convertToURLArray( compileTaxonomyDir ) , ClassLoader.getSystemClassLoader() );
		TaxonomyLoader taxonomyLoader = new TaxonomyLoader( TaxonomyLoader.getSystemTaxonomyLoader(), taxonomyClassLoader );
		
        // Load proxy
		URLClassLoader proxyClassLoader = new URLClassLoader( TestUtility.convertToURLArray( compiledProxyDir ), ClassLoader.getSystemClassLoader() );
		Class<?> proxy = proxyClassLoader.loadClass( fullyQualifiedClassName );
		proxyClassLoader.close();

		// Instantiate proxy
		TaxonomyLoader.setContextTaxonomyLoader( taxonomyLoader );
		Object proxyObject = proxy.newInstance();
		
		assertTrue( "must be instance of proxy", proxyObject instanceof Proxy );
		return (Proxy) proxyObject;
	}
	
}
