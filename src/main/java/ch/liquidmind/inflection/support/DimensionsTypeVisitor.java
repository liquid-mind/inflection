package ch.liquidmind.inflection.support;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.liquidmind.inflection.association.Dimension;

public class DimensionsTypeVisitor extends RelatingTypeVisitor
{
	private List< Dimension > dimensions = new ArrayList< Dimension >();
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
		
		if ( Collection.class.isAssignableFrom( rawType ) || Map.class.isAssignableFrom( rawType ) )
			dimensions.add( createDimension( parameterizedType, rawType ) );

		super.visitParameterizedType( parameterizedType );
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

	public List< Dimension > getDimensions()
	{
		return dimensions;
	}
}
