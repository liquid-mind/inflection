package ch.liquidmind.inflection.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.liquidmind.inflection.association.Dimension;

public class DimensionsTypeVisitor extends AbstractTypeVisitor
{
	private List< Dimension > dimensions = new ArrayList< Dimension >();
	
	@Override
	public void visitType( Type type )
	{
//		if ( )
//		
//		dimensions.add( new Dimension() )
//		super.visitType( type );
	}
	
	@Override
	public void visitParameterizedType( ParameterizedType parameterizedType )
	{
		Class< ? > rawType = (Class< ? >)parameterizedType.getRawType();
		
		if ( Collection.class.isAssignableFrom( rawType ) )
			super.visitParameterizedType( parameterizedType );
		
		// If this is a map --> skip the key, walk the value.
		else if ( Map.class.isAssignableFrom( rawType ) )
			getTypeWalker().walkActualTypeArgument( parameterizedType.getActualTypeArguments()[ 1 ] );
	}
}
