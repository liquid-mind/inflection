package ch.liquidmind.inflection.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;

public abstract class RelatingTypeVisitor extends AbstractTypeVisitor
{
	private enum Mode { MapMode, CollectionMode }

	private Stack< Mode > modes = new Stack< Mode >();

	@Override
	public void visitParameterizedType( ParameterizedType parameterizedType )
	{
		Class< ? > rawType = (Class< ? >)parameterizedType.getRawType();
		
		if ( Collection.class.isAssignableFrom( rawType ) )
			modes.push( Mode.CollectionMode );
		else if ( Map.class.isAssignableFrom( rawType ) )
			modes.push( Mode.MapMode );
		else
			return;

		super.visitParameterizedType( parameterizedType );
		modes.pop();
	}

	@Override
	public void visitActualTypeArgument( Type actualTypeArgument, int index )
	{
		if ( !(modes.peek().equals( Mode.MapMode ) && index == 0) )
			super.visitActualTypeArgument( actualTypeArgument, index );
	}

	@Override
	public void visitRawType( Type rawType )
	{
		// Don't walk this path.
	}
}
