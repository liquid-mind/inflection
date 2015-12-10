package ch.liquidmind.inflection.test.mock;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import __java.nio.file.__Files;

public abstract class AbstractFileMock
{

	private String fileName;
	private String packageName;
	private String content;

	/**
	 * Simulates a file for unit testing
	 * 
	 * @param fileName
	 *            the file name to be used including suffix
	 * @param packageName
	 *            the target directory location (written as package name)
	 * @param content
	 *            the file content (including package name)
	 */
	public AbstractFileMock( String fileName, String packageName, String content )
	{
		super();
		if ( content == null )
		{
			throw new IllegalArgumentException( "content must not be null" );
		}
		this.packageName = packageName;
		this.content = content;
		this.fileName = fileName;
	}

	/**
	 * Simulates a file for unit testing
	 * 
	 * @param packageName
	 *            the target directory location (written as package name)
	 * @param content
	 *            the file content (including package name)
	 */
	public AbstractFileMock( String packageName, String content )
	{
		this( null, packageName, content );
	}

	/**
	 * Simulates a file for unit testing in unnamed package
	 * 
	 * @param content
	 *            the file content (including package name)
	 */
	public AbstractFileMock( String content )
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

	public abstract String getDefaultSuffix();

	public Path writeToFile( Path rootDir )
	{
		Path currentDir = rootDir;
		if ( this.getPackageName() != null )
		{
			String[] parts = this.getPackageName().split( "\\." );
			for ( String part : parts )
			{
				currentDir = new File( currentDir.toFile(), part ).toPath();
				currentDir.toFile().mkdirs();
			}
		}
		String fileName = this.getFileName();
		if ( fileName == null )
		{
			fileName = UUID.randomUUID().toString() + "." + getDefaultSuffix();
		}
		Path file = __Files.write( null, Paths.get( currentDir.toFile().getAbsolutePath(), fileName ), this.getContent().getBytes() );
		return file;
	}

}
