package ch.zhaw.inflection.operation.extended;


public class ValidationError
{
	private String location;
	private String errorMsg;
	
	public ValidationError( String location, String errorMsg )
	{
		super();
		this.location = location;
		this.errorMsg = errorMsg;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation( String location )
	{
		this.location = location;
	}

	public String getErrorMsg()
	{
		return errorMsg;
	}

	public void setErrorMsg( String errorMsg )
	{
		this.errorMsg = errorMsg;
	}
}
