package ch.zhaw.inflection.operation.extended;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.operation.ClassViewFrame;

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
