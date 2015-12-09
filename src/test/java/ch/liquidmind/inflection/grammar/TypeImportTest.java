package ch.liquidmind.inflection.grammar;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;

public class TypeImportTest extends AbstractInflectionTest
{

	@Test
	public void testTypeImport_SingleTypeLegalImport_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "A.inflect", "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "B.inflect", "b", "package b; import a.A;" ) );
	}
	
	@Test
	public void testTypeImport_SingleTypeIllegalImport_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); // <inflection-error/> should not compile, does not in java
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "b", "package b; import a.B;" ) );
	}
	
	@Test
	public void testTypeImport_SingleTypeMultipleImports_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); 
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "b", "package b; taxonomy B {}" ), 
			createInflectionFileMock( "c", "package c; import a.A; import b.B;" ) );
	}
	
	@Test
	public void testTypeImport_SingleTypeDuplicateImport_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job ); // <inflection-error/> should not compile, does not in java (due to file name constraint)
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), 
			createInflectionFileMock( "b", "package b; taxonomy A {}" ), 
			createInflectionFileMock( "c", "package c; import a.A; import b.A;" ) );
	}
		
}
