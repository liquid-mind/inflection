package ch.liquidmind.inflection.operation.extended;

import java.lang.reflect.Constructor;

import __java.lang.__Class;
import __java.lang.reflect.__Constructor;

public class SynchronizeBasicTypeVisitor extends SynchronizeAbstractVisitor
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected Object createRightObject( Class< ? > rightClass, Object leftObject )
	{
		Constructor< ? > constructor = __Class.getConstructor( rightClass, rightClass );
		Object rightObject = __Constructor.newInstance( constructor, leftObject );

		return rightObject;
	}
}
