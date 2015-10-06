package ch.liquidmind.inflection2.model.compiled;

import java.io.Serializable;

public class NameSelector implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String value;

	public NameSelector( String value )
	{
		super();
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue( String value )
	{
		this.value = value;
	}
}
