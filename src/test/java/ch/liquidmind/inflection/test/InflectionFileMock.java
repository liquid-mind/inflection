package ch.liquidmind.inflection.test;

/**
 * Simulates an *.inflect file for unit testing
**/
public class InflectionFileMock
{

	private String fileName;
	private String packageName;
	private String content;

	/**
	 * Simulates an *.inflect file for unit testing
	 * 
	 * @param fileName the file name to be used including *.inflect suffix
	 * @param packageName the target directory location (written as package name)
	 * @param content the file content (including package name)
	 */
	public InflectionFileMock( String fileName, String packageName, String content )
	{
		super();
		if (content == null) {
			throw new IllegalArgumentException("content must not be null");
		}
		this.packageName = packageName;
		this.content = content;
		this.fileName = fileName;
	}	
	
	/**
	 * Simulates an *.inflect file for unit testing
	 * 
	 * @param packageName the target directory location (written as package name)
	 * @param content the file content (including package name)
	 */
	public InflectionFileMock( String packageName, String content )
	{
		this( null, packageName, content );
	}
	
	/**
	 * Simulates an *.inflect file for unit testing in unnamed package
	 * 
	 * @param content the file content (including package name)
	 */
	public InflectionFileMock( String content )
	{
		this( null, null, content );
	}

	public String getPackageName()
	{
		return packageName;
	}

	public String getContent()
	{
		return content;
	}
	
	public String getFileName()
	{
		return fileName;
	}

}
