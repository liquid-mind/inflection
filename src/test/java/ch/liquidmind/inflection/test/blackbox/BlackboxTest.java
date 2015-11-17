package ch.liquidmind.inflection.test.blackbox;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.proxy.ProxyRegistry;
import ch.liquidmind.inflection.test.blackbox.BlackboxTestCalculatedTaxonomy.ch.liquidmind.inflection.test.model.BlackboxTestCalculatedTaxonomy_Person;
import ch.liquidmind.inflection.test.blackbox.BlackboxTestTaxonomy.ch.liquidmind.inflection.test.model.BlackboxTestTaxonomy_Address;
import ch.liquidmind.inflection.test.blackbox.BlackboxTestTaxonomy.ch.liquidmind.inflection.test.model.BlackboxTestTaxonomy_Person;
import ch.liquidmind.inflection.test.model.Address;
import ch.liquidmind.inflection.test.model.Gender;
import ch.liquidmind.inflection.test.model.Person;

public class BlackboxTest {

	private static final String TESTSTRING = "city";
	private static final String TESTSTRING2 = "city2";

	@Test
	public void testString() throws Exception {
		BlackboxTestTaxonomy_Address address = new BlackboxTestTaxonomy_Address();
		address.setCity(TESTSTRING);
		assertEquals(TESTSTRING, address.getCity());
	}
	
	@Test
	public void testDate() throws Exception {
		BlackboxTestTaxonomy_Person person = new BlackboxTestTaxonomy_Person();
		Date date = new Date();
		person.setDateOfBirth(date);
		assertEquals(date, person.getDateOfBirth());
	}
	
	@Test
	public void testEnum() throws Exception {
		BlackboxTestTaxonomy_Person person = new BlackboxTestTaxonomy_Person();
		person.setGender(Gender.FEMALE);
		assertEquals(Gender.FEMALE, person.getGender());
	}
	
	@Test
	public void testList1() throws Exception {
		BlackboxTestTaxonomy_Person personProxy = new BlackboxTestTaxonomy_Person();
		Person personObject = ProxyRegistry.getContextProxyRegistry().getObject(personProxy);
		
		List<Address> addresses = new ArrayList<>();
		Address address = new Address();
		address.setCity(TESTSTRING);
		addresses.add(address);
		personObject.setAddresses(addresses);
		
		BlackboxTestTaxonomy_Address addressProxy = personProxy.getAddresses().get(0);
		assertNotNull(addressProxy);
		assertEquals(TESTSTRING, addressProxy.getCity());
		
		addressProxy.setCity(TESTSTRING2);
		assertEquals("City was updated", TESTSTRING2, address.getCity());
	}
	
	@Test
	@Ignore("size() not yet implemented on proxy")
	public void testList2() throws Exception {
		BlackboxTestTaxonomy_Person personProxy = new BlackboxTestTaxonomy_Person();
		personProxy.getAddresses().add(new BlackboxTestTaxonomy_Address());
		assertEquals(1, personProxy.getAddresses().size());
	}
	
	@Test
	public void testCalculated() throws Exception {
		BlackboxTestCalculatedTaxonomy_Person personProxy = new BlackboxTestCalculatedTaxonomy_Person();
		Date date = new Date();
		personProxy.setDateOfBirth(date);
		int age = personProxy.getAge();
		assertEquals(0, age);
	}
	
}