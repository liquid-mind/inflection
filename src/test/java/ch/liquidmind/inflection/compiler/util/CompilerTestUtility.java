package ch.liquidmind.inflection.compiler.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.loader.TaxonomyLoader;

public final class CompilerTestUtility
{
	
	public static CompilationJob createCompilationJob( Class<?> testClass, String... resources ) throws URISyntaxException {
		if (testClass == null) {
			throw new IllegalArgumentException("testClass must not be null");
		}
		List< File > files = new ArrayList< >();
		for (String resource : resources) {
			URL url = testClass.getResource( resource );
			files.add( new File( url.toURI() ) );
		}
		return new CompilationJob( TaxonomyLoader.getSystemTaxonomyLoader(), Files.createTempDir(), CompilationMode.BOOTSTRAP, files.toArray( new File[ files.size() ] ) );
	}

}
