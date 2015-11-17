package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.test.blackbox.BlackboxTestUtility;
import ch.liquidmind.inflection.test.model.A;

public class TaxonomyTest {
	
	@Test
	public void testGetView_IncludeViewWithoutInclude_ViewExists()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeViewWithoutIncludeTaxonomy");
		assertNotNull("view must exist", taxonomy.getView(A.class.getCanonicalName()));
	}
	
	@Test
	public void testGetView_IncludeViewWithInclude_ViewExists()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "TaxonomyTest_GetView_IncludeViewWithIncludeTaxonomy");
		assertNotNull("view must exist", taxonomy.getView(A.class.getCanonicalName()));
	}
	
	@Test
	public void testGetView_ExcludeView_ViewDoesNotExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "TaxonomyTest_GetView_ExcludeViewTaxonomy");
		assertNull("view must not exist", taxonomy.getView(A.class.getCanonicalName()));
	}
		
	@Test
	public void testGetView_IncludeExcludeOrderIncludeFirst_ViewDoesNotExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "GetView_IncludeExcludeOrderIncludeFirstTaxonomy");
		assertNull("view must not exist", taxonomy.getView(A.class.getCanonicalName()));
	}
	
	@Test
	@Ignore("check if real error? Assumption: First all includes, then all excludes, regardless of definition in file")
	public void testGetView_IncludeExcludeOrderExcludeFirst_ViewDoesNotExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "GetView_IncludeExcludeOrderExcludeFirstTaxonomy");
		assertNull("view must not exist", taxonomy.getView(A.class.getCanonicalName()));
	}
	
	@Test
	public void testGetView_IncludeWithSelector_ViewDoesExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "GetView_IncludeWithSelectorTaxonomy");
		assertNotNull("view must exist", taxonomy.getView(A.class.getCanonicalName()));
	}
	
	@Test
	public void testGetView_ExcludeWithSelector_ViewDoesNotExist()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "GetView_ExcludeWithSelectorTaxonomy");
		assertNull("view must not exist", taxonomy.getView(A.class.getCanonicalName()));
	}
		
}
