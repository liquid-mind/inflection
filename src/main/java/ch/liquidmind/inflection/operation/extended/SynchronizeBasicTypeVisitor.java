package ch.liquidmind.inflection.operation.extended;

import java.util.HashMap;
import java.util.Map;

public class SynchronizeBasicTypeVisitor extends SynchronizeAbstractVisitor
{
	private static Map< Class< ? >, Class< ? > > basicTypeToWrapperMap = new HashMap< Class< ? >, Class< ? > >();
	
	static
	{
		basicTypeToWrapperMap.put( byte.class, Byte.class );
		basicTypeToWrapperMap.put( short.class, Short.class );
		basicTypeToWrapperMap.put( int.class, Integer.class );
		basicTypeToWrapperMap.put( long.class, Long.class );
		basicTypeToWrapperMap.put( float.class, Float.class );
		basicTypeToWrapperMap.put( double.class, Double.class );
		basicTypeToWrapperMap.put( char.class, Character.class );
		basicTypeToWrapperMap.put( boolean.class, Boolean.class );
	}

	// Note that this works because Java basic types are immutable.
	@Override
	protected Object synchronizeRightObject( Class< ? > rightClass, Object leftObject, Object rightObject )
	{
		return leftObject;
	}
}
