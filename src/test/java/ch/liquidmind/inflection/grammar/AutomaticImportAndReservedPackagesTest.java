package ch.liquidmind.inflection.grammar;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.AbstractInflectionListener;
import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

public class AutomaticImportAndReservedPackagesTest extends AbstractInflectionTest
{
	
	@Test
	public void testAutomaticImport_JavaLangFullyQualifiedImport_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "taxonomy A { view java.lang.String {} }" ) );
	}
	
	@Test
	public void testAutomaticImport_JavaLangSimpleNameImport_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "taxonomy A { view String {} }" ) );
	}
	
	@Test
	public void testAutomaticImport_JavaLangFullyQualifiedImport_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); // <inflection-error/> should not compile, class does not exist
		} , createInflectionFileMock( "taxonomy A { view java.lang.DoesNotExist {} }" ) );
	}
	
	@Test
	public void testAutomaticImport_JavaLangSimpleNameImport_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); // <inflection-error/> should not compile, class does not exist
		} , createInflectionFileMock( "taxonomy A { view DoesNotExist {} }" ) );
	}
	
	@Test
	public void testReservedPackage_JavaLangNotAllowed_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( AbstractInflectionListener.JAVA_LANG_PACKAGE, "package " + AbstractInflectionListener.JAVA_LANG_PACKAGE + "; taxonomy A {}" ) );
	}
	
	@Test
	public void testReservedPackage_InflectionNotAllowed_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( AbstractInflectionListener.CH_LIQUIDMIND_INFLECTION_PACKAGE, "package " + AbstractInflectionListener.CH_LIQUIDMIND_INFLECTION_PACKAGE + "; taxonomy A {}" ) );
	}

}
