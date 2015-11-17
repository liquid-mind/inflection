package ch.liquidmind.inflection.compiler.fixture;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.loader.TaxonomyLoader;

public final class CompilerTestUtility {

	public static CompilationJob createCompilationJob(CompilationMode mode, URL... urls) throws URISyntaxException {
		List<File> files = new ArrayList<>();
		for (URL url : urls) {
			files.add(new File(url.toURI()));
		}
		return new CompilationJob(TaxonomyLoader.getSystemTaxonomyLoader(), Files.createTempDir(), mode, files.toArray(new File [files.size()]));
	}
	
}
