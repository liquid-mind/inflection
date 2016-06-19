package ch.liquidmind.inflection.support;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ch.liquidmind.inflection.association.Dimension;

public class DimensionsTypeVisitor extends AbstractTypeVisitor
{
	private enum Mode { MapMode, CollectionMode }
	
	private List< Dimension > dimensions = new ArrayList< Dimension >();
	private Stack< Mode > modes = new Stack< Mode >();
	private int typeCounter = 0;

	@Override
	public void visitType( Type type )
	{
		++typeCounter;
		super.visitType( type );
		--typeCounter;
		
		if ( typeCounter == 0 && dimensions.isEmpty() )
			dimensions.add( new Dimension( type, false, true ) );
	}

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
		
		dimensions.add( createDimension( parameterizedType, rawType ) );

		super.visitParameterizedType( parameterizedType );
		modes.pop();
	}
	
	private Dimension createDimension( Type type, Class< ? > rawType )
	{
		boolean isOrdered, isUnique;

		if ( List.class.isAssignableFrom( rawType ) )
		{
			isOrdered = true;
			isUnique = false;
		}
		else if ( Set.class.isAssignableFrom( rawType ) )
		{
			isOrdered = false;
			isUnique = true;
		}
		else if ( Map.class.isAssignableFrom( rawType ) )
		{
			isOrdered = false;
			isUnique = false;
		}
		else
		{
			throw new IllegalStateException( "Unexpected type for rawType: " + rawType.getName() );
		}
		
		return new Dimension( type, isOrdered, isUnique );
	}

	@Override
	public void visitRawType( Type rawType )
	{
		// Don't walk this path.
	}

	@Override
	public void visitGenericArrayType( GenericArrayType genericArrayType )
	{
		dimensions.add( new Dimension( genericArrayType, true, false ) );
		super.visitGenericArrayType( genericArrayType );
	}

	@Override
	public void visitClass( Class< ? > classType )
	{
		if ( classType.getComponentType() != null )
			dimensions.add( new Dimension( classType, true, false ) );
		else if ( Collection.class.isAssignableFrom( classType ) || Map.class.isAssignableFrom( classType ) )
			dimensions.add( createDimension( classType, classType ) );
		
		super.visitClass( classType );
	}

	@Override
	public void visitActualTypeArgument( Type actualTypeArgument, int index )
	{
		if ( !(modes.peek().equals( Mode.MapMode ) && index == 0) )
			super.visitActualTypeArgument( actualTypeArgument, index );
	}

	public List< Dimension > getDimensions()
	{
		return dimensions;
	}
}
