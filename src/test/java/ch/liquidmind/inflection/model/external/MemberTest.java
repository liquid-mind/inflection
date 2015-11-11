package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.test.blackbox.BlackboxTestUtility;
import ch.liquidmind.inflection.test.model.Address;

public class MemberTest {
		
	@Test
	public void testSelector()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "MemberTaxonomy");
		View view = taxonomy.getView(Address.class.getCanonicalName());
		Member member = view.getDeclaredMember("street");
		assertEquals(SelectionType.INCLUDE, member.getSelectionType());
	}
	
}
