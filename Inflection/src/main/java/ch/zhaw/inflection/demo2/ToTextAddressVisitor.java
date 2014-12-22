package ch.zhaw.inflection.demo2;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.operation.ClassViewFrame;
import ch.zhaw.inflection.operation.basic.ToTextAbstractVisitor;

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
