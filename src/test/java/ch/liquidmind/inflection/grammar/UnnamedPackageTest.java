package ch.liquidmind.inflection.grammar;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

public class UnnamedPackageTest extends AbstractInflectionTest
{

	@Test
	public void testUnnamedPackage_DuplicateTaxonomyInUnnamedPackage_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( null, "taxonomy A {}" ), createInflectionFileMock( null, "taxonomy A {}" ) );
	}

	@Test
	public void testUnnamedPackage_DuplicateTaxonomyInUnnamedPackageAndNamedPackage_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( null, "taxonomy A {}" ), createInflectionFileMock( "a.b.c", "package a.b.c; taxonomy A {}" ) );
	}

	@Test
	public void testUnnamedPackage_ReferenceTaxonomyInUnnamedPackageFromUnnamedPackageWithinSameFile_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job ); // <inflection-error/> should compile, does in java
		} , createInflectionFileMock( null, "taxonomy A {} taxonomy B extends A {}" ) );
	}

	@Test
	public void testUnnamedPackage_ReferenceTaxonomyInUnnamedPackageFromUnnamedPackageWithinSameFileReverseOrder_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job ); // <inflection-error/> should compile, does in java
		} , createInflectionFileMock( null, "taxonomy B extends A {} taxonomy A {}" ) );
	}

	@Test
	public void testUnnamedPackage_ReferenceTaxonomyInUnnamedPackageFromUnnamedPackageWithDifferentFiles_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job ); // <inflection-error/> should compile, does in java
		} , createInflectionFileMock( null, "taxonomy A {}" ), createInflectionFileMock( null, "taxonomy B extends A {}" ) );
	}

	@Test
	public void testUnnamedPackage_ReferenceTaxonomyInUnnamedPackageFromNamedPackage_CompilationFailure() throws Exception
	{
		// Java Language Spec: It is a compile time error to import a type from the unnamed package.
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( null, "taxonomy A {}" ), createInflectionFileMock( "a.b.c", "package a.b.c; taxonomy B extends A {}" ) );
	}

	@Test
	public void testUnnamedPackage_ReferenceTaxonomyFromNamedPackageInUnnamedPackage_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a.b.c", "package a.b.c; taxonomy A {}" ), createInflectionFileMock( null, "taxonomy B extends a.b.c.A {}" ) );
	}

}
