package ch.liquidmind.inflection.test.model;

import java.util.List;

import ch.liquidmind.inflection.Auxiliary;
import ch.liquidmind.inflection.Inflection;
import ch.liquidmind.inflection.bidir.BidirectionalList;


public class PersonAuxiliary3 extends Auxiliary
{
	public List< Address > getAddressesUnidir()
	{
		Person person = Inflection.cast( Person.class, this );
		List< Address > addressesUnidir = new BidirectionalList< Address >( person, "people", person.getAddresses() );
		
		return addressesUnidir;
	}
}
