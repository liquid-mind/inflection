package ch.liquidmind.inflection.support;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public abstract class AbstractTypeVisitor implements TypeVisitor
{
	private TypeWalker typeWalker;

	@Override
	public void setTypeWalker( TypeWalker typeWalker )
	{
		this.typeWalker = typeWalker;
	}

	@Override
	public TypeWalker getTypeWalker()
	{
		return typeWalker;
	}

	@Override
	public void visitTypes( List< Type > types )
	{
		typeWalker.walkTypes( types );
	}

	@Override
	public void visitType( Type type )
	{
		typeWalker.walkType( type );
	}

	@Override
	public void visitGenericArrayType( GenericArrayType genericArrayType )
	{
		typeWalker.walkGenericArrayType( genericArrayType );
	}

	@Override
	public void visitGenericComponentType( Type genericComponentType )
	{
		typeWalker.walkGenericComponentType( genericComponentType );
	}

	@Override
	public void visitParameterizedType( ParameterizedType parameterizedType )
	{
		typeWalker.walkParameterizedType( parameterizedType );
	}

	@Override
	public void visitRawType( Type rawType )
	{
		typeWalker.walkRawType( rawType );
	}

	@Override
	public void visitActualTypeArguments( Type[] actualTypeArguments )
	{
		typeWalker.walkActualTypeArguments( actualTypeArguments );
	}

	@Override
	public void visitActualTypeArgument( Type actualTypeArgument, int index )
	{
		typeWalker.walkActualTypeArgument( actualTypeArgument );
	}

	@Override
	public void visitTypeVariable( TypeVariable< ? > typeVariable )
	{
		typeWalker.walkTypeVariable( typeVariable );
	}

	@Override
	public void visitTypeVariableBounds( Type[] typeVariableBounds )
	{
		typeWalker.walkTypeVariableBounds( typeVariableBounds );
	}

	@Override
	public void visitTypeVariableBoundary( Type typeVariableBoundary, int index )
	{
		typeWalker.walkTypeVariableBoundary( typeVariableBoundary );
	}

	@Override
	public void visitWildcardType( WildcardType wildcardType )
	{
		typeWalker.walkWildcardType( wildcardType );
	}

	@Override
	public void visitWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds )
	{
		typeWalker.walkWildcardTypeUpperBounds( wildcardTypeUpperBounds );
	}

	@Override
	public void visitWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary, int index )
	{
		typeWalker.walkWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
	}

	@Override
	public void visitWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds )
	{
		typeWalker.walkWildcardTypeLowerBounds( wildcardTypeLowerBounds );
	}

	@Override
	public void visitWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary, int index )
	{
		typeWalker.walkWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
	}

	@Override
	public void visitClass( Class< ? > classType )
	{
		typeWalker.walkClass( classType );
	}

	@Override
	public void visitComponentType( Class< ? > componentType )
	{
		typeWalker.walkComponentType( componentType );
	}
}
