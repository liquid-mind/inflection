package ch.liquidmind.inflection.grammar;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.InflectionFileMock;

public class TaxonomyUniquenessTest extends AbstractInflectionTest
{
	
	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyInSameFile_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {} taxonomy A {}" ) );
	}
	
	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyInDifferentFiles_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}
	
	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyOnClasspath_CompilationFailure() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "a", "package a; taxonomy A {}" ) };
		
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , classpath, createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}
	
	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesInSameFile_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {} taxonomy B {}" ) );
	}
	
	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesInDifferentFiles_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "a", "package a; taxonomy B {}" ) );
	}
	
	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesOnClasspath_SuccessfulCompilation() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "a", "package a; taxonomy A {}" ) };
		
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , classpath, createInflectionFileMock( "a", "package a; taxonomy B {}" ) );
	}

}
