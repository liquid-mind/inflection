package ch.liquidmind.inflection.compiler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;

public class InflectionCompilerTest {

	@Test
	public void testCompile() throws Exception {
		CompilationJob job = CompilerTestUtility.createCompilationJob(CompilationMode.NORMAL, 
				this.getClass().getResource("CompilerTestModel.inflect"));
		InflectionCompiler.compile(job);
		assertFalse(job.getCompilationUnits().isEmpty());
	}
	
	@Test
	@Ignore("IndexOutOfBoundsException is thrown")
	public void testMessage() throws Exception {
		CompilationJob job = CompilerTestUtility.createCompilationJob(CompilationMode.BOOTSTRAP, 
				this.getClass().getResource("CompilerTestModel.inflect"));
		InflectionCompiler.compile(job);
		assertFalse(job.getCompilationFaults().isEmpty());
		String message = job.getCompilationFaults().get(0).createFaultMessage();
		assertNotNull(message != null);
		assertTrue(message.length() > 0);
	}
	
	@Test
	@Ignore("Should not compile")
	public void testInheritance1() throws Exception {
		CompilationJob job = CompilerTestUtility.createCompilationJob(CompilationMode.BOOTSTRAP, 
				this.getClass().getResource("InheritanceTestModelA.inflect"), 
				this.getClass().getResource("InheritanceTestModelB.inflect"));
		InflectionCompiler.compile(job);
		assertFalse(job.getCompilationFaults().isEmpty());
	}
	
	@Test
	public void testInheritance2() throws Exception {
		CompilationJob job = CompilerTestUtility.createCompilationJob(CompilationMode.BOOTSTRAP, 
				this.getClass().getResource("InheritanceTestModelC.inflect"), 
				this.getClass().getResource("InheritanceTestModelD.inflect"));
		InflectionCompiler.compile(job);
		assertFalse(job.getCompilationFaults().isEmpty());
	}
	
}
