package ch.liquidmind.inflection.test.mock;

/**
 * Simulates an *.inflect file for unit testing
 **/
public class InflectionFileMock extends AbstractFileMock
{

	public InflectionFileMock( String content )
	{
		super( content );
	}

	public InflectionFileMock( String packageName, String content )
	{
		super( packageName, content );
	}

	public InflectionFileMock( String fileName, String packageName, String content )
	{
		super( fileName, packageName, content );
	}

	@Override
	public String getDefaultSuffix()
	{
		return "inflect";
	}

}
