package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import com.google.common.io.Files;

import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.loader.TaxonomyLoader;

public class InflectionCompilerTest {

	@Test
	public void testCompile() throws Exception {
		URL url = this.getClass().getResource("CompilerTestModel.inflect");
		File file = new File(url.toURI());
		CompilationJob job = new CompilationJob(TaxonomyLoader.getSystemTaxonomyLoader(), Files.createTempDir(), CompilationMode.NORMAL, file);
		InflectionCompiler.compile(job);
		// TODO asserts
	}
	
}
