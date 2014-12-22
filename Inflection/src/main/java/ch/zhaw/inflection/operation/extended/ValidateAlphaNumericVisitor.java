package ch.zhaw.inflection.operation.extended;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.operation.ClassViewFrame;

public class ValidateAlphaNumericVisitor extends ValidateAbstractVisitor
{
	private static final String ALPHA_NUMERIC_PATTERN = "^[a-zA-Z0-9]*$";
	
	@Override
	public void visit( ClassViewFrame frame )
	{
		IdentifiableObject< ?, ? > leftObject = frame.getClassViewPair().getLeftObject();
		
		if ( leftObject != null )
		{
			Object leftRawObject = leftObject.getObject();
			
			if ( leftRawObject instanceof String )
			{
				String leftRawObjectAsString = (String)leftRawObject;
				
				if ( !leftRawObjectAsString.matches( ALPHA_NUMERIC_PATTERN ) )
					getTraverser().getValidationErrors().add( new ValidationError( getLocation(), "Value must be alphanumeric ([a-zA-Z0-9])." ) );
			}
		}
	}
}
