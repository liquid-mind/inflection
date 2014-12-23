package ch.liquidmind.inflection.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.liquidmind.inflection.InflectionResourceLoader;
import ch.liquidmind.inflection.compiler.InflectionCompilerBootstrap;
import ch.liquidmind.inflection.operation.basic.BasicOperations;
import ch.liquidmind.inflection.operation.extended.ExtendedOperations;
import ch.liquidmind.inflection.operation.extended.ValidationError;

// TODO Might remove DimensionView.isMapped and add DimensionView.isCardinal with the following consequences:
//     List, Array: isOrdered, isCardinal
//     Map: isCardinal
//     Set: (neither)
// Renaming:
//     OrderingDimensionViewInstance --> OrderingCardinalDimensionViewInstance
//     MappingDimensionViewInstance --> NonOrderingCardinalDimensionViewInstance
//     UnqualifiedDimensionViewInstance --> NonOrderingNonCardinalDimensionViewInstance
@SuppressWarnings( "unused" )
public class Demo
{
	public static void main( String[] args )
	{
		demo14();
	}
	
	private static void demo14()
	{
		compile();
		Customer customer = createCustomer();
		customer.setLastName( null );
		List< ValidationError > errors = ExtendedOperations.validate( customer, "ch.liquidmind.inflection.demo.FullGroup", "ch.liquidmind.inflection.demo.FullGroupValidation" );
		
		for ( ValidationError error : errors )
			System.out.println( error.getLocation() + ": " + error.getErrorMsg() );
	}
	
	private static void demo13()
	{
		compile();
		Customer customer = createCustomer();
		ExtendedOperations.toJson( customer, "ch.liquidmind.inflection.demo.FullGroup", null );
	}

	private static void demo12()
	{
		compile();

		A< String, ? > a1 = new A< String, Object >();
		A< String, ? > a2 = new A< String, Object >();
		
//		a1.b = new B( "b1" );	// a1 == a2
//		a2.b = new B( "b1" );
		
//		a1.b = null;	// a1 == a2
//		a2.b = null;
		
//		a1.b = new B( "b1" );	// a1 != a2
//		a2.b = new B( "b2" );
		
//		a1.b = new B( "b1" );	// a1 != a2
//		a2.b = null;
		
//		a1.b2s = new B[] { new B( "b1" ), new B( "b2" ) };	// a1 == a2
//		a2.b2s = new B[] { new B( "b1" ), new B( "b2" ) };
		
//		a1.b2s = new B[] { new B( "b1" ), new B( "b2" ) };	// a1 != a2
//		a2.b2s = new B[] { new B( "b1" ), new B( "b3" ) };
		
//		a1.b2s = new B[] { new B( "b1" ), new B( "b2" ) };	// a1 != a2
//		a2.b2s = new B[] { new B( "b1" ) };
		
//		a1.b2s = new B[] { new B( "b1" ), new B( "b2" ) };	// a1 != a2
//		a2.b2s = new B[] { new B( "b1" ), null };
		
//		a1.b2s = new B[] { new B( "b1" ), new B( "b2" ) };	// a1 != a2
//		a2.b2s = new B[] { new B( "b2" ), new B( "b1" ) };
		
//		a1.b3s = new B[][] { { new B( "b1" ), new B( "b2" ) }, { new B( "b3" ), new B( "b4" ) } };	// a1 == a2
//		a2.b3s = new B[][] { { new B( "b1" ), new B( "b2" ) }, { new B( "b3" ), new B( "b4" ) } };
		
//		a1.b3s = new B[][] { { new B( "b1" ), new B( "b2" ) }, { new B( "b3" ), new B( "b4" ) } };	// a1 != a2
//		a2.b3s = new B[][] { { new B( "b1" ), new B( "b2" ) }, { new B( "b3" ), new B( "b5" ) } };
		
//		a1.b5s = createList( new B( "b1" ), new B( "b2" ) );	// a1 == a2
//		a2.b5s = createList( new B( "b1" ), new B( "b2" ) );
		
//		a1.b5s = createList( new B( "b1" ), new B( "b2" ) );	// a1 != a2
//		a2.b5s = createList( new B( "b1" ), new B( "b3" ) );
		
//		a1.b6s = createList( createSet( new B( "b1" ), new B( "b2" ) ), createSet( new B( "b3" ), new B( "b4" ) ) );	// a1 == a2
//		a2.b6s = createList( createSet( new B( "b1" ), new B( "b2" ) ), createSet( new B( "b3" ), new B( "b4" ) ) );
		
//		a1.b6s = createList( createSet( new B( "b1" ), new B( "b2" ) ), createSet( new B( "b3" ), new B( "b4" ) ) );	// a1 == a2
//		a2.b6s = createList( createSet( new B( "b1" ), new B( "b2" ) ), createSet( new B( "b4" ), new B( "b3" ) ) );

//		a1.b6s = createList( createSet( new B( "b1" ), new B( "b2" ) ), createSet( new B( "b3" ), new B( "b4" ) ) );	// a1 != a2
//		a2.b6s = createList( createSet( new B( "b3" ), new B( "b4" ) ), createSet( new B( "b1" ), new B( "b2" ) ) );
		
//		a1.b7s = createMap( new Object[][] { { "key1", new B( "b1" ) }, { "key2", new B( "b2" ) } } );	// a1 == a2
//		a2.b7s = createMap( new Object[][] { { "key1", new B( "b1" ) }, { "key2", new B( "b2" ) } } );

//		a1.b7s = createMap( new Object[][] { { "key1", new B( "b1" ) }, { "key2", new B( "b2" ) } } );	// a1 == a2
//		a2.b7s = createMap( new Object[][] { { "key2", new B( "b2" ) }, { "key1", new B( "b1" ) } } );

//		a1.b7s = createMap( new Object[][] { { "key1", new B( "b1" ) }, { "key2", new B( "b2" ) } } );	// a1 != a2
//		a2.b7s = createMap( new Object[][] { { "key1", new B( "b1" ) }, { "key2", new B( "b3" ) } } );

//		a1.b7s = createMap( new Object[][] { { "key1", new B( "b1" ) }, { "key2", new B( "b2" ) } } );	// a1 != a2
//		a2.b7s = createMap( new Object[][] { { "key1", new B( "b1" ) }, { "key3", new B( "b2" ) } } );

//		a1.b7s = createMap( new Object[][] { { "key1", new B( "b1" ) }, { "key2", new B( "b2" ) } } );	// a1 != a2
//		a2.b7s = createMap( new Object[][] { { "key1", new B( "b1" ) }, { "key3", null } } );

//		a1.b13 = new B( "b1" );	// a1 == a2
//		a2.b13 = a1.b13;
		
//		a1.b13 = new B( "b1" );	// a1 != a2
//		a2.b13 = new B( "b1" );
				
//		a1.b13 = new B( "b1" );	// a1 != a2
//		a2.b13 = null;
		
		boolean isEqual = BasicOperations.equals( a1, a2, "ch.liquidmind.inflection.demo.ABGroup", null );
		System.out.println( "equals=" + isEqual );
	}
	
