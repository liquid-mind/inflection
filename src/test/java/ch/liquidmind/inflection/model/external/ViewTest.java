package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ch.liquidmind.inflection.test.blackbox.BlackboxTestUtility;
import ch.liquidmind.inflection.test.model.Address;

public class ViewTest {

	@Test
	public void testGetMember()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "ChildTaxonomy");
		View view = taxonomy.getView(Address.class.getCanonicalName());
		assertNotNull(view.getMember("street"));
	}
	
	@Test
	public void testGetDeclaredMember()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "ChildTaxonomy");
		View view = taxonomy.getView(Address.class.getCanonicalName());
		assertNotNull(view.getDeclaredMember("street"));
	}
	
	@Test
	public void testGetSuperMember()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "ChildTaxonomy");
		View view = taxonomy.getView(Address.class.getCanonicalName());
		assertNotNull(view.getMember("id"));
	}
	
	@Test
	public void testGetParentView()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "ChildTaxonomy");
		View view = taxonomy.getView(Address.class.getCanonicalName());
		Taxonomy currentParent = view.getParentTaxonomy();
		assertNotNull(currentParent);
		assertEquals(taxonomy, currentParent);
	}
	
}
