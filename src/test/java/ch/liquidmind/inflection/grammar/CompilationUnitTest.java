package ch.liquidmind.inflection.grammar;

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
	public void testCompilationUnit_MultipleTaxonomies_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "taxonomy A {} taxonomy B {}" ) );
	}

	@Test
	public void testCompilationUnit_DuplicateTaxonomies_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "taxonomy A {} taxonomy A {}" ) );
	}

	@Test
	public void testCompilationUnit_FileNameWithInflectSuffix_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "test.inflect", null, "taxonomy A {}" ) );
	}

	@Test
	public void testCompilationUnit_FileNameWithOtherSuffix_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); // <inflection-error/> test.other is an illegal file name suffix, should not compile
		} , createInflectionFileMock( "test.other", null, "taxonomy A {}" ) );
	}

	@Test
	public void testCompilationUnit_PackageDeclarationInUnnamedPackage_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job ); // <inflection-error/> should compile, does in java
		} , createInflectionFileMock( null, "package a.b.c;" ) );
	}

	@Test
	public void testCompilationUnit_PackageDeclarationWithinDirectoryStructure_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a.b.c", "package a.b.c;" ) );
	}

	@Test
	public void testCompilationUnit_MultipleTaxonomiesInUnnamedPackage_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job ); // <inflection-error/> should compile, does in java
		} , createInflectionFileMock( null, "package a.b.c; taxonomy A {}" ), createInflectionFileMock( null, "package a.b.c; taxonomy B {}" ) );
	}

	@Test
	public void testCompilationUnit_MultipleTaxonomiesWithinDirectoryStructure_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a.b.c", "package a.b.c; taxonomy A {}" ), createInflectionFileMock( "a.b.c", "package a.b.c; taxonomy B {}" ) );
	}

	@Test
	public void testCompilationUnit_DuplicateTaxonomyInUnnamedPackage_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( null, "package a.b.c; taxonomy A {}" ), createInflectionFileMock( null, "package a.b.c; taxonomy A {}" ) );
	}

	@Test
	public void testCompilationUnit_DuplicateTaxonomyWithinDirectoryStructure_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "a.b.c", "package a.b.c; taxonomy A {}" ), createInflectionFileMock( "a.b.c", "package a.b.c; taxonomy A {}" ) );
	}

}
