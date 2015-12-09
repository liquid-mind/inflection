package ch.liquidmind.inflection.grammar;

import java.io.File;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

public class TypeReferenceResolutionTest extends AbstractInflectionTest
{

	@Test
	public void testTypeReferenceResolution_SimpleNameResolutionWithTypeImport_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "b", "package b; import a.A; taxonomy B extends A {}" ) );
	}
	
	@Test
	public void testTypeReferenceResolution_FullyQualifiedNameResolutionWithTypeImport_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "b", "package b; import a.A; taxonomy B extends a.A {}" ) );
	}
	
	@Test
	public void testTypeReferenceResolution_SimpleNameResolutionWithPackageImport_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "b", "package b; import a.*; taxonomy B extends A {}" ) );
	}
	
	@Test
	public void testTypeReferenceResolution_FullyQualifiedNameResolutionWithPackageImport_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "b", "package b; import a.*; taxonomy B extends a.A {}" ) );
	}
	
	@Test
	public void testTypeReferenceResolution_SimpleNameResolutionWithoutImport_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "b", "package b; taxonomy B extends A {}" ) );
	}
	
	@Test
	public void testTypeReferenceResolution_FullyQualifiedNameResolutionWithoutImport_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "b", "package b; taxonomy B extends a.A {}" ) );
	}
	
	@Test
	public void testTypeReferenceResolution_ClasspathResolution_SuccessfulCompilation() throws Exception
	{
		File compiledTaxonomyDirectory = InflectionCompilerTestUtility.compileInflection( createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
		TaxonomyLoader taxonomyLoader = TaxonomyTestUtility.createTaxonomyLoader( compiledTaxonomyDirectory );
		
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , taxonomyLoader, createInflectionFileMock( "b", "package b; taxonomy B extends a.A {}" ) );
	}
	
	@Test
	public void testTypeReferenceResolution_IllegalClasspathResolution_CompilationFailure() throws Exception
	{
		File compiledTaxonomyDirectory = InflectionCompilerTestUtility.compileInflection( createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
		TaxonomyLoader taxonomyLoader = TaxonomyTestUtility.createTaxonomyLoader( compiledTaxonomyDirectory );
		
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , taxonomyLoader, createInflectionFileMock( "b", "package b; taxonomy B extends A {}" ) );
	}
					
}
