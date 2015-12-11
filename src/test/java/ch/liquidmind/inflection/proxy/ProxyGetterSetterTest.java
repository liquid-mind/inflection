package ch.liquidmind.inflection.proxy;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.proxy.Proxy;
import ch.liquidmind.inflection.proxy.ProxyGenerator;
import ch.liquidmind.inflection.proxy.util.ProxyGeneratorTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class ProxyGetterSetterTest extends AbstractInflectionTest
{
	private static final String TESTSTRING = "abcdef";
	private static JavaFileMock[] javaModel;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder javaClassV = new StringBuilder();
		javaClassV.append( "package v.w.x;" );
		javaClassV.append( "public class V { " );
		javaClassV.append( TestUtility.generateMember( "int", "id" ) );
		javaClassV.append( " }" );
		JavaFileMock javaFileMockV = new JavaFileMock( "V.java", "v.w.x", javaClassV.toString() );

		StringBuilder javaClassW = new StringBuilder();
		javaClassW.append( "package v.w.x;" );
		javaClassW.append( "import java.util.Date;" );
		javaClassW.append( "public class W extends V { " );
		javaClassW.append( TestUtility.generateMember( "Long", "longMember" ) );
		javaClassW.append( TestUtility.generateMember( "String", "stringMember" ) );
		javaClassW.append( TestUtility.generateMember( "Date", "dateMember" ) );
		javaClassW.append( " }" );
		JavaFileMock javaFileMockW = new JavaFileMock( "W.java", "v.w.x", javaClassW.toString() );

		javaModel = new JavaFileMock[] { javaFileMockV, javaFileMockW };
	}

	@Test
	public void testSetGetString() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c;" );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A" );
		builder.append( "{" );
		builder.append( "	view W { *; }	" );
		builder.append( "}" );

		doTest( job -> {
			Proxy b1 = createTestViewProxy( TestUtility.getTaxonomyLoader( job ), "a.b.c.A", "v.w.x.W" );
			TestUtility.invokeMethod( b1, "setStringMember", TESTSTRING );
			Object result = TestUtility.invokeMethod( b1, "getStringMember" );
			assertEquals( "String was updated by proxy", TESTSTRING, result );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	@Test
	public void testSetGetDate() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c;" );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A" );
		builder.append( "{" );
		builder.append( "	view W { *; }	" );
		builder.append( "}" );

		doTest( job -> {
			Proxy b1 = createTestViewProxy( TestUtility.getTaxonomyLoader( job ), "a.b.c.A", "v.w.x.W" );
			Date date = new Date();
			TestUtility.invokeMethod( b1, "setDateMember", date );
			Object result = TestUtility.invokeMethod( b1, "getDateMember" );
			assertEquals( "Date was updated by proxy", date, result );
		} , javaModel, null, createInflectionFileMock( "a.b.c", builder.toString() ) );
	}

	private Proxy createTestViewProxy( TaxonomyLoader taxonomyLoader, String taxonomyName, String viewName )
	{
		TaxonomyLoader.setContextTaxonomyLoader( taxonomyLoader );
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( taxonomyName );
		View view = taxonomy.getView( viewName );
		File compiledProxyDir = ProxyGeneratorTestUtility.createProxy( taxonomy, view );
		Proxy proxy = ProxyGeneratorTestUtility.loadProxy( compiledProxyDir, ProxyGenerator.getFullyQualifiedViewName( taxonomy, view ) );
		return proxy;
	}

}
