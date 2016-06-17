package ch.liquidmind.inflection.support;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public interface TypeInterpreter
{
	public void push();
	public void pop();
	
	public void startTypes( List< Type > types );
	public void endTypes( List< Type > types );
	public void startType( Type type );
	public void endType( Type type );
	public void startGenericArrayType( GenericArrayType genericArrayType );
	public void endGenericArrayType( GenericArrayType genericArrayType );
	public void startGenericComponentType( Type genericComponentType );
	public void endGenericComponentType( Type genericComponentType );
	public void startParameterizedType( ParameterizedType parameterizedType );
	public void endParameterizedType( ParameterizedType parameterizedType );
	public void startActualTypeArguments( Type[] actualTypeArguments );
	public void endActualTypeArguments( Type[] actualTypeArguments );
	public void startActualTypeArgument( Type actualTypeArgument );
	public void endActualTypeArgument( Type actualTypeArgument );
	public void startTypeVariable( TypeVariable< ? > typeVariable );
	public void endTypeVariable( TypeVariable< ? > typeVariable );
	public void startTypeVariableBounds( Type[] typeVariableBounds );
	public void endTypeVariableBounds( Type[] typeVariableBounds );
	public void startTypeVariableBoundary( Type typeVariableBoundary );
	public void endTypeVariableBoundary( Type typeVariableBoundary );
	public void startWildcardType( WildcardType wildcardType );
	public void endWildcardType( WildcardType wildcardType );
	public void startWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds );
	public void endWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds );
	public void startWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary );
	public void endWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary );
	public void startWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds );
	public void endWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds );
	public void startWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary );
	public void endWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary );
	public void startClass( Class< ? > classType );
	public void endClass( Class< ? > classType );
	public void startComponentType( Class< ? > componentType );
	public void endComponentType( Class< ? > componentType );
}
