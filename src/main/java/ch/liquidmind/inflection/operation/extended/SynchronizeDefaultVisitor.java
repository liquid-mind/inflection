package ch.liquidmind.inflection.operation.extended;

import java.lang.reflect.Type;

import __java.lang.__Class;
import ch.liquidmind.inflection.operation.DimensionViewFrame;

public class SynchronizeDefaultVisitor extends SynchronizeAbstractVisitor
{
	@Override
	protected Object createRightObject( Class< ? > rightClass, Object leftObject )
	{
		Object rightObject;
		
		if ( rightClass.isEnum() )
			rightObject = leftObject;
		else
			rightObject = __Class.newInstance( rightClass );
		
		return rightObject;
	}

	@Override
	public void visit( DimensionViewFrame frame )
	{
		Type type = frame.getDimensionViewPair().getRightDimensionView().getDimensionType();
		Object rightObject = frame.getIdentifiableObjectPair().getRightObject().getObject();
		
		getTraverser().continueTraversal();
	}
}
