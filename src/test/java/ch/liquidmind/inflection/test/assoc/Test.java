package ch.liquidmind.inflection.test.assoc;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

import __java.lang.__Class;
import ch.liquidmind.inflection.support.RelatedTypeVisitor;
import ch.liquidmind.inflection.support.TypeWalker;

public class Test
{
	String test1;
	Class< ? > test2;
	String[] test3;
	Class< ? >[] test4;
	List< String > test5;
	Map< Integer, String > test6;
	List< Set< String > > test7;
	List< Class< ? > > test8;
	List< String[] > test9;
	List< ? > test10;
	List< ? extends String > test11;
	List test12;
	
//	Map< String, Integer > test;	// relatedType == Integer
//	List< Class< String > > test2;	// relatedType == Class< String >
//	List test3;						// relatedType == Object
//	String test4;					// relatedType == String
//	List< Set< Boolean > > test5;	// relatedType == Boolean
//	List< ? extends String > test6;	// relatedType == String
//	List< ? > test7;				// relatedType == String
//	Class< ? >[] test8;
	
	public static void main( String[] args )
	{
//		AssociationRegistry.scan( "ch.liquidmind.inflection.test.assoc.model.*" );
		
		Type classType = __Class.getDeclaredField( Test.class, "test2" ).getGenericType();
		
		testTypeWalker( "test1", String.class );
		testTypeWalker( "test2", classType );
		testTypeWalker( "test3", String.class );
		testTypeWalker( "test4", classType );
		testTypeWalker( "test5", String.class );
		testTypeWalker( "test6", String.class );
		testTypeWalker( "test7", String.class );
		testTypeWalker( "test8", classType );
		testTypeWalker( "test9", String.class );
		testTypeWalker( "test10", Object.class );
		testTypeWalker( "test11", String.class );
		testTypeWalker( "test12", Object.class );
	}
	
	private static void testTypeWalker( String fieldName, Type expectedType )
	{
		Type type = __Class.getDeclaredField( Test.class, fieldName ).getGenericType();
		RelatedTypeVisitor visitor = new RelatedTypeVisitor();
		TypeWalker walker = new TypeWalker( visitor );
		walker.walk( type );
		Type relatedType = visitor.getRelatedType();
		
		Assert.assertEquals( expectedType, relatedType );
	}
}
