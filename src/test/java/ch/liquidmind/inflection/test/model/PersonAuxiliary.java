package ch.liquidmind.inflection.test.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ch.liquidmind.inflection.Inflection;
import ch.liquidmind.inflection.bidir.BidirectionalList;

public class PersonAuxiliary
{
	public int getAge()
	{
		Person person = Inflection.cast( Person.class, this );
		Calendar c = new GregorianCalendar ();
		c.setTime( person.getDateOfBirth() );
		return (int)-ChronoUnit.YEARS.between(
			LocalDate.now(), LocalDate.of(
			c.get( Calendar.YEAR ),
			c.get( Calendar.MONTH ), c.get( Calendar.DAY_OF_MONTH )
			) );
	}
	
	public String getFullName()
	{
		Person person = Inflection.cast( Person.class, this );
		String fullName = person.getFirstName() + " " + person.getLastName();
		
		return fullName;
	}
	
	public Address getPrimaryAddress()
	{
		Person person = Inflection.cast( Person.class, this );
		Address primaryAddress = ( person.getAddresses().size() == 0 ? null : person.getAddresses().get( 0 ) );
		
		return primaryAddress;
	}
	
	public List< Address > getAddressesUnidir()
	{
		Person person = Inflection.cast( Person.class, this );
		List< Address > addressesUnidir = new BidirectionalList< Address >( person, "people", person.getAddresses() );
		
		return addressesUnidir;
	}
	
}
