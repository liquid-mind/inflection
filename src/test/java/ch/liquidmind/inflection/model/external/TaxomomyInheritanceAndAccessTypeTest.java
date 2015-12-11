package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;

public class TaxomomyInheritanceAndAccessTypeTest extends AbstractInflectionTest
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

}
