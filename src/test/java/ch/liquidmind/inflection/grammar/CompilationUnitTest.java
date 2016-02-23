package ch.liquidmind.inflection.grammar;

import static ch.liquidmind.inflection.test.mock.AbstractFileMock.UNNAMED_PACKAGE;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

public class CompilationUnitTest extends AbstractInflectionTest
{

	@Test
	public void testCompilationUnit_Empty_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "" ) );
	}

	@Test
	public void testCompilationUnit_FileNameWithInflectSuffix_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "test.inflect", UNNAMED_PACKAGE, "taxonomy A {}" ) );
	}

	@Test
	public void testCompilationUnit_FileNameWithOtherSuffix_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); // <inflection-error/> test.other is an illegal file name suffix, should not compile
		} , createInflectionFileMock( "test.other", UNNAMED_PACKAGE, "taxonomy A {}" ) );
	}

	@Test
	public void testCompilationUnit_PackageDeclarationInUnnamedPackage_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job ); // <inflection-error/> should compile, does in java
		} , createInflectionFileMock( UNNAMED_PACKAGE, "package a.b.c;" ) );
	}

	@Test
	public void testCompilationUnit_PackageDeclarationWithinDirectoryStructure_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a.b.c", "package a.b.c;" ) );
	}

}
