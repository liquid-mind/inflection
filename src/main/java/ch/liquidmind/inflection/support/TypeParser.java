package ch.liquidmind.inflection.support;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public class TypeParser
{
	private TypeInterpreter interpreter;
	
	public TypeParser( TypeInterpreter interpreter )
	{
		super();
		this.interpreter = interpreter;
	}

	public void parseTypes( List< Type > types )
	{
		interpreter.push();
		interpreter.startTypes( types );
		
		for ( Type type : types )
			parseType( type );

		interpreter.endTypes( types );
		interpreter.pop();
	}

	public void parseType( Type type )
	{
		interpreter.push();
		interpreter.startType( type );
		
		if ( type instanceof GenericArrayType )
			parseGenericArrayType( (GenericArrayType)type );
		else if ( type instanceof ParameterizedType )
			parseParameterizedType( (ParameterizedType)type );
		else if ( type instanceof TypeVariable )
			parseTypeVariable( (TypeVariable< ? >)type );
		else if ( type instanceof WildcardType )
			parseWildcardType( (WildcardType)type );
		else if ( type instanceof Class )
			parseClass( (Class< ? >)type );
		else
			throw new IllegalStateException( "Unexpected type for type: " + type );
		
		interpreter.endType( type );
		interpreter.pop();
	}
	
	public void parseGenericArrayType( GenericArrayType genericArrayType )
	{
		interpreter.push();
		interpreter.startGenericArrayType( genericArrayType );
		
		Type genericComponentType = genericArrayType.getGenericComponentType();
		
		if ( genericComponentType != null )
			parseGenericComponentType( genericComponentType );
		
		parseType( genericArrayType.getGenericComponentType() );
		interpreter.endGenericArrayType( genericArrayType );
		interpreter.pop();
	}

	public void parseGenericComponentType( Type genericComponentType )
	{
		interpreter.push();
		interpreter.startGenericComponentType( genericComponentType );
		parseType( genericComponentType );
		interpreter.endGenericComponentType( genericComponentType );
		interpreter.pop();
	}
	
	public void parseParameterizedType( ParameterizedType parameterizedType )
	{
		interpreter.push();
		interpreter.startParameterizedType( parameterizedType );
		
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		
		if ( actualTypeArguments.length > 0 )
			parseActualTypeArguments( actualTypeArguments );
		
		interpreter.endParameterizedType( parameterizedType );
		interpreter.pop();
	}

	public void parseActualTypeArguments( Type[] actualTypeArguments )
	{
		interpreter.push();
		interpreter.startActualTypeArguments( actualTypeArguments );
		
		for ( Type actualTypeArgument : actualTypeArguments )
			parseActualTypeArgument( actualTypeArgument );
		
		interpreter.endActualTypeArguments( actualTypeArguments );
		interpreter.pop();
	}
	
	public void parseActualTypeArgument( Type actualTypeArgument )
	{
		interpreter.push();
		interpreter.startActualTypeArgument( actualTypeArgument );
		parseType( actualTypeArgument );
		interpreter.endActualTypeArgument( actualTypeArgument );
		interpreter.pop();
	}
	
	public void parseTypeVariable( TypeVariable< ? > typeVariable )
	{
		interpreter.push();
		interpreter.startTypeVariable( typeVariable );
		
		Type[] typeVariableBounds = typeVariable.getBounds();
		
		if ( typeVariableBounds.length > 0 )
			parseTypeVariableBounds( typeVariable.getBounds() );
		
		interpreter.endTypeVariable( typeVariable );
		interpreter.pop();
	}
	
	public void parseTypeVariableBounds( Type[] typeVariableBounds )
	{
		interpreter.push();
		interpreter.startTypeVariableBounds( typeVariableBounds );
		
		for ( Type typeVariableBoundary : typeVariableBounds )
			parseTypeVariableBoundary( typeVariableBoundary );
		
		interpreter.endTypeVariableBounds( typeVariableBounds );
		interpreter.pop();
	}
	
	public void parseTypeVariableBoundary( Type typeVariableBoundary )
	{
		interpreter.push();
		interpreter.startTypeVariableBoundary( typeVariableBoundary );
		parseType( typeVariableBoundary );
		interpreter.endTypeVariableBoundary( typeVariableBoundary );
		interpreter.pop();
	}
	
	public void parseWildcardType( WildcardType wildcardType )
	{
		interpreter.push();
		interpreter.startWildcardType( wildcardType );
		
		Type[] wildcardTypeUpperBounds = wildcardType.getUpperBounds();
		Type[] wildcardTypeLowerBounds = wildcardType.getLowerBounds();
		
		if ( wildcardTypeUpperBounds.length > 0 )
			parseWildcardTypeUpperBounds( wildcardTypeUpperBounds );
		
		if ( wildcardTypeLowerBounds.length > 0 )
			parseWildcardTypeLowerBounds( wildcardTypeLowerBounds );
		
		if ( wildcardTypeUpperBounds.length == 0 && wildcardTypeLowerBounds.length == 0 )
			throw new IllegalStateException( "Upper and lower bounds are both empty." );

		interpreter.endWildcardType( wildcardType );
		interpreter.pop();
	}

	public void parseWildcardTypeUpperBounds( Type[] wildcardTypeUpperBounds )
	{
		interpreter.push();
		interpreter.startWildcardTypeUpperBounds( wildcardTypeUpperBounds );
		
		for ( Type wildcardTypeUpperBoundary : wildcardTypeUpperBounds )
			parseWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
		
		interpreter.endWildcardTypeUpperBounds( wildcardTypeUpperBounds );
		interpreter.pop();
	}
	
	public void parseWildcardTypeUpperBoundary( Type wildcardTypeUpperBoundary )
	{
		interpreter.push();
		interpreter.startWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
		parseType( wildcardTypeUpperBoundary );
		interpreter.endWildcardTypeUpperBoundary( wildcardTypeUpperBoundary );
		interpreter.pop();
	}

	public void parseWildcardTypeLowerBounds( Type[] wildcardTypeLowerBounds )
	{
		interpreter.push();
		interpreter.startWildcardTypeLowerBounds( wildcardTypeLowerBounds );
		
		for ( Type wildcardTypeLowerBoundary : wildcardTypeLowerBounds )
			parseWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
		
		interpreter.endWildcardTypeLowerBounds( wildcardTypeLowerBounds );
		interpreter.pop();
	}
	
	public void parseWildcardTypeLowerBoundary( Type wildcardTypeLowerBoundary )
	{
		interpreter.push();
		interpreter.startWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
		parseType( wildcardTypeLowerBoundary );
		interpreter.endWildcardTypeLowerBoundary( wildcardTypeLowerBoundary );
		interpreter.pop();
	}
	
	public void parseClass( Class< ? > classType )
	{
		interpreter.push();
		interpreter.startClass( classType );
		
		Class< ? > componentType = classType.getComponentType();
		
		if ( componentType != null )
			parseComponentType( componentType );
		
		interpreter.endClass( classType );
		interpreter.pop();
	}

	public void parseComponentType( Class< ? > componentType )
	{
		interpreter.push();
		interpreter.startComponentType( componentType );
		parseType( componentType );
		interpreter.endComponentType( componentType );
		interpreter.pop();
	}
}
