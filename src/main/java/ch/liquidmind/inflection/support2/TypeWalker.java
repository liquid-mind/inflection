package ch.liquidmind.inflection.support2;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public abstract class TypeWalker
{
	private TypeVisitor visitor;

	public TypeWalker( TypeVisitor visitor )
	{
		super();
		this.visitor = visitor;
		visitor.setTypeWalker( this );
	}
	
	public void walkTypes( List< Type > types )
	{
		visitor.visitTypes( types );
	}
	
	public void continueTypes( List< Type > types )
	{
		for ( Type type : types )
			walkType( type );
	}

	public void walkType( Type type )
	{
		visitor.visitType( type );
	}
	
	public void continueType( Type type )
	{
		if ( type instanceof GenericArrayType )
			walkGenericArrayType( (GenericArrayType)type );
		else if ( type instanceof ParameterizedType )
			walkParameterizedType( (ParameterizedType)type );
		else if ( type instanceof TypeVariable )
			walkTypeVariable( (TypeVariable< ? >)type );
		else if ( type instanceof WildcardType )
			walkWildcardType( (WildcardType)type );
		else if ( type instanceof Class )
			walkClass( (Class< ? >)type );
		else
			throw new IllegalStateException( "Unexpected type for type: " + type );
	}

	public void walkGenericArrayType( GenericArrayType genericArrayType )
	{
		visitor.visitGenericArrayType( genericArrayType );
	}
	
	public void continueGenericArrayType( GenericArrayType genericArrayType )
	{
		Type genericComponentType = genericArrayType.getGenericComponentType();
		
		if ( genericComponentType != null )
			walkGenericComponentType( genericComponentType );
		
		walkType( genericArrayType.getGenericComponentType() );
	}

	public void walkGenericComponentType( Type genericComponentType )
	{
		visitor.visitGenericComponentType( genericComponentType );
	}

	public void continueGenericComponentType( Type genericComponentType )
	{
		walkType( genericComponentType );
	}

	public void walkParameterizedType( ParameterizedType parameterizedType )
	{
		visitor.visitParameterizedType( parameterizedType );
	}
	
	public void continueParameterizedType( ParameterizedType parameterizedType )
	{
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		
		if ( actualTypeArguments.length > 0 )
			walkActualTypeArguments( actualTypeArguments );
	}

	public void walkActualTypeArguments( Type[] actualTypeArguments )
	{
		visitor.visitActualTypeArguments( actualTypeArguments );
	}

	public void continueActualTypeArguments( Type[] actualTypeArguments )
	{
		for ( Type actualTypeArgument : actualTypeArguments )
			walkActualTypeArgument( actualTypeArgument );
	}
	
	public void walkActualTypeArgument( Type actualTypeArgument )
	{
		visitor.visitActualTypeArgument( actualTypeArgument );
	}
	
	public void continueActualTypeArgument( Type actualTypeArgument )
	{
		walkType( actualTypeArgument );
	}
	
	public void walkTypeVariable( TypeVariable< ? > typeVariable )
	{
		visitor.visitTypeVariable( typeVariable );
	}
	
	public void continueTypeVariable( TypeVariable< ? > typeVariable )
	{
		Type[] typeVariableBounds = typeVariable.getBounds();
		
		if ( typeVariableBounds.length > 0 )
			walkTypeVariableBounds( typeVariable.getBounds() );
	}
	
	public void walkTypeVariableBounds( Type[] typeVariableBounds )
	{
		visitor.visitTypeVariableBounds( typeVariableBounds );
	}
	
	public void continueTypeVariableBounds( Type[] typeVariableBounds )
	{
		for ( Type typeVariableBoundary : typeVariableBounds )
			walkTypeVariableBoundary( typeVariableBoundary );
	}

	public void walkTypeVariableBoundary( Type typeVariableBoundary )
	{
		visitor.visitTypeVariableBoundary( typeVariableBoundary );
	}
	
	public void continueTypeVariableBoundary( Type typeVariableBoundary )
	{
		walkType( typeVariableBoundary );
	}
	
	public void walkWildcardType( WildcardType wildcardType )
	{
		visitor.visitWildcardType( wildcardType );
	}
	
	public void continueWildcardType( WildcardType wildcardType )
	{
		Type[] wildcardTypeUpperBounds = wildcardType.getUpperBounds();
		Type[] wildcardTypeLowerBounds = wildcardType.getLowerBounds();
		
		if ( wildcardTypeUpperBounds.length > 0 )
			walkWildcardTypeUpperBounds( wildcardTypeUpperBounds );
		
		if ( wildcardTypeLowerBounds.length > 0 )
			walkWildcardTypeLowerBounds( wildcardTypeLowerBounds );
		
		if ( wildcardTypeUpperBounds.length == 0 && wildcardTypeLowerBounds.length == 0 )
			throw new IllegalStateException( "Upper and lower bounds are both empty." );
	}

	public void walkWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds )
	{
		visitor.visitWildcardTypeUpperBounds( wildcardTypeUpperBounds );
	}

	public void continueWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds )
	{
		for ( Type wildcardTypeUpperBoundary : wildcardTypeUpperBounds )
			walkWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
	}
	
	public void walkWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary )
	{
		visitor.visitWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
	}
	
	public void continueWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary )
	{
		walkType( wildcardTypeUpperBoundary );
	}

	public void walkWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds )
	{
		visitor.visitWildcardTypeLowerBounds( wildcardTypeLowerBounds );
	}

	public void continueWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds )
	{
		for ( Type wildcardTypeLowerBoundary : wildcardTypeLowerBounds )
			walkWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
	}
	
	public void walkWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary )
	{
		visitor.visitWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
	}
	
	public void continueWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary )
	{
		walkType( wildcardTypeLowerBoundary );
	}
	
	public void walkClass( Class< ? > classType )
	{
		visitor.visitClass( classType );
	}
	
	public void continueClass( Class< ? > classType )
	{
		Class< ? > componentType = classType.getComponentType();
		
		if ( componentType != null )
			walkComponentType( componentType );
	}

	public void walkComponentType( Class< ? > componentType )
	{
		visitor.visitComponentType( componentType );
	}

	public void continueComponentType( Class< ? > componentType )
	{
		walkType( componentType );
	}	
}
