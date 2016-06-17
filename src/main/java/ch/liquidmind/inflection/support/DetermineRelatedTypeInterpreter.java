package ch.liquidmind.inflection.support;

import java.lang.reflect.ParameterizedType;

public class DetermineRelatedTypeInterpreter extends AbstractTypeInterpreter
{
	@Override
	public void startParameterizedType( ParameterizedType parameterizedType )
	{
		System.out.println( String.format( "rawType: %1$s", parameterizedType.getRawType() ) );
	}

	@Override
	public void startClass( Class< ? > classType )
	{
		System.out.println( String.format( "classType: %1$s", classType.getName() ) );
	}
}
