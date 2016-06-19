package ch.liquidmind.inflection.test.assoc;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import __java.lang.__Class;
import ch.liquidmind.inflection.support.RelatedTypeVisitor;
import ch.liquidmind.inflection.support.TypeWalker;

public class Test
{
	Map< String, Integer > test;	// relatedType == Integer
	List< Class< String > > test2;	// relatedType == Class< String >
	List test3;						// relatedType == Object
	String test4;					// relatedType == String
	List< Set< Boolean > > test5;	// relatedType == Boolean
	List< ? extends String > test6;	// relatedType == String
	List< ? > test7;				// relatedType == String
	Class< ? >[] test8;
	
	public static void main( String[] args )
	{
//		AssociationRegistry.scan( "ch.liquidmind.inflection.test.assoc.model.*" );
		
		Type type = __Class.getDeclaredField( Test.class, "test8" ).getGenericType();
		RelatedTypeVisitor visitor = new RelatedTypeVisitor();
		TypeWalker walker = new TypeWalker( visitor );
		walker.walk( type );
		Type relatedType = visitor.getRelatedType();
	}
}
