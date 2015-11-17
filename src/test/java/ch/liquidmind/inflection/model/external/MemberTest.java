package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.test.blackbox.BlackboxTestUtility;
import ch.liquidmind.inflection.test.model.A;

public class MemberTest {
		
	@Test
	public void testGetSelectionType_Include_CorrectType()
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy(this.getClass().getPackage().getName(), "MemberTest_GetSelectionTypeTaxonomy");
		View view = taxonomy.getView(A.class.getCanonicalName());
		Member member = view.getDeclaredMember("id");
		assertEquals(SelectionType.INCLUDE, member.getSelectionType());
	}
	
}
