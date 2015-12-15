package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;

public class TaxonomyTest extends AbstractInflectionTest
{

	@Test
	public void testDefaultAccessType_SingleTaxonomy_AccessTypeIsProperty() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.A" );
			assertNull( "Declared access type is empty", taxomomy.getDeclaredDefaultAccessType() );
			assertEquals( "Default access type is property", AccessType.PROPERTY, taxomomy.getDefaultAccessType() );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ) );

	}

	@Test
	public void testDefaultAccessType_ChildTaxonomyWithPropertyAccessType_AccessTypeIsInherited() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.B" );
			assertNull( "Declared access type", taxomomy.getDeclaredDefaultAccessType() );
			assertEquals( "Access type", AccessType.PROPERTY, taxomomy.getDefaultAccessType() );
		} , createInflectionFileMock( "a", "package a; taxonomy A {} taxonomy B extends A {}" ) );

	}

	@Test
	public void testDefaultAccessType_ChildTaxonomyWithFieldAccessType_AccessTypeIsInherited() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.B" );
			assertEquals( "Access type", AccessType.FIELD, taxomomy.getDefaultAccessType() );
			// assertNull( "Declared access type", taxomomy.getDeclaredDefaultAccessType() );
			// <inflection-error/> declared access type should be null
		} , createInflectionFileMock( "a", "package a; taxonomy A { default field; } taxonomy B extends A {}" ) );

	}

	@Test
	public void testDefaultAccessType_ChildTaxonomyOverridesAccessTypeWithProperty_AccessTypeIsNotInherited() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.B" );
			assertEquals( "Access type", AccessType.PROPERTY, taxomomy.getDefaultAccessType() );
			assertNotNull( "Declared access type", taxomomy.getDeclaredDefaultAccessType() );
		} , createInflectionFileMock( "a", "package a; taxonomy A { default field; } taxonomy B extends A { default property; }" ) );

	}

	@Test
	public void testDefaultAccessType_ChildTaxonomyOverridesAccessTypeWithDefault_AccessTypeIsInherited() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.B" );
			assertEquals( "Access type", AccessType.FIELD, taxomomy.getDefaultAccessType() );
			assertNull( "Declared access type", taxomomy.getDeclaredDefaultAccessType() );
		} , createInflectionFileMock( "a", "package a; taxonomy A { default field; } taxonomy B extends A { default; }" ) );

	}

	@Test
	public void testDefaultAccessType_MultipleInheritanceOrderField_AccessTypeIsInheritedFromFirstParent() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.C" );
			assertEquals( "Access type", AccessType.PROPERTY, taxomomy.getDefaultAccessType() ); // <inflection-error/> access type should be FIELD
		} , createInflectionFileMock( "a", "package a; taxonomy A { default field; } taxonomy B { default property; } taxonomy C extends A,B { }" ) );

	}

	@Test
	public void testDefaultAccessType_MultipleInheritanceOrderProperty_AccessTypeIsInheritedFromFirstParent() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.C" );
			assertEquals( "Access type", AccessType.PROPERTY, taxomomy.getDefaultAccessType() );
		} , createInflectionFileMock( "a", "package a; taxonomy A { default field; } taxonomy B { default property; } taxonomy C extends B,A { }" ) );

	}

	@Test
	public void testDefaultAccessType_MultipleInheritanceOrderFieldDefault_AccessTypeIsInheritedFromFirstParent() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.C" );
			assertEquals( "Access type", AccessType.FIELD, taxomomy.getDefaultAccessType() );
			assertNull( "Declared access type", taxomomy.getDeclaredDefaultAccessType() );
		} , createInflectionFileMock( "a", "package a; taxonomy A { default field; } taxonomy B { default property; } taxonomy C extends A,B { default; }" ) );

	}

	@Test
	public void testDefaultAccessType_MultipleInheritanceOrderPropertyDefault_AccessTypeIsInheritedFromFirstParent() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.C" );
			assertEquals( "Access type", AccessType.PROPERTY, taxomomy.getDefaultAccessType() );
			assertNull( "Declared access type", taxomomy.getDeclaredDefaultAccessType() );
		} , createInflectionFileMock( "a", "package a; taxonomy A { default field; } taxonomy B { default property; } taxonomy C extends B,A { default; }" ) );

	}

	@Test
	public void testDefaultAccessType_MultipleInheritanceOrderFieldOverride_AccessTypeIsNotInheritedFromFirstParent() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.C" );
			assertEquals( "Access type", AccessType.PROPERTY, taxomomy.getDefaultAccessType() );
			assertNotNull( "Declared access type", taxomomy.getDeclaredDefaultAccessType() );
		} , createInflectionFileMock( "a", "package a; taxonomy A { default field; } taxonomy B { default property; } taxonomy C extends A,B { default property; }" ) );

	}

	@Test
	public void testDefaultAccessType_MultipleInheritanceOrderPropertyOverride_AccessTypeIsNotInheritedFromFirstParent() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.C" );
			assertEquals( "Access type", AccessType.FIELD, taxomomy.getDefaultAccessType() );
			assertNotNull( "Declared access type", taxomomy.getDeclaredDefaultAccessType() );
		} , createInflectionFileMock( "a", "package a; taxonomy A { default field; } taxonomy B { default property; } taxonomy C extends B,A { default field; }" ) );

	}
		
	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyNamedPackageInSameFile_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {} taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyNamedPackageInDifferentFiles_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyUnnamedPackageInDifferentFiles_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "taxonomy A {}" ), createInflectionFileMock( "taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyUnnamedPackageInSameFile_CompilationFailure() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , createInflectionFileMock( "taxonomy A {} taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyNamedAndUnnamedPackageInDifferentFiles_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), createInflectionFileMock( "taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyNamedPackageOnClasspath_CompilationFailure() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "a", "package a; taxonomy A {}" ) };

		doTest( job -> {
			InflectionCompilerTestUtility.assertCompilationFailure( job );
		} , null, classpath, createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DuplicateTaxonomyUnnamedPackageOnClasspath_SuccessfulCompilation() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "taxonomy A {}" ) };

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , null, classpath, createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesNamedPackageInSameFile_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {} taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesUnnamedPackageInSameFile_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "taxonomy A {} taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesNamedPackageInDifferentFiles_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ), createInflectionFileMock( "a", "package a; taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesUnnamedPackageInDifferentFiles_SuccessfulCompilation() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , createInflectionFileMock( "taxonomy A {}" ), createInflectionFileMock( "taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesNamedPackageOnClasspath_SuccessfulCompilation() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "a", "package a; taxonomy A {}" ) };

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , null, classpath, createInflectionFileMock( "a", "package a; taxonomy B {}" ) );
	}

	@Test
	public void testTaxonomyUniqueness_DifferentTaxonomiesUnnamedPackageOnClasspath_SuccessfulCompilation() throws Exception
	{
		InflectionFileMock[] classpath = { createInflectionFileMock( "taxonomy A {}" ) };

		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
		} , null, classpath, createInflectionFileMock( "taxonomy B {}" ) );
	}

	@Test
	public void testEmptyTaxonomy_NoViews_ViewsDoNotExist() throws Exception
	{
		doTest( job -> {
			InflectionCompilerTestUtility.assertSuccessfulCompilation( job );
			Taxonomy taxomomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.A" );
			assertTrue( "Views must not exist", taxomomy.getViews().isEmpty() );
		} , createInflectionFileMock( "a", "package a; taxonomy A {}" ) );
	}

}
