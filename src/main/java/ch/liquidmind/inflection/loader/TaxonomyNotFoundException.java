package ch.liquidmind.inflection.loader;

public class TaxonomyNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public TaxonomyNotFoundException()
	{
		super();
	}

	public TaxonomyNotFoundException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public TaxonomyNotFoundException( String message )
	{
		super( message );
	}
}
