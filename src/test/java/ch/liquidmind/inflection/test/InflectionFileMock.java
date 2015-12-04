package ch.liquidmind.inflection.test;

public class InflectionFileMock
{

	private String packageName;
	private String content;

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
