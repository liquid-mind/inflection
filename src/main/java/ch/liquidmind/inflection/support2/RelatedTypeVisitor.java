package ch.liquidmind.inflection.support2;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class RelatedTypeVisitor extends AbstractTypeVisitor
{
	private Type relatedType;
	
	@Override
	public void visitType( Type type )
	{
		relatedType = type;
		super.visitType( type );
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

	@Override
	public void visitClass( Class< ? > classType )
	{
		if ( Collection.class.isAssignableFrom( classType ) || Map.class.isAssignableFrom( classType ) )
			relatedType = null;
	}

	public Type getRelatedType()
	{
		return relatedType;
	}
}
