package ch.liquidmind.inflection.compiler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.compiler.util.CompilerTestUtility;

public class InflectionCompilerTest {

	@Test
	public void testCompile_ValidFile_SuccessfulCompilation() throws Exception {
		CompilationJob job = CompilerTestUtility.createCompilationJob(CompilationMode.BOOTSTRAP, 
				this.getClass().getResource("InflectionCompilerTest_ValidFile.inflect"));
		InflectionCompiler.compile(job);
		assertFalse("Compilation units must exist", job.getCompilationUnits().isEmpty());
		assertTrue("Compilation errors must not exist", job.getCompilationFaults().isEmpty());
	}
	
	@Test
	@Ignore("IndexOutOfBoundsException is thrown when creating fault message") // TODO failing test
	public void testCompile_InvalidFile_SuccessfulFaultMessageGeneration() throws Exception {
		CompilationJob job = CompilerTestUtility.createCompilationJob(CompilationMode.BOOTSTRAP, 
				this.getClass().getResource("InflectionCompilerTest_InvalidFile.inflect"));
		InflectionCompiler.compile(job);
		assertFalse("Compilation errors must not exist", job.getCompilationFaults().isEmpty());
		String message = job.getCompilationFaults().get(0).createFaultMessage();
		assertNotNull("Fault message must exist", message != null);
		assertTrue("", message.length() > 0);
	}
	
	@Test
	@Ignore("Should not compile because child does not import view A") // TODO failing test
	public void testCompile_InheritanceAndImports_IllegalImport() throws Exception {
		CompilationJob job = CompilerTestUtility.createCompilationJob(CompilationMode.BOOTSTRAP, 
				this.getClass().getResource("InflectionCompilerTest_InheritanceAndImports_Parent.inflect"), 
				this.getClass().getResource("InflectionCompilerTest_InheritanceAndImports_IllegalChildImport.inflect"));
		InflectionCompiler.compile(job);
		assertFalse("compile errors expected since A cannot be resolved", job.getCompilationFaults().isEmpty());
	}
	
	@Test
	public void testCompile_InheritanceAndImports_SuccessfulCompilation() throws Exception {
		CompilationJob job = CompilerTestUtility.createCompilationJob(CompilationMode.BOOTSTRAP, 
				this.getClass().getResource("InflectionCompilerTest_InheritanceAndImports_Parent.inflect"), 
				this.getClass().getResource("InflectionCompilerTest_InheritanceAndImports_LegalChildImport.inflect"));
		InflectionCompiler.compile(job);
		assertTrue("successful compilation expected", job.getCompilationFaults().isEmpty());
	}
	
}
