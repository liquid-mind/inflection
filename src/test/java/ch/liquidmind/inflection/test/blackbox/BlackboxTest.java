package ch.liquidmind.inflection.test.blackbox;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ch.liquidmind.inflection.proxy.ProxyRegistry;
import ch.liquidmind.inflection.test.blackbox.BlackboxTestTaxonomy.ch.liquidmind.inflection.test.model.BlackboxTestTaxonomy_Address;
import ch.liquidmind.inflection.test.blackbox.BlackboxTestTaxonomy.ch.liquidmind.inflection.test.model.BlackboxTestTaxonomy_Person;
import ch.liquidmind.inflection.test.model.Address;
import ch.liquidmind.inflection.test.model.Person;

public class BlackboxTest {

	private static final String TESTSTRING = "city";

	@Test
	public void testBeanString() throws Exception {
		BlackboxTestTaxonomy_Address address = new BlackboxTestTaxonomy_Address();
		address.setCity(TESTSTRING);
		assertEquals(TESTSTRING, address.getCity());
	}
	
	@Test
	public void testBeanList() throws Exception {
		BlackboxTestTaxonomy_Person personView = new BlackboxTestTaxonomy_Person();
		Person personObject = ProxyRegistry.getContextProxyRegistry().getObject(personView);
		List<Address> addresses = new ArrayList<>();
		Address address = new Address();
		address.setCity(TESTSTRING);
		addresses.add(address);
		personObject.setAddresses(addresses);
		assertNotNull(personView.getAddresses().get(0));
		assertEquals(TESTSTRING, personView.getAddresses().get(0).getCity());
	}
	
}
