package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.test.blackbox.BlackboxTestUtility;
import ch.liquidmind.inflection.test.model.Address;

public class TaxonomyTest {
	
	@Test
	public void testSuperTaxonomy()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "TaxonomySuperTaxonomy");
		assertNotNull("verify view exists", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testExcludeView()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "ExcludeTaxonomy");
		assertNull("verify view does not exist", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testIncludeView()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "IncludeTaxonomy");
		assertNotNull("verify view exists", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testIncludeExcludeOrder1()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "IncludeExcludeOrderTaxonomy1");
		assertNull("verify view does not exist", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	@Ignore("check if real error? Assumption: First all includes, then all excludes, regardless of definition in file")
	public void testIncludeExcludeOrder2()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "IncludeExcludeOrderTaxonomy2");
		assertNull("verify view does not exist", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testSelector1()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "SelectorTaxonomy1");
		assertNotNull("verify view exists", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testSelector2()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "SelectorTaxonomy2");
		assertNull("verify view does not exist", taxonomy.getView(Address.class.getCanonicalName()));
	}
		
}