	private static void demo11()
	{
		compile();
		Customer customer1 = createCustomer();
		Customer customer2 = createCustomer();
		boolean isEqual = BasicOperations.equals( customer1, customer2, "ch.liquidmind.inflection.demo.FullGroup", null );
		System.out.println( "isEqual=" + isEqual );
	}
	
	private static void demo10()
	{
		compile();
		Customer customer = createCustomer();
		int hashcode = BasicOperations.hashcode( customer, "ch.liquidmind.inflection.demo.FullGroup", null );
		System.out.println( "hashcode=" + hashcode );
	}
	
	private static void demo9()
	{
		compile();
		Customer customer = createCustomer();
		BasicOperations.toText( customer, "ch.liquidmind.inflection.demo.FullGroup", null );
	}
	
	private static void demo8()
	{
		compile();
		Customer customer = createCustomer();
		BasicOperations.metaModelToText( customer, "ch.liquidmind.inflection.demo.FullGroup", null );
	}
	
	@SuppressWarnings( "unchecked" )
	private static < T > List< T > createList( T ... objects )
	{
		List< T > objectsAsList = new ArrayList< T >();
		
		for ( T object : objects )
			objectsAsList.add( object );
		
		return objectsAsList;
	}
	
	@SuppressWarnings( "unchecked" )
	private static < T > Set< T > createSet( T ... objects )
	{
		Set< T > objectsAsSet = new HashSet< T >();
		
		for ( T object : objects )
			objectsAsSet.add( object );
		
		return objectsAsSet;
	}
	
	@SuppressWarnings( "unchecked" )
	private static < T, V > Map< T, V > createMap( Object[][] keyValuePairs )
	{
		Map< T, V > objectsAsMap = new HashMap< T, V >();
		
		for ( int i = 0 ; i < keyValuePairs.length ; ++i )
		{
			T key = (T)keyValuePairs[ i ][ 0 ];
			V value = (V)keyValuePairs[ i ][ 1 ];
			objectsAsMap.put( key, value );
		}
		
		return objectsAsMap;
	}
	
	private static void compile()
	{
		InflectionResourceLoader loader = InflectionResourceLoader.getSystemInflectionResourceLoader();
		InflectionCompilerBootstrap.compile(
			new File[] {
				new File( "./src/main/resources/ch/liquidmind/inflection/Inflection.inflect" ),
				new File( "./src/main/resources/ch/liquidmind/inflection/InflectionOperationBasic.inflect" ),
				new File( "./src/main/resources/ch/liquidmind/inflection/InflectionOperationExtended.inflect" ),
				new File( "./src/main/resources/ch/liquidmind/inflection/demo/Demo.inflect" ) },
			new File( "./target/views" ), loader );
	}
	
	private static Customer createCustomer()
	{
		Address address1 = new Address( "Karl Stauffer-Strasse 3", "Zurich", "8008", "Switzerland" );
		Address address2 = new Address( "Dufourstr. 117", "Zurich", "8008", "Switzerland" );
		
		Customer customer =  new Customer( "John", "Brush", 42, Customer.Sex.MALE, address1, address2, address2 );
		
		return customer;
	}
}

