package ch.liquidmind.inflection.model.compiled;

import java.io.Serializable;

public class NamedElementCompiled implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String name;

	public NamedElementCompiled( String name )
	{
		super();
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}
}
