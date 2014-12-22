package ch.zhaw.inflection;

public class ClassViewFormatError extends Error
{
	private static final long serialVersionUID = 1L;

	public ClassViewFormatError()
	{
		super();
	}

	public ClassViewFormatError( String message, Throwable cause )
	{
		super( message, cause );
	}

	public ClassViewFormatError( String message )
	{
		super( message );
	}
}
