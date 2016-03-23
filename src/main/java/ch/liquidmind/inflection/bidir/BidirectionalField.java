package ch.liquidmind.inflection.bidir;

import java.util.Collection;

import ch.liquidmind.inflection.exception.ExceptionWrapper;

public class BidirectionalField
{
	@SuppressWarnings( "unchecked" )
	public static void set( Object objectA, Object objectB, String propertyNameBinA, String propertyNameAinB )
	{
		Collection< Object > opposingCollection = (Collection< Object >)ExceptionWrapper.PropertyUtils_getProperty( objectB, propertyNameAinB );
		Object oldValue = ExceptionWrapper.PropertyUtils_getProperty( objectA, propertyNameBinA );
		
		if ( oldValue != null )
			opposingCollection.remove( objectA );
		
		opposingCollection.add( objectA );
		
		ExceptionWrapper.PropertyUtils_setProperty( objectA, propertyNameBinA, objectB );
	}

}
