package ch.liquidmind.inflection.compiler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.Ignore;
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
		assertFalse(job.getCompilationUnits().isEmpty());
	}
	
	@Test
	@Ignore("IndexOutOfBoundsException is thrown")
	public void testMessage() throws Exception {
		URL url = this.getClass().getResource("CompilerTestModel.inflect");
		File file = new File(url.toURI());
		CompilationJob job = new CompilationJob(TaxonomyLoader.getSystemTaxonomyLoader(), Files.createTempDir(), CompilationMode.BOOTSTRAP, file);
		InflectionCompiler.compile(job);
		assertFalse(job.getCompilationFaults().isEmpty());
		String message = job.getCompilationFaults().get(0).createFaultMessage();
		assertNotNull(message != null);
		assertTrue(message.length() > 0);
	}
	
}
