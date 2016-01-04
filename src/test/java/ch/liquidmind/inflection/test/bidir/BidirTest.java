package ch.liquidmind.inflection.test.bidir;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import __java.lang.__Class;
import __java.lang.reflect.__Field;

/*
 * Notes:
 * 1. The only way to catch member accesses via reflection is to intercept any
 *    invocations to reflection itself, i.e., @Before( "call( * java.lang.reflect.Field.set*(..) )" ).
 */
public class BidirTest
{
	@Test
	public void test()
	{
		Address address1 = new Address( "Feldgüetliweg 82", "Feldmeilen", "8706", "Switzerland" );
		Address address2 = new Address( "Karl Stauffer-Strasse 3", "Zürich", "8008", "Switzerland" );
		Address address3 = new Address( "Karl Stauffer-Strasse 4", "Zürich", "8008", "Switzerland" );
		Address address4 = new Address( "Karl Stauffer-Strasse 5", "Zürich", "8008", "Switzerland" );
		Address address5 = new Address( "Karl Stauffer-Strasse 6", "Zürich", "8008", "Switzerland" );
		// Test setting empty collection (happens in constructor).
		Person person1 = new Person( "John", "Brush" );
		Person person2 = new Person( "Zachary", "Brush" );

		// TESTS ON PERSON SIDE

		// Test adding single element to empty collection.
		person1.getAddresses().add( address1 );
		// Test adding single element to non-empty collection.
		person1.getAddresses().add( address2 );
		// Test removing single element from collection.
		person1.getAddresses().remove( address1 );
		// Test adding multiple elements to collection.
		person1.getAddresses().addAll( Arrays.asList( new Address[] { address3, address4, address5 } ) );
		// Test removing multiple elements from collection.
		person1.getAddresses().removeAll( Arrays.asList( new Address[] { address3, address4, address5 } ) );
		// Test adding element that already exists.
		person1.getAddresses().add( address2 );
		// Test removing element that doesn't exist.
		person1.getAddresses().remove( address1 );
		// Test setting a non-overlapping collection.
		person1.setAddresses( new ArrayList< Address >( Arrays.asList( new Address[] { address3, address4 } ) ) );
		// Test setting an overlapping collection.
		person1.setAddresses( new ArrayList< Address >( Arrays.asList( new Address[] { address4, address5 } ) ) );
		// Test clear().
		person1.getAddresses().clear();
		// Test retainAll().
		person1.getAddresses().addAll( Arrays.asList( new Address[] { address1, address2, address3, address4, address5 } ) );
		person1.getAddresses().retainAll( Arrays.asList( new Address[] { address2, address4 } ) );
		// Test setting to null.
		person1.setAddresses( null );
		// Test setting address already linked to other person.
		person1.setAddresses( new ArrayList< Address >() );
		person1.getAddresses().add( address1 );
		person2.getAddresses().add(  address1 );
		// Test setting on a collection that used to be registered.
		List< Address > oldAddresses = person2.getAddresses();
		person2.setAddresses( new ArrayList< Address >() );
		oldAddresses.add( address2 );
		
		// TODO:
		// 3. test setting on a collection that used to be registered.
		// 4. test operations through reflection.
		// 5. test everything with sets
		
		// TESTS ON ADDRESS SIDE

		// Test set to non-null value.
		address1.setPerson( person1 );
		// Test set to non-null value when non-null already set.
		address1.setPerson( person2 );
		// Test set to null value.
		address1.setPerson( null );
		// Test set to null value when null already set.
		address1.setPerson( null );
		
		Field street = __Class.getDeclaredField( Address.class, "street" );
		street.setAccessible( true );
		__Field.set( street, address1, null );
		Field person = __Class.getDeclaredField( Address.class, "person" );
		person.setAccessible( true );
		__Field.set( person, address1, null );
	}
}
