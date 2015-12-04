package ch.liquidmind.inflection.compiler.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

import __java.io.__File;
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
	@Deprecated
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
	 * @param inflection defining the content of an *.inflect file
	 */
	public static CompilationJob createCompilationJob( String pakkage, String inflection ) {
		if (pakkage == null) {
			throw new IllegalArgumentException("package must not be null");
		}
		if (inflection == null) {
			throw new IllegalArgumentException("inflection must not be null");
		}
		File dir = Files.createTempDir();
		String[] parts = pakkage.split( "\\." );
		for (String part : parts) {
			dir = new File( dir, part );
			dir.mkdir();
		}
		File inflectFile = new File( dir.getAbsolutePath() + File.separatorChar + "temp.inflect" );
		try {
			__File.createNewFile( inflectFile );
			FileWriter fileWriter = new FileWriter( inflectFile ); 
			fileWriter.write( inflection );
			fileWriter.close();
		} catch (IOException exception) {
			throw new RuntimeException( exception );
		} 
		return new CompilationJob( TaxonomyLoader.getSystemTaxonomyLoader(), Files.createTempDir(), CompilationMode.BOOTSTRAP, inflectFile );
	}
	
	/**
	 * @param testClass defining the location of *.inflect files
	 * @param inflectFilenames 
	 * @return directory with compiled *.tax files
	 */
	@Deprecated
	public static File compileInflectionFile(Class<?> testClass, String... inflectFilenames) {
		CompilationJob job = createCompilationJob( testClass, inflectFilenames );
		InflectionCompiler.compile( job );
		File compiledTaxonomyDir = job.getTargetDirectory();
		assertTrue( compiledTaxonomyDir.exists() );
		assertTrue( job.getCompilationFaults().isEmpty() );
		return compiledTaxonomyDir;
	}
	
	/**
	 * @param testClass defining the location of *.inflect files
	 * @param inflectFilenames 
	 * @return directory with compiled *.tax files
	 */
	public static File compileInflection(String pakkage, String inflection) {
		CompilationJob job = createCompilationJob( pakkage, inflection );
		InflectionCompiler.compile( job );
		File compiledTaxonomyDir = job.getTargetDirectory();
		assertTrue( compiledTaxonomyDir.exists() );
		assertTrue( job.getCompilationFaults().isEmpty() );
		return compiledTaxonomyDir;
	}

}
