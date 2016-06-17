package ch.liquidmind.inflection.support2;

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
		typeWalker.continueTypes( types );
	}

	@Override
	public void visitType( Type type )
	{
		typeWalker.continueType( type );
	}

	@Override
	public void visitGenericArrayType( GenericArrayType genericArrayType )
	{
		typeWalker.continueGenericArrayType( genericArrayType );
	}

	@Override
	public void visitGenericComponentType( Type genericComponentType )
	{
		typeWalker.continueGenericComponentType( genericComponentType );
	}

	@Override
	public void visitParameterizedType( ParameterizedType parameterizedType )
	{
		typeWalker.continueParameterizedType( parameterizedType );
	}

	@Override
	public void visitActualTypeArguments( Type[] actualTypeArguments )
	{
		typeWalker.continueActualTypeArguments( actualTypeArguments );
	}

	@Override
	public void visitActualTypeArgument( Type actualTypeArgument )
	{
		typeWalker.continueActualTypeArgument( actualTypeArgument );
	}

	@Override
	public void visitTypeVariable( TypeVariable< ? > typeVariable )
	{
		typeWalker.continueTypeVariable( typeVariable );
	}

	@Override
	public void visitTypeVariableBounds( Type[] typeVariableBounds )
	{
		typeWalker.continueTypeVariableBounds( typeVariableBounds );
	}

	@Override
	public void visitTypeVariableBoundary( Type typeVariableBoundary )
	{
		typeWalker.continueTypeVariableBoundary( typeVariableBoundary );
	}

	@Override
	public void visitWildcardType( WildcardType wildcardType )
	{
		typeWalker.continueWildcardType( wildcardType );
	}

	@Override
	public void visitWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds )
	{
		typeWalker.continueWildcardTypeUpperBounds( wildcardTypeUpperBounds );
	}

	@Override
	public void visitWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary )
	{
		typeWalker.continueWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
	}

	@Override
	public void visitWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds )
	{
		typeWalker.continueWildcardTypeLowerBounds( wildcardTypeLowerBounds );
	}

	@Override
	public void visitWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary )
	{
		typeWalker.continueWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
	}

	@Override
	public void visitClass( Class< ? > classType )
	{
		typeWalker.continueClass( classType );
	}

	@Override
	public void visitComponentType( Class< ? > componentType )
	{
		typeWalker.continueComponentType( componentType );
	}
}
