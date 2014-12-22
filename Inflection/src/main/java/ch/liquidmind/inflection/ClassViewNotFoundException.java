package ch.liquidmind.inflection;

public class ClassViewNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ClassViewNotFoundException()
	{
		super();
	}

	public ClassViewNotFoundException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public ClassViewNotFoundException( String message )
	{
		super( message );
	}
}
