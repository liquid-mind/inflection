package ch.liquidmind.inflection.test.model;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import __java.lang.__Class;
import ch.liquidmind.inflection.bidir.BidirectionalList;

public class CalculatedMembers
{
	public static int getAge( Person person )
	{
		Calendar c = new GregorianCalendar ();
		c.setTime( person.getDateOfBirth() );
		return (int)-ChronoUnit.YEARS.between(
			LocalDate.now(), LocalDate.of(
			c.get( Calendar.YEAR ),
			c.get( Calendar.MONTH ), c.get( Calendar.DAY_OF_MONTH )
			) );
	}

	public static String getFullName( Person person )
	{
		return person.getFirstName() + " " + person.getLastName();
	}
	
	public static Address getPrimaryAddress( Person person )
	{
		return ( person.getAddresses().size() == 0 ? null : person.getAddresses().get( 0 ) );
	}
	
	private static final Field PEOPLE_FIELD = __Class.getDeclaredField( Address.class, "people" );
	
	static
	{
		PEOPLE_FIELD.setAccessible( true );
	}
	
	public static List< Address > getAddressesUnidir( Person person )
	{
		return new BidirectionalList<>( person, PEOPLE_FIELD, person.getAddresses() );
	}
}
