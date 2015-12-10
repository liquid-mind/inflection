package ch.liquidmind.inflection.test.blackbox;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.proxy.Proxy;
import ch.liquidmind.inflection.proxy.util.ProxyGeneratorTestUtility;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class BlackboxTest
{
	private static final String TESTSTRING = "abcdef";
	
	private static TaxonomyLoader taxonomyLoader;
	private static URLClassLoader modelClassLoader;
	private static File compiledProxyDir;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		StringBuilder javaContent = new StringBuilder();
		javaContent.append( "package v.w.x;" );
		javaContent.append( "public class V { private int id; public int getId() { return id; }; }" );
		JavaFileMock javaFileMock = new JavaFileMock( "V.java", "v.w.x", javaContent.toString() );
		
		StringBuilder javaContent3 = new StringBuilder();
		javaContent3.append( "package v.w.x;" );
		javaContent3.append( "import java.util.Date;" );
		javaContent3.append( "public class W extends V { ");
		javaContent3.append( "private Long longMember; public Long getLongMember() { return longMember; };" );
		javaContent3.append( "private String stringMember; public String getStringMember() { return stringMember; }; public void setStringMember(String stringMember) { this.stringMember = stringMember; };" );
		javaContent3.append( "private Date dateMember; public Date getDateMember() { return dateMember; }; public void setDateMember(Date dateMember) { this.dateMember = dateMember; };" );
		javaContent3.append( "private Enum enumMember; public Enum getEnumMember() { return enumMember; }; public void setEnumMember(Enum enumMember) { this.enumMember = enumMember; };" );
		javaContent3.append( " }" );
		JavaFileMock javaFileMock3 = new JavaFileMock( "W.java", "v.w.x", javaContent3.toString() );
		
		modelClassLoader = InflectionCompilerTestUtility.compileJava( new JavaFileMock[] { javaFileMock, javaFileMock3 } );
		
		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c;" );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy BlackboxTestTaxonomy" );
		builder.append( "{" );
		builder.append( "	view W { *; }	" );
		builder.append( "}" );
		builder.append( "taxonomy BlackboxTestChildTaxonomy extends BlackboxTestTaxonomy " );
		builder.append( "{" );
		builder.append( "	view * { *; }	" );
		builder.append( "}" );
		builder.append( "taxonomy BlackboxTestCalculatedTaxonomy extends BlackboxTestTaxonomy" );
		builder.append( "{" );
		// view B1 use B1CalculatedMembers { *; }
		builder.append( "	view W { *; }" );
		builder.append( "}" );
		
		taxonomyLoader = InflectionCompilerTestUtility.compileInflection( modelClassLoader, new InflectionFileMock( "a.b.c", builder.toString() ) );
		

		taxonomyLoader = InflectionCompilerTestUtility.compileInflection( modelClassLoader, new InflectionFileMock( "a.b.c", builder.toString() ) );
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "a.b.c.BlackboxTestTaxonomy" );
		View view = taxonomy.getView( "v.w.x.W" );
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

	private Proxy createTestViewB1() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
	{
		TaxonomyLoader.setContextTaxonomyLoader( taxonomyLoader );
		Proxy proxy = ProxyGeneratorTestUtility.loadProxy( compiledProxyDir, "a.b.c.BlackboxTestTaxonomy.v.w.x.BlackboxTestTaxonomy_W" );
		return proxy;
	}

}
