package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import ch.liquidmind.inflection.test.blackbox.util.BlackboxTestUtility;
import ch.liquidmind.inflection.test.model.A;

public class TaxonomyTest
{

	@Test
	public void testGetView_IncludeViewWithoutInclude_ViewExists()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy( this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeViewWithoutIncludeTaxonomy" );
		assertNotNull( "view must exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_IncludeViewWithInclude_ViewExists()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy( this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeViewWithIncludeTaxonomy" );
		assertNotNull( "view must exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_ExcludeView_ViewDoesNotExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy( this.getClass().getPackage().getName(), "TaxonomyTest_GetView_ExcludeViewTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_IncludeExcludeOrderIncludeFirst_ViewDoesNotExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy( this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeExcludeOrderIncludeFirstTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_IncludeExcludeOrderExcludeFirst_ViewDoesNotExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy( this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeExcludeOrderExcludeFirstTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_IncludeWithSelector_ViewDoesExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy( this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeWithSelectorTaxonomy" );
		assertNotNull( "view must exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

	@Test
	public void testGetView_ExcludeWithSelector_ViewDoesNotExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy( this.getClass().getPackage().getName(), "TaxonomyTest_GetView_ExcludeWithSelectorTaxonomy" );
		assertNull( "view must not exist", taxonomy.getView( A.class.getCanonicalName() ) );
	}

}
