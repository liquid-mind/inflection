package ch.liquidmind.inflection;

public class IdentifiableObject< ObjectIdType, ObjectType >
{
	private ObjectIdType objectId;
	private ObjectType object;
	
	public IdentifiableObject( ObjectIdType objectId, ObjectType object )
	{
		super();
		this.objectId = objectId;
		this.object = object;
	}

	public ObjectIdType getObjectId()
	{
		return objectId;
	}

	public void setObjectId( ObjectIdType objectId )
	{
		this.objectId = objectId;
	}

	public ObjectType getObject()
	{
		return object;
	}

	public void setObject( ObjectType object )
	{
		this.object = object;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( objectId == null ) ? 0 : objectId.hashCode() );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		IdentifiableObject< ?, ? > other = (IdentifiableObject< ?, ? >)obj;
		if ( objectId == null )
		{
			if ( other.objectId != null )
				return false;
		}
		else if ( !objectId.equals( other.objectId ) )
			return false;
		return true;
	}
}
