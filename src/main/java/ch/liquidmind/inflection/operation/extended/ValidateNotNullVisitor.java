package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.operation.ClassViewFrame;

public class ValidateNotNullVisitor extends ValidateAbstractVisitor
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		IdentifiableObject< ?, ? > leftObject = frame.getClassViewPair().getLeftObject();
		
		if ( leftObject == null )
			getTraverser().getValidationErrors().add( new ValidationError( getLocation(), "Value must not be null." ) );
	}
}
