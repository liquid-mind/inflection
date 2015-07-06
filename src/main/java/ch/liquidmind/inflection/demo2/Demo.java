package ch.liquidmind.inflection.demo2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

@SuppressWarnings( "unused" )
public class Demo
{
	public static void main( String[] args )
	{
		demo4();
		
		// Ideas:
		// Rename hgroup to taxonomy and vmap to visitors
		// public class Inflection
		// {
		//     public static void addImport( String import );      Adds, removes and fetchs imports, which may be either single types or entire packages.
		//     public static void removeImport( String import );
		//     public static List< String > getImports();
		//     public static void setDefaultTaxonomy( String taxonomy )   Sets the default taxonomy for this session/thread.
		//     public static void setDefaultVisitors( String visitors )   Sets the default taxonomy for this session/thread.
		// }
	}

	// Demonstrate multiple visitors and visitors on member views.
	private static void demo7()
	{
		compile();
		Customer customer = createCustomer();
		customer.setLastName( null );
		List< ValidationError > errors = ExtendedOperations.validate( customer, "ch.liquidmind.inflection.demo2.FullTaxonomy", "ch.liquidmind.inflection.demo2.DemoValidation" );
		
		for ( ValidationError error : errors )
			System.out.println( error.getLocation() + ": " + error.getErrorMsg() );
	}

	// Demonstrate custom visitors.
	private static void demo6()
	{
		compile();
		Customer customer = createCustomer();
		BasicOperations.toText( customer, "ch.liquidmind.inflection.demo2.FullTaxonomy", "ch.liquidmind.inflection.demo2.DemoToTextWithAddressVisitorMap" );
	}
	
	// Demonstrate other algorithms with same views.
	private static void demo5()
	{
		compile();
		Customer customer = createCustomer();
		customer.getAddresses().get( 0 ).setCountry( "Iceland" );
		int hashcode = BasicOperations.hashcode( customer, "ch.liquidmind.inflection.demo2.FullTaxonomy", "ch.liquidmind.inflection.operation.basic.HashCodeTraverserConfiguration" );
		System.out.println( "hashcode=" + hashcode );
	}

	// Demonstrate other algorithms with same views.
	private static void demo4()
	{
		compile();
		Customer customer = createCustomer();
		ExtendedOperations.toJson( customer, "ch.liquidmind.inflection.demo2.FullTaxonomy", "ch.liquidmind.inflection.demo2.DemoToJsonMap" );
	}
	
	// Demonstrate IDs and references.
	private static void demo3()
	{
		compile();
		Customer customer = createCustomer();
		customer.getAddresses().add( customer.getAddresses().get( 0 ) );
		BasicOperations.toText( customer, "ch.liquidmind.inflection.demo2.FullTaxonomy", "ch.liquidmind.inflection.demo2.DemoToTextMap" );
	}
	
	// Demonstrate FullTaxonomy.
	private static void demo2()
	{
		compile();
		Customer customer = createCustomer();
		BasicOperations.toText( customer, "ch.liquidmind.inflection.demo2.FullTaxonomy", "ch.liquidmind.inflection.demo2.DemoToTextMap" );
	}
	
	// Demonstrate SimpleTaxonomy.
	private static void demo1()
	{
		compile();
		Customer customer = createCustomer();
		BasicOperations.toText( customer, "ch.liquidmind.inflection.demo2.SimpleTaxonomy", "ch.liquidmind.inflection.demo2.DemoToTextMap" );
	}
	
	private static void compile()
	{
		InflectionResourceLoader loader = InflectionResourceLoader.getSystemInflectionResourceLoader();
		InflectionCompilerBootstrap.compile(
			new File[] {
				new File( "./src/main/resources/ch/liquidmind/inflection/Inflection.inflect" ),
				new File( "./src/main/resources/ch/liquidmind/inflection/InflectionOperationBasic.inflect" ),
				new File( "./src/main/resources/ch/liquidmind/inflection/InflectionOperationExtended.inflect" ),
				new File( "./src/main/resources/ch/liquidmind/inflection/demo/Demo2.inflect" ) },
			new File( "./target/views" ), loader );
	}
	
	private static Customer createCustomer()
	{
		Address[] addresses = {
			new Address( "Karl Stauffer-Strasse 3", "Zurich", "8008", "Switzerland" ),
			new Address( "Dufourstr. 117", "Zurich", "8008", "Switzerland" )
		};
		
		Account[] accounts = {
			new Account( "402593-40", "CH87 0483 5040 2593 4000 0", "CRESCHZZ80A", AccountType.Checking, "4835" )
		};
		
		Customer customer =  new Customer( "John", "Brush", 42, Gender.Male,
			new ArrayList< Address >( Arrays.asList( addresses ) ), new ArrayList< Account >( Arrays.asList( accounts ) ) );
		
		return customer;
	}
}

