package ch.liquidmind.inflection.proxy;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.proxy.util.ProxyGeneratorTestUtility;
import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

@RunWith( Parameterized.class )
public class ProxyGetterSetterTest extends AbstractInflectionTest
{
	private static final String TESTSTRING = "abcdef";

	@Parameters( name = "{index}: Member type: {1}, used test data: {0}" )
	public static Collection< Object[] > data()
	{
		return Arrays.asList( new Object[][] {

				{ Boolean.TRUE, Boolean.class }, 
				{ Boolean.TRUE, boolean.class }, 
				{ Byte.MAX_VALUE, Byte.class }, 
				{ Byte.MAX_VALUE, byte.class }, 
				{ Short.MAX_VALUE, Short.class }, 
				{ Short.MAX_VALUE, short.class }, 
				{ Character.MAX_VALUE, Character.class }, 
				{ Character.MAX_VALUE, char.class }, 
				{ Integer.MAX_VALUE, Integer.class },
				{ Integer.MAX_VALUE, int.class }, 
				{ Long.MAX_VALUE, Long.class }, 
				{ Long.MAX_VALUE, long.class }, 
				{ Float.MAX_VALUE, Float.class }, 
				{ Float.MAX_VALUE, float.class }, 
				{ Double.MAX_VALUE, Double.class }, 
				{ Double.MAX_VALUE, double.class }, 
				{ BigInteger.TEN, BigInteger.class }, 
				{ BigDecimal.TEN, BigDecimal.class }, 
				{ TESTSTRING, String.class }, 
				{ new Date(), Date.class }, 
				{ new ArrayList<Long>() { private static final long serialVersionUID = 1L; { add(1L); add(2L); }}, List.class }, 
				{ new HashSet<Long>() { private static final long serialVersionUID = 1L; { add(1L); add(2L); }}, Set.class }, 
				{ new HashMap<Long, String>() { private static final long serialVersionUID = 1L; { put(1L, "1"); put(2L, "2"); }}, Map.class }, 
				{ new Object(), Object.class }

		} );
	}

	private final Object testData;
	private final Class< ? > type;

	public ProxyGetterSetterTest( Object testData, Class< ? > type )
	{
		this.testData = testData;
		this.type = type;
	}

	@Test
	public void testMember()
	{
		StringBuilder javaClassV = new StringBuilder();
		javaClassV.append( "package v.w.x;" );
		javaClassV.append( "public class V { " );
		javaClassV.append( TestUtility.generateMember( "int", "id" ) );
		javaClassV.append( " }" );
		JavaFileMock javaFileMockV = new JavaFileMock( "V.java", "v.w.x", javaClassV.toString() );

		StringBuilder javaClassW = new StringBuilder();
		javaClassW.append( "package v.w.x;" );
		javaClassW.append( "public class W extends V { " );
		javaClassW.append( TestUtility.generateMember( type.getName(), "member" ) );
		javaClassW.append( " }" );
		JavaFileMock javaFileMockW = new JavaFileMock( "W.java", "v.w.x", javaClassW.toString() );

		JavaFileMock[] javaModel = new JavaFileMock[] { javaFileMockV, javaFileMockW };

		StringBuilder builder = new StringBuilder();
		builder.append( "package a.b.c;" );
		builder.append( "import v.w.x.*;" );
		builder.append( "taxonomy A" );
		builder.append( "{" );
		builder.append( "	view W { *; }	" );
		builder.append( "}" );

		doTest( job -> {
			Proxy b1 = createTestViewProxy( TestUtility.getTaxonomyLoader( job ), "a.b.c.A", "v.w.x.W" );
			TestUtility.invokeMethod( b1, "setMember", testData, type );
			Object result = TestUtility.invokeMethod( b1, "getMember" );
			if ( testData instanceof Collection )
			{
				Iterator< ? > targetIt = ( (Collection< ? >)result ).iterator();
				for ( Object obj : (Collection< ? >)testData )
					if ( !obj.equals( targetIt.next() ) )
					{
						Assert.fail( "Collection " + testData + " does not match " + result );
					}
			}
			else if ( testData instanceof Map ) {
				// <inflection-error/> MapProxy is not yet implemented, remove this else-if part when fixed
			}
			else
			{
				assertEquals( "Member was updated by proxy", testData, result );
			}
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
