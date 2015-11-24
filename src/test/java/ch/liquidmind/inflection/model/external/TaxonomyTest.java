package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;
import ch.liquidmind.inflection.test.model.A;

public class TaxonomyTest
{

	private static File compiledTaxonomyDir;
	
	@BeforeClass
	public static void beforeClass() throws Exception
	{
		compiledTaxonomyDir = InflectionCompilerTestUtility.compileInflectionFile( TaxonomyTest.class, "TaxonomyTest.inflect" );
	}
	
	@Test
	public void testGetView_IncludeViewWithoutInclude_ViewExists() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeViewWithoutIncludeTaxonomy" );
		assertNotNull( "view must exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_IncludeViewWithInclude_ViewExists() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeViewWithIncludeTaxonomy" );
		assertNotNull( "view must exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_ExcludeView_ViewDoesNotExist() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "TaxonomyTest_GetView_ExcludeViewTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_IncludeExcludeOrderIncludeFirst_ViewDoesNotExist() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeExcludeOrderIncludeFirstTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_IncludeExcludeOrderExcludeFirst_ViewDoesNotExist() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeExcludeOrderExcludeFirstTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_IncludeWithSelector_ViewDoesExist() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeWithSelectorTaxonomy" );
		assertNotNull( "view must exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_ExcludeWithSelector_ViewDoesNotExist() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "TaxonomyTest_GetView_ExcludeWithSelectorTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

}
