package ch.liquidmind.inflection.test.mock;

/**
 * Simulates an *.java file for unit testing
 **/
public class JavaFileMock extends AbstractFileMock
{

	public JavaFileMock( String content )
	{
		super( content );
	}

	public JavaFileMock( String packageName, String content )
	{
		super( packageName, content );
	}

	public JavaFileMock( String fileName, String packageName, String content )
	{
		super( fileName, packageName, content );
	}

	@Override
	public String getDefaultSuffix()
	{
		return "java";
	}

}
