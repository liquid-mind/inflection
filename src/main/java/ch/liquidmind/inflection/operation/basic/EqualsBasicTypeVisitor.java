package ch.liquidmind.inflection.operation.basic;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.operation.ClassViewFrame;
import ch.liquidmind.inflection.operation.ClassViewPair;

public class EqualsBasicTypeVisitor extends EqualsAbstractVisitor
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		ClassViewPair pair = frame.getClassViewPair();
		IdentifiableObject< ?, ? > objectLeft = pair.getLeftObject();
		IdentifiableObject< ?, ? > objectRight = pair.getRightObject();
		boolean isEqual;

		if ( !areBothNullOrNotNull( objectLeft, objectRight ) )
			isEqual = false;
		if ( objectLeft.getObject().equals( objectRight.getObject() ) )
			isEqual = true;
		else
			isEqual = false;
		
		returnEquality( pair, isEqual );
	}
}
