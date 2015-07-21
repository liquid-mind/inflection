package ch.liquidmind.inflection;

public class VisitorsNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public VisitorsNotFoundException()
	{
		super();
	}

	public VisitorsNotFoundException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public VisitorsNotFoundException( String message )
	{
		super( message );
	}
}
