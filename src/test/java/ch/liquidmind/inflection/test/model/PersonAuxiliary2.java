package ch.liquidmind.inflection.test.model;

import ch.liquidmind.inflection.Auxiliary;
import ch.liquidmind.inflection.Inflection;


@Auxiliary( "ch.liquidmind.inflection.test.model.UseCase3" )
public class PersonAuxiliary2
{
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
}
