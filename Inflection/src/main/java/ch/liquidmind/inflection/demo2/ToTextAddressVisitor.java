package ch.liquidmind.inflection.demo2;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.operation.ClassViewFrame;
import ch.liquidmind.inflection.operation.basic.ToTextAbstractVisitor;

public class ToTextAddressVisitor extends ToTextAbstractVisitor
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		IdentifiableObject< ?, ? > iObject = frame.getClassViewPair().getLeftObject();
		Address address = (Address)iObject.getObject();
		println( "address" + frame.getPositionCurrent() + " : " + address.getStreet() + ", " + address.getZip() + ", " + address.getCity() + ", " + address.getCountry() );
	}
}
