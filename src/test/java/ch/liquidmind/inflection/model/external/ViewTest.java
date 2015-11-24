package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;
import ch.liquidmind.inflection.test.model.A;
import ch.liquidmind.inflection.test.model.B1;

public class ViewTest
{
	
	private static File compiledTaxonomyDir;
	
	@BeforeClass
	public static void beforeClass() throws Exception
	{
		compiledTaxonomyDir = InflectionCompilerTestUtility.compileInflectionFile( ViewTest.class, "ViewTest.inflect" );
	}

	@Test
	public void testGetMember_DeclaredMember_MemberExists() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "ViewTest_GetMember_DeclaredMemberTaxonomy" );
		View view = taxonomy.getView( B1.class.getCanonicalName() );
		assertNotNull( view.getMember( "longMember" ) );
	}

	@Test
	public void testGetDeclaredMember_DeclaredMember_MemberExists() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "ViewTest_GetDeclaredMember_DeclaredMemberTaxonomy" );
		View view = taxonomy.getView( B1.class.getCanonicalName() );
		assertNotNull( view.getDeclaredMember( "longMember" ) );
	}

	@Test
	public void testMember_InheritedMember_MemberExists() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "ViewTest_GetMember_InheritedMemberTaxonomy" );
		View view = taxonomy.getView( A.class.getCanonicalName() );
		assertNotNull( view.getMember( "id" ) );
	}

	@Test
	public void testGetParentTaxonomy_ChildView_ParentExists() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "ViewTest_GetParentTaxonomy" );
		View view = taxonomy.getView( B1.class.getCanonicalName() );
		Taxonomy currentParent = view.getParentTaxonomy();
		assertNotNull( currentParent );
		assertEquals( taxonomy, currentParent );
	}

}
