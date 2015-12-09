package ch.liquidmind.inflection.compiler.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;

import __java.nio.file.__Files;
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
		Path rootDir = __Files.createTempDirectory( null, "inflect", new FileAttribute<?>[0] );
		List<File> inflectFileList = new ArrayList<>();
		int c = 0;
		for (InflectionFileMock mock : inflectionFileMocks) {
			Path currentDir = rootDir;
			if (mock.getPackageName() != null) {
				String[] parts = mock.getPackageName().split( "\\." );
				for (String part : parts) {
					currentDir = new File( currentDir.toFile(), part ).toPath();
					currentDir.toFile().mkdirs();
				}
			}
			String fileName = mock.getFileName();
			if (fileName == null) {
				fileName = c + ".inflect";
			}
			Path file = __Files.write(null, Paths.get(currentDir.toFile().getAbsolutePath(),fileName), mock.getContent().getBytes());
			inflectFileList.add( file.toFile() );
			c++;
		}
		return new CompilationJob( TaxonomyLoader.getSystemTaxonomyLoader(), __Files.createTempDirectory( null, "tax", new FileAttribute<?>[0] ).toFile(), CompilationMode.BOOTSTRAP, inflectFileList.toArray( new File[inflectFileList.size()] ) );
	}
		
	public static File compileInflection(InflectionFileMock... inflectionFileMocks) {
		CompilationJob job = createCompilationJob( inflectionFileMocks );
		InflectionCompiler.compile( job );
		File compiledTaxonomyDir = job.getTargetDirectory();
		assertTrue( compiledTaxonomyDir.exists() );
		assertTrue( job.getCompilationFaults().isEmpty() );
		return compiledTaxonomyDir;
	}
	
	public static void assertSuccessfulCompilation(CompilationJob job) {
		assertFalse( "Compilation units must exist", job.getCompilationUnits().isEmpty() );
		assertFalse( "Compilation errors must not exist", job.hasCompilationErrors() );		
	}
	
	public static void assertCompilationFailure(CompilationJob job) {
		assertTrue( job.hasCompilationErrors() );
		assertFalse( "Compilation errors must not exist", job.getCompilationFaults().isEmpty() );
		String message = job.getCompilationFaults().get( 0 ).getMessage();
		assertNotNull( "Fault message must exist", message != null );
		assertTrue( "", message.length() > 0 );		
	}

}
