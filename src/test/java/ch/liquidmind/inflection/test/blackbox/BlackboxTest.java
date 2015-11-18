package ch.liquidmind.inflection.test.blackbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.proxy.ProxyRegistry;
import ch.liquidmind.inflection.test.blackbox.BlackboxTestTaxonomy.ch.liquidmind.inflection.test.model.BlackboxTestTaxonomy_B1;
import ch.liquidmind.inflection.test.model.B1;
import ch.liquidmind.inflection.test.model.TestEnum;

public class BlackboxTest
{

	private static final String TESTSTRING = "abcdef";
	private static final String TESTSTRING2 = "123456";

	@Test
	public void testSetGetString() throws Exception
	{
		BlackboxTestTaxonomy_B1 b1 = new BlackboxTestTaxonomy_B1();
		b1.setStringMember( TESTSTRING );
		assertEquals( "String was updated by proxy", TESTSTRING, b1.getStringMember() );
	}

	@Test
	public void testSetGetDate() throws Exception
	{
		BlackboxTestTaxonomy_B1 person = new BlackboxTestTaxonomy_B1();
		Date date = new Date();
		person.setDateMember( date );
		assertEquals( "Date was updated by proxy", date, person.getDateMember() );
	}

	@Test
	public void testSetGetEnum() throws Exception
	{
		BlackboxTestTaxonomy_B1 person = new BlackboxTestTaxonomy_B1();
		person.setEnumMember( TestEnum.VALUE1 );
		assertEquals( "Enum was updated by proxy", TestEnum.VALUE1, person.getEnumMember() );
	}

	@Test
	public void testGetListElement() throws Exception
	{
		BlackboxTestTaxonomy_B1 b1Proxy = new BlackboxTestTaxonomy_B1();
		B1 personObject = ProxyRegistry.getContextProxyRegistry().getObject( b1Proxy );

		List< B1 > list = new ArrayList< >();
		B1 b1 = new B1();
		b1.setStringMember( TESTSTRING );
		list.add( b1 );
		personObject.setListMember( list );

		BlackboxTestTaxonomy_B1 b1ListProxy = b1Proxy.getListMember().get( 0 );
		assertNotNull( b1ListProxy );
		assertEquals( TESTSTRING, b1ListProxy.getStringMember() );

		b1ListProxy.setStringMember( TESTSTRING2 );
		assertEquals( "String was updated by proxy", TESTSTRING2, b1.getStringMember() );
	}

	@Test
	@Ignore( "size() not yet implemented on proxy" ) // TODO failing test
	public void testGetListSize() throws Exception
	{
		BlackboxTestTaxonomy_B1 b1Proxy = new BlackboxTestTaxonomy_B1();
		b1Proxy.getListMember().add( new BlackboxTestTaxonomy_B1() );
		assertEquals( "size() == 1 after inserting one element", 1, b1Proxy.getListMember().size() );
	}

}
