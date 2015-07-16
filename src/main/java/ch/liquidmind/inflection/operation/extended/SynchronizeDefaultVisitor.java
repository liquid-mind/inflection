package ch.liquidmind.inflection.operation.extended;

import __java.lang.__Class;

public class SynchronizeDefaultVisitor extends SynchronizeAbstractVisitor
{
	@Override
	protected Object synchronizeRightObject( Class< ? > rightClass, Object leftObject, Object rightObject )
	{
		Object rightObjectSynchronized;
		
		if ( rightClass.isEnum() )
			rightObjectSynchronized = leftObject;
		else if ( rightObject == null )
			rightObjectSynchronized = __Class.newInstance( rightClass );
		else
			rightObjectSynchronized = rightObject;
		
		return rightObjectSynchronized;
	}

}
