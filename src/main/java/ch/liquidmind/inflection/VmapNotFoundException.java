package ch.liquidmind.inflection;

public class VmapNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public VmapNotFoundException()
	{
		super();
	}

	public VmapNotFoundException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public VmapNotFoundException( String message )
	{
		super( message );
	}
}
