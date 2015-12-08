package ch.liquidmind.inflection.test;

/**
 * Simulates an *.inflect file for unit testing
**/
public class InflectionFileMock
{

	private String packageName;
	private String content;

	/**
	 * Simulates an *.inflect file for unit testing
	 * 
	 * @param packageName the target directory location (written as package name)
	 * @param content the file content (including package name)
	 */
	public InflectionFileMock( String packageName, String content )
	{
		super();
		if (packageName == null || content == null) {
			throw new IllegalArgumentException("arguments must not be null");
		}
		this.packageName = packageName;
		this.content = content;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName( String packageName )
	{
		this.packageName = packageName;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent( String content )
	{
		this.content = content;
	}

}
