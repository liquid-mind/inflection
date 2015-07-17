package ch.liquidmind.inflection.operation.extended;


public class SynchronizeBasicTypeVisitor extends SynchronizeAbstractVisitor
{
	// Note that this works because Java basic types are immutable.
	@Override
	protected Object synchronizeRightObject( Class< ? > rightClass, Object leftObject, Object rightObject )
	{
		return leftObject;
	}
}
