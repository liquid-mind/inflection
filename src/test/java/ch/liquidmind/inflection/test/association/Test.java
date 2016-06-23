package ch.liquidmind.inflection.test.association;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

import __java.lang.__Class;
import ch.liquidmind.inflection.association.AssociationRegistry;
import ch.liquidmind.inflection.association.Dimension;
import ch.liquidmind.inflection.print.AssociationPrinter;
import ch.liquidmind.inflection.support.DimensionsTypeVisitor;
import ch.liquidmind.inflection.support.RelatedTypeVisitor;
import ch.liquidmind.inflection.support.TypeWalker;
import ch.liquidmind.inflection.support.UnadornedClassVisitor;
import ch.liquidmind.inflection.test.association.model.Vehicle;

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
	@SuppressWarnings( "rawtypes" )
	List test12;
	
	public static void main( String[] args )
	{
		testAssociationRegistry();
//		testRelatedTypeVisitor();
//		testDimensionsTypeVisitor();
		System.out.println( "Done!" );
	}
	
	private static void testAssociationRegistry()
	{
		// TODO: Does PropertyUtils.getPropertyDescriptors() return protected and package level properties? We need
		// these as well...
		System.setProperty( AssociationRegistry.INCLUDE_FILTERS, Vehicle.class.getPackage().getName() + ".*" );
		AssociationRegistry registry = AssociationRegistry.instance();
		AssociationPrinter printer = new AssociationPrinter();
		printer.printClasses( registry.getRegisteredClasses() );
	}

	@SuppressWarnings( "unused" )
	private static void testDimensionsTypeVisitor()
	{
		testDimensionsTypeVisitor( "test1", new Dimension[] { new Dimension( String.class, false, true ) } );
		testDimensionsTypeVisitor( "test2", new Dimension[] { new Dimension( Class.class, false, true ) } );
		testDimensionsTypeVisitor( "test3", new Dimension[] { new Dimension( String.class, true, false ) } );
		testDimensionsTypeVisitor( "test4", new Dimension[] { new Dimension( Class.class, true, false ) } );
		testDimensionsTypeVisitor( "test5", new Dimension[] { new Dimension( List.class, true, false ) } );
		testDimensionsTypeVisitor( "test6", new Dimension[] { new Dimension( Map.class, false, false ) } );
		testDimensionsTypeVisitor( "test7", new Dimension[] { new Dimension( List.class, true, false ) , new Dimension( Set.class, false, true ) } );
		testDimensionsTypeVisitor( "test8", new Dimension[] { new Dimension( List.class, true, false ) } );
		testDimensionsTypeVisitor( "test9", new Dimension[] { new Dimension( List.class, true, false ), new Dimension( String.class, true, false ) } );
		testDimensionsTypeVisitor( "test10", new Dimension[] { new Dimension( List.class, true, false ) } );
		testDimensionsTypeVisitor( "test11", new Dimension[] { new Dimension( List.class, true, false ) } );
		testDimensionsTypeVisitor( "test12", new Dimension[] { new Dimension( List.class, true, false ) } );
	}
	
	private static void testDimensionsTypeVisitor( String fieldName, Dimension ... expectedDimensions )
	{
		Type type = __Class.getDeclaredField( Test.class, fieldName ).getGenericType();
		DimensionsTypeVisitor visitor = new DimensionsTypeVisitor();
		TypeWalker walker = new TypeWalker( visitor );
		walker.walk( type );
		List< Dimension > actualDimensions = visitor.getDimensions();
		
		Assert.assertEquals( expectedDimensions.length, actualDimensions.size() );
		
		for ( int i = 0 ; i < actualDimensions.size() ; ++i )
		{
			Class< ? > unadornedClass = getUnadornedClass( actualDimensions.get( i ).getTargetType() );
			Dimension actualDimensionAdjusted = new Dimension( unadornedClass, actualDimensions.get( i ).isOrdered(), actualDimensions.get( i ).isUnique() );
			
			Assert.assertEquals( expectedDimensions[ i ], actualDimensionAdjusted );
		}
	}
	
	private static Class< ? > getUnadornedClass( Type type )
	{
		UnadornedClassVisitor visitor = new UnadornedClassVisitor();
		TypeWalker walker = new TypeWalker( visitor );
		walker.walk( type );
		
		return visitor.getUnadornedClass();
	}
	
	@SuppressWarnings( "unused" )
	private static void testRelatedTypeVisitor()
	{
		Type classType = __Class.getDeclaredField( Test.class, "test2" ).getGenericType();

		testRelatedTypeVisitor( "test1", String.class );
		testRelatedTypeVisitor( "test2", classType );
		testRelatedTypeVisitor( "test3", String.class );
		testRelatedTypeVisitor( "test4", classType );
		testRelatedTypeVisitor( "test5", String.class );
		testRelatedTypeVisitor( "test6", String.class );
		testRelatedTypeVisitor( "test7", String.class );
		testRelatedTypeVisitor( "test8", classType );
		testRelatedTypeVisitor( "test9", String.class );
		testRelatedTypeVisitor( "test10", Object.class );
		testRelatedTypeVisitor( "test11", String.class );
		testRelatedTypeVisitor( "test12", Object.class );
	}
	
	private static void testRelatedTypeVisitor( String fieldName, Type expectedType )
	{
		Type type = __Class.getDeclaredField( Test.class, fieldName ).getGenericType();
		RelatedTypeVisitor visitor = new RelatedTypeVisitor();
		TypeWalker walker = new TypeWalker( visitor );
		walker.walk( type );
		Type actualType = visitor.getRelatedType();
		
		Assert.assertEquals( expectedType, actualType );
	}
}
