package ch.liquidmind.inflection.loader;

public class TaxonomyFormatError extends Error
{
	private static final long serialVersionUID = 1L;

	public TaxonomyFormatError()
	{
		super();
	}

	public TaxonomyFormatError( String message, Throwable cause )
	{
		super( message, cause );
	}

	public TaxonomyFormatError( String message )
	{
		super( message );
	}
}
