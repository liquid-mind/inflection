package ch.liquidmind.inflection.support;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public class TypeWalker
{
	private TypeVisitor visitor;

	public TypeWalker( TypeVisitor visitor )
	{
		super();
		this.visitor = visitor;
		visitor.setTypeWalker( this );
	}
	
	public void walk( List< Type > types )
	{
		visitor.visitTypes( types );
	}

	public void walk( Type type )
	{
		visitor.visitType( type );
	}
	
	public void walkTypes( List< Type > types )
	{
		for ( Type type : types )
			visitor.visitType( type );
	}

	public void walkType( Type type )
	{
		if ( type instanceof GenericArrayType )
			visitor.visitGenericArrayType( (GenericArrayType)type );
		else if ( type instanceof ParameterizedType )
			visitor.visitParameterizedType( (ParameterizedType)type );
		else if ( type instanceof TypeVariable )
			visitor.visitTypeVariable( (TypeVariable< ? >)type );
		else if ( type instanceof WildcardType )
			visitor.visitWildcardType( (WildcardType)type );
		else if ( type instanceof Class )
			visitor.visitClass( (Class< ? >)type );
		else
			throw new IllegalStateException( "Unexpected type for type: " + type );
	}
	
	public void walkGenericArrayType( GenericArrayType genericArrayType )
	{
		Type genericComponentType = genericArrayType.getGenericComponentType();
		
		if ( genericComponentType != null )
			visitor.visitGenericComponentType( genericComponentType );
		
		visitor.visitType( genericArrayType.getGenericComponentType() );
	}

	public void walkGenericComponentType( Type genericComponentType )
	{
		visitor.visitType( genericComponentType );
	}
	
	public void walkParameterizedType( ParameterizedType parameterizedType )
	{
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		
		if ( actualTypeArguments.length > 0 )
			visitor.visitActualTypeArguments( actualTypeArguments );
	}

	public void walkActualTypeArguments( Type[] actualTypeArguments )
	{
		for ( Type actualTypeArgument : actualTypeArguments )
			visitor.visitActualTypeArgument( actualTypeArgument );
	}
	
	public void walkActualTypeArgument( Type actualTypeArgument )
	{
		visitor.visitType( actualTypeArgument );
	}
	
	public void walkTypeVariable( TypeVariable< ? > typeVariable )
	{
		Type[] typeVariableBounds = typeVariable.getBounds();
		
		if ( typeVariableBounds.length > 0 )
			visitor.visitTypeVariableBounds( typeVariable.getBounds() );
	}
	
	public void walkTypeVariableBounds( Type[] typeVariableBounds )
	{
		for ( Type typeVariableBoundary : typeVariableBounds )
			visitor.visitTypeVariableBoundary( typeVariableBoundary );
	}
	
	public void walkTypeVariableBoundary( Type typeVariableBoundary )
	{
		visitor.visitType( typeVariableBoundary );
	}
	
	public void walkWildcardType( WildcardType wildcardType )
	{
		Type[] wildcardTypeUpperBounds = wildcardType.getUpperBounds();
		Type[] wildcardTypeLowerBounds = wildcardType.getLowerBounds();
		
		if ( wildcardTypeUpperBounds.length > 0 )
			visitor.visitWildcardTypeUpperBounds( wildcardTypeUpperBounds );
		
		if ( wildcardTypeLowerBounds.length > 0 )
			visitor.visitWildcardTypeLowerBounds( wildcardTypeLowerBounds );
		
		if ( wildcardTypeUpperBounds.length == 0 && wildcardTypeLowerBounds.length == 0 )
			throw new IllegalStateException( "Upper and lower bounds are both empty." );
	}

	public void walkWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds )
	{
		for ( Type wildcardTypeUpperBoundary : wildcardTypeUpperBounds )
			visitor.visitWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
	}
	
	public void walkWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary )
	{
		visitor.visitType( wildcardTypeUpperBoundary );
	}

	public void walkWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds )
	{
		for ( Type wildcardTypeLowerBoundary : wildcardTypeLowerBounds )
			visitor.visitWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
	}
	
	public void walkWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary )
	{
		visitor.visitType( wildcardTypeLowerBoundary );
	}
	
	public void walkClass( Class< ? > classType )
	{
		Class< ? > componentType = classType.getComponentType();
		
		if ( componentType != null )
			visitor.visitComponentType( componentType );
	}

	public void walkComponentType( Class< ? > componentType )
	{
		visitor.visitType( componentType );
	}	
}
