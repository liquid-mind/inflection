package ch.liquidmind.inflection.support;



public class Frame
{
	public static final String DEFAULT_RETURN_PARAMETER_NAME = "default_return";
	
	private Variables local = new Variables();
	private Variables paramsSent = new Variables();
	private Variables paramsReceived = new Variables();
	private Variables returnsSent = new Variables();
	private Variables returnsReceived = new Variables();
	
	public Variables getParamsSent()
	{
		return paramsSent;
	}
	
	public void setParamsReceived( Variables paramsReceived )
	{
		this.paramsReceived = paramsReceived;
	}
	
	public Variables getReturnsSent()
	{
		return returnsSent;
	}
	
	public void setReturnsReceived( Variables returnsReceived )
	{
		this.returnsReceived = returnsReceived;
	}
	
	public < T > T getLocal( String name )
	{
		return local.get( name );
	}

	public < T > void setLocal( String name, T value )
	{
		local.put( name, value );
	}
	
	public < T > T getParam( String name )
	{
		return paramsReceived.get( name );
	}
	
	public < T > void setParam( String name, T value )
	{
		paramsSent.put( name, value );
	}
	
	public < T > T getReturn( String name )
	{
		return returnsReceived.get( name );
	}
	
	public < T > void setReturn( String name, T value )
	{
		returnsSent.put( name, value );
	}
	
	public < T > T getReturn()
	{
		return returnsReceived.get( DEFAULT_RETURN_PARAMETER_NAME );
	}
	
	public < T > void setReturn( T value)
	{
		returnsSent.put( DEFAULT_RETURN_PARAMETER_NAME, value );
	}

	@Override
	public String toString()
	{
		String s = "";
			
		s += variablesToString( "local", local );
		s += variablesToString( "paramsSent", paramsSent );
		s += variablesToString( "paramsReceived", paramsReceived );
		s += variablesToString( "returnsSent", returnsSent );
		s += variablesToString( "returnsReceived", returnsReceived );
		
		if ( !s.isEmpty() )
			s = "{\n" + indentString( s ) + "}\n";
		
		return s;
	}
	
	private String variablesToString( String name, Variables variables )
	{
		String s = variables.toString();
		
		if ( !s.isEmpty() )
			s = name + "\n" + s;
		
		return s;
	}
	
	// TODO: put this method somewhere more appropriate.
	public static String indentString( String s )
	{
		return s.replaceAll("(?m)^", "\t");
	}
}
