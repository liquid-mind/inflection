package ch.liquidmind.inflection.compiler.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

import __java.net.__URL;
import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.loader.TaxonomyLoader;

public final class InflectionCompilerTestUtility
{
	
	/**
	 * @param testClass defining the location of *.inflect files
	 * @param inflectFilenames 
	 */
	public static CompilationJob createCompilationJob( Class<?> testClass, String... inflectFilenames ) {
		if (testClass == null) {
			throw new IllegalArgumentException("testClass must not be null");
		}
		List< File > files = new ArrayList< >();
		for (String resource : inflectFilenames) {
			URL url = testClass.getResource( resource );
			files.add( new File( __URL.toURI( url ) ) );
		}
		return new CompilationJob( TaxonomyLoader.getSystemTaxonomyLoader(), Files.createTempDir(), CompilationMode.BOOTSTRAP, files.toArray( new File[ files.size() ] ) );
	}
	
	/**
	 * @param testClass defining the location of *.inflect files
	 * @param inflectFilenames 
	 * @return directory with compiled *.tax files
	 */
	public static File compileInflectionFile(Class<?> testClass, String... inflectFilenames) {
		CompilationJob job = createCompilationJob( testClass, inflectFilenames );
		InflectionCompiler.compile( job );
		File compiledTaxonomyDir = job.getTargetDirectory();
		assertTrue( compiledTaxonomyDir.exists() );
		assertTrue( job.getCompilationFaults().isEmpty() );
		return compiledTaxonomyDir;
	}

}
