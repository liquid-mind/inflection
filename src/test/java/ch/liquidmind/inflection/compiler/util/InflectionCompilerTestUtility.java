package ch.liquidmind.inflection.compiler.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

import __java.io.__File;
import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.test.InflectionFileMock;

public final class InflectionCompilerTestUtility
{
		
	public static CompilationJob createCompilationJob( InflectionFileMock... inflectionFileMocks ) {
		if (inflectionFileMocks == null) {
			throw new IllegalArgumentException("inflectionFileMocks must not be null");
		}
		File root = Files.createTempDir();
		List<File> inflectFileList = new ArrayList<>();
		int c = 0;
		for (InflectionFileMock mock : inflectionFileMocks) {
			File dir = root;
			String[] parts = mock.getPackageName().split( "\\." );
			for (String part : parts) {
				dir = new File( dir, part );
				dir.mkdir();
			}
			File inflectFile = new File( dir.getAbsolutePath() + File.separatorChar + c + ".inflect" );
			// TODO how can I use __FileWriter?
			try {
				__File.createNewFile( inflectFile );
				FileWriter fileWriter = new FileWriter( inflectFile ); 
				fileWriter.write( mock.getContent() );
				fileWriter.close();
				inflectFileList.add( inflectFile );
			} catch (IOException exception) {
				throw new RuntimeException( exception );
			} 
			c++;
		}
		return new CompilationJob( TaxonomyLoader.getSystemTaxonomyLoader(), Files.createTempDir(), CompilationMode.BOOTSTRAP, inflectFileList.toArray( new File[inflectFileList.size()] ) );
	}
		
	public static File compileInflection(InflectionFileMock... inflectionFileMocks) {
		CompilationJob job = createCompilationJob( inflectionFileMocks );
		InflectionCompiler.compile( job );
		File compiledTaxonomyDir = job.getTargetDirectory();
		assertTrue( compiledTaxonomyDir.exists() );
		assertTrue( job.getCompilationFaults().isEmpty() );
		return compiledTaxonomyDir;
	}

}
