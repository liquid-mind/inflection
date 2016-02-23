package ch.liquidmind.inflection.test.model;

public class IdentifiableObject
{
	long id;

	public IdentifiableObject()
	{}

	public IdentifiableObject( long id )
	{
		super();
		this.id = id;
	}

	public long getId()
	{
		return id;
	}

	public void setId( long id )
	{
		this.id = id;
	}
}
