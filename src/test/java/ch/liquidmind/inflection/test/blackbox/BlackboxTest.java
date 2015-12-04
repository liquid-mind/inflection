package ch.liquidmind.inflection.test.blackbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;
import ch.liquidmind.inflection.proxy.Proxy;
import ch.liquidmind.inflection.proxy.ProxyRegistry;
import ch.liquidmind.inflection.proxy.util.ProxyGeneratorTestUtility;
import ch.liquidmind.inflection.test.InflectionFileMock;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.model.B1;
import ch.liquidmind.inflection.test.model.TestEnum;

public class BlackboxTest
{

	private static final String TESTSTRING = "abcdef";
	// private static final String TESTSTRING2 = "123456";

	private static File compiledTaxonomyDir;
	private static File compiledProxyDir;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package ch.liquidmind.inflection.test.blackbox;" );
		builder.append( "import ch.liquidmind.inflection.test.model.*;" );
		builder.append( "taxonomy BlackboxTestTaxonomy" );
		builder.append( "{" );
		builder.append( "	view B1 { *; }	" );
		builder.append( "}" );
		builder.append( "taxonomy BlackboxTestChildTaxonomy extends BlackboxTestTaxonomy " );
		builder.append( "{" );
		builder.append( "	view * { *; }	" );
		builder.append( "}" );
		builder.append( "taxonomy BlackboxTestCalculatedTaxonomy extends BlackboxTestTaxonomy" );
		builder.append( "{" );
		// view B1 use B1CalculatedMembers { *; }
		builder.append( "	view B1 { *; }" );
		builder.append( "}" );

		compiledTaxonomyDir = InflectionCompilerTestUtility.compileInflection( new InflectionFileMock("ch.liquidmind.inflection.test.blackbox", builder.toString()) );
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, BlackboxTest.class.getPackage().getName(), "BlackboxTestTaxonomy" );
		View view = taxonomy.getView( B1.class.getName() );
		compiledProxyDir = ProxyGeneratorTestUtility.createProxy( taxonomy, view );
	}

	@Test
	public void testSetGetString() throws Exception
	{
		Proxy b1 = createTestViewB1();
		TestUtility.invokeMethod( b1, "setStringMember", TESTSTRING );
		Object result = TestUtility.invokeMethod( b1, "getStringMember" );
		assertEquals( "String was updated by proxy", TESTSTRING, result );
	}

	@Test
	public void testSetGetDate() throws Exception
	{
		Proxy b1 = createTestViewB1();
		Date date = new Date();
		TestUtility.invokeMethod( b1, "setDateMember", date );
		Object result = TestUtility.invokeMethod( b1, "getDateMember" );
		assertEquals( "Date was updated by proxy", date, result );
	}

	@Test
	public void testSetGetEnum() throws Exception
	{
		Proxy b1 = createTestViewB1();
		TestUtility.invokeMethod( b1, "setEnumMember", TestEnum.VALUE1 );
		Object result = TestUtility.invokeMethod( b1, "getEnumMember" );
		assertEquals( "Enum was updated by proxy", TestEnum.VALUE1, result );
	}

	@Test
	public void testGetListElement() throws Exception
	{
		Proxy b1Proxy = createTestViewB1();
		B1 b1Object = ProxyRegistry.getContextProxyRegistry().getObject( b1Proxy );

		List< B1 > list = new ArrayList< >();
		B1 b1 = new B1();
		b1.setStringMember( TESTSTRING );
		list.add( b1 );
		b1Object.setListMember( list );

		Object result = TestUtility.invokeMethod( b1Proxy, "getListMember" );
		assertNotNull( result );
		// TODO currently not working due to classloading issues
		// Object b1ListProxyElement = ( (ListProxy< ? >)result ).get( 0 );
		// assertNotNull( b1ListProxyElement );
		// assertEquals( TESTSTRING, TestUtility.invokeMethod(
		// b1ListProxyElement, "getStringMember" ) );
		//
		// TestUtility.invokeMethod( b1ListProxyElement, "setStringMember",
		// TESTSTRING2 );
		// assertEquals( "String was updated by proxy", TESTSTRING2,
		// TestUtility.invokeMethod( b1, "getStringMember" ) );
	}

	private Proxy createTestViewB1() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
	{
		Proxy proxy = ProxyGeneratorTestUtility
			.loadProxy( compiledTaxonomyDir, compiledProxyDir, "ch.liquidmind.inflection.test.blackbox.BlackboxTestTaxonomy.ch.liquidmind.inflection.test.model.BlackboxTestTaxonomy_B1" );
		return proxy;
	}

}
