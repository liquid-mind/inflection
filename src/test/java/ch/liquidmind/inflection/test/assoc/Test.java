package ch.liquidmind.inflection.test.assoc;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import __java.lang.__Class;
import ch.liquidmind.inflection.support2.RelatedTypeVisitor;
import ch.liquidmind.inflection.support2.TypeWalker;

public class Test
{
	Map< String, Integer > test;	// relatedType == Integer
	List< Class< String > > test2;	// relatedType == Class< String >
	List test3;						// relatedType == null
	String test4;					// relatedType == String
	List< Set< Boolean > > test5;	// relatedType == Boolean
	
	public static void main( String[] args )
	{
//		AssociationRegistry.scan( "ch.liquidmind.inflection.test.assoc.model.*" );
		
		Type type = __Class.getDeclaredField( Test.class, "test5" ).getGenericType();
		RelatedTypeVisitor visitor = new RelatedTypeVisitor();
		TypeWalker walker = new TypeWalker( visitor );
		walker.walk( type );
		Type relatedType = visitor.getRelatedType();
	}
}
