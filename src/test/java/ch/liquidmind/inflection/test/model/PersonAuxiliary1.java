package ch.liquidmind.inflection.test.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ch.liquidmind.inflection.Auxiliary;
import ch.liquidmind.inflection.Inflection;


public class PersonAuxiliary1 extends Auxiliary
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
}
