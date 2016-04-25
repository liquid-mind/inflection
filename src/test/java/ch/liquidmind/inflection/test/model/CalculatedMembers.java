package ch.liquidmind.inflection.test.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
		return ( person.getAddresses().length == 0 ? null : person.getAddresses()[ 0 ] );
	}
	
	public static List< Address > getAddressesUnidir( Person person )
	{
		return new BidirectionalList< Address >( person, "people", person.getAddresses() );
	}
}
