package ch.liquidmind.inflection.model.external;

import static ch.liquidmind.inflection.test.mock.AbstractFileMock.UNNAMED_PACKAGE;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public class UseClassTest extends AbstractInflectionTest
{

	private JavaFileMock[] createSimpleJavaModel() 
	{
		StringBuilder javaClass = new StringBuilder();
		javaClass.append( "public class V {" );
		javaClass.append( TestUtility.generateMember( "int", "intMember" ) );
		javaClass.append( "}" );
		JavaFileMock javaFileMock = createJavaFileMock( "V.java", UNNAMED_PACKAGE, javaClass.toString() );

		StringBuilder usedClass = new StringBuilder();
		usedClass.append( "public class C1 {" );
		usedClass.append( "public static int getSquare(V v1) { return v1.getIntMember() * v1.getIntMember(); } " );
		usedClass.append( "}" );
		JavaFileMock usedClassMock = createJavaFileMock( "C1.java", UNNAMED_PACKAGE, usedClass.toString() );
		
		return new JavaFileMock[] { javaFileMock, usedClassMock };
	}
	
	@Test
	public void testUseClass() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a;" );
		builder.append( "taxonomy A {" );
		builder.append( "	view V use C1 { square; }" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.A" );
			View view = taxonomy.getView( "V" );
			assertNotNull( view );
			Member member = taxonomy.getMember( view, "square" );
			assertNotNull( member );
		} , createSimpleJavaModel(), null, createInflectionFileMock( "a", builder.toString() ) );
	}
	
}
