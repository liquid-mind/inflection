package ch.liquidmind.inflection.model;

public class NoSuchPropertyException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NoSuchPropertyException( Class< ? > theClass, String memberName )
	{
		super( "Class " );
	}
}
