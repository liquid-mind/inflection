package ch.liquidmind.inflection.support;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public interface TypeVisitor
{
	public void setTypeWalker( TypeWalker typeWalker );
	public TypeWalker getTypeWalker();
	public void visitTypes( List< Type > types );
	public void visitType( Type type );
	public void visitGenericArrayType( GenericArrayType genericArrayType );
	public void visitGenericComponentType( Type genericComponentType );
	public void visitParameterizedType( ParameterizedType parameterizedType );
	public void visitRawType( Type type );
	public void visitActualTypeArguments( Type[] actualTypeArguments );
	public void visitActualTypeArgument( Type actualTypeArgument, int index );
	public void visitTypeVariable( TypeVariable< ? > typeVariable );
	public void visitTypeVariableBounds( Type[] typeVariableBounds );
	public void visitTypeVariableBoundary( Type typeVariableBoundary, int index );
	public void visitWildcardType( WildcardType wildcardType );
	public void visitWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds );
	public void visitWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary, int index );
	public void visitWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds );
	public void visitWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary, int index );
	public void visitClass( Class< ? > classType );
	public void visitComponentType( Class< ? > componentType );
}
