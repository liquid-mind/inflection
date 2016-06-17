package ch.liquidmind.inflection.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings( "unchecked" )
public class Variables
{
	private Map< String, Object > variableMap = new HashMap< String, Object >();

	public < T > T get( String key )
	{
		return (T)variableMap.get( key );
	}

	public < T > T put( String key, T value )
	{
		return (T)variableMap.put( key, value );
	}

	@Override
	public String toString()
	{
		String s = "";
		
		if ( !variableMap.isEmpty() )
		{
			s += "{\n";
			for ( Entry< String, Object > entry : variableMap.entrySet() )
				s += "\t" + entry.getKey() + "=" + entry.getValue() + "\n";
				
			s += "}\n";	
		}
		
		return s;
	}
}
