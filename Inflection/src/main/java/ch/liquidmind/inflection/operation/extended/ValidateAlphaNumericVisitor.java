package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.operation.ClassViewFrame;

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
