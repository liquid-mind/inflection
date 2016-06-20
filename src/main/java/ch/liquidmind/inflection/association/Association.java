package ch.liquidmind.inflection.association;

public class Association
{
	private String name;
	private Property selfEnd, otherEnd;
	private boolean isDeclared;
	private Class owningClass;

	public Association( String name, Property selfEnd, Property otherEnd, boolean isDeclared )
	{
		super();
		this.name = name;
		this.selfEnd = selfEnd;
		this.otherEnd = otherEnd;
		this.isDeclared = isDeclared;
	}

	public String getName()
	{
		return name;
	}

	void setName( String name )
	{
		this.name = name;
	}

	public Property getSelfEnd()
	{
		return selfEnd;
	}

	void setSelfEnd( Property selfEnd )
	{
		this.selfEnd = selfEnd;
	}

	public Property getOtherEnd()
	{
		return otherEnd;
	}

	void setOtherEnd( Property otherEnd )
	{
		this.otherEnd = otherEnd;
	}

	public boolean isDeclared()
	{
		return isDeclared;
	}

	void setDeclared( boolean isDeclared )
	{
		this.isDeclared = isDeclared;
	}

	public Class getOwningClass()
	{
		return owningClass;
	}

	void setOwningClass( Class owningClass )
	{
		this.owningClass = owningClass;
		owningClass.getOwnedAssociations().add( this );
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( otherEnd == null ) ? 0 : otherEnd.hashCode() );
		result = prime * result + ( ( owningClass == null ) ? 0 : owningClass.hashCode() );
		result = prime * result + ( ( selfEnd == null ) ? 0 : selfEnd.hashCode() );
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
		Association other = (Association)obj;
		if ( otherEnd == null )
		{
			if ( other.otherEnd != null )
				return false;
		}
		else if ( !otherEnd.equals( other.otherEnd ) )
			return false;
		if ( owningClass == null )
		{
			if ( other.owningClass != null )
				return false;
		}
		else if ( !owningClass.equals( other.owningClass ) )
			return false;
		if ( selfEnd == null )
		{
			if ( other.selfEnd != null )
				return false;
		}
		else if ( !selfEnd.equals( other.selfEnd ) )
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		String selfEndName = ( selfEnd == null ? "NA" : selfEnd.getName() );
		String otherEndName = ( otherEnd == null ? "NA" : otherEnd.getName() );
		
		return String.format( "Property [name=%s, selfEnd=%s, otherEnd=%s, declared=%s]",
			getName(), selfEndName, otherEndName, isDeclared );
	}
}
