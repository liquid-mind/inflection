package ch.liquidmind.inflection.test.assoc;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import __java.lang.__Class;
import ch.liquidmind.inflection.support2.RelatedTypeWalker;

public class Test
{
	List< Set< Map< List< String >, Set< Integer > > > > test;
	
	public static void main( String[] args )
	{
//		AssociationRegistry.scan( "ch.liquidmind.inflection.test.assoc.model.*" );
		
		Type type = __Class.getDeclaredField( Test.class, "test" ).getGenericType();
		RelatedTypeWalker walker = new RelatedTypeWalker();
		walker.walkType( type );
	}
}