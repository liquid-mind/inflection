package ch.liquidmind.inflection.test.blackbox;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.test.model.Address;

public class InflectionBlackboxTest {

	@Test
	public void testSuperTaxonomy()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy("SuperTaxonomy");
		assertNotNull("verify view exists", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testExcludeView()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy("ExcludeTaxonomy");
		assertNull("verify view does not exist", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testIncludeView()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy("IncludeTaxonomy");
		assertNotNull("verify view exists", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testIncludeExcludeOrder1()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy("IncludeExcludeOrderTaxonomy1");
		assertNull("verify view does not exist", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	@Ignore("check if real error? Assumption: First all includes, then all excludes, regardless of definition in file")
	public void testIncludeExcludeOrder2()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy("IncludeExcludeOrderTaxonomy2");
		assertNull("verify view does not exist", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testSelector1()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy("SelectorTaxonomy1");
		assertNotNull("verify view exists", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
	@Test
	public void testSelector2()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy("SelectorTaxonomy2");
		assertNull("verify view does not exist", taxonomy.getView(Address.class.getCanonicalName()));
	}
	
}
