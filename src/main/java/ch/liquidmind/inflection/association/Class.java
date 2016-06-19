package ch.liquidmind.inflection.association;

import java.util.HashSet;
import java.util.Set;

public class Class
{
	private java.lang.Class< ? > targetClass;
	private Set< Property > ownedProperties;
	private Set< Association > ownedAssociations  = new HashSet< Association >();
	private Set< Class > ownedClasses = new HashSet< Class >();
	private Set< Class > subClasses = new HashSet< Class >();
	private Class owningClass, superClass;

	public Class( java.lang.Class< ? > targetClass, Set< Property > ownedProperties )
	{
		super();
		this.targetClass = targetClass;
		this.ownedProperties = ownedProperties;
		
		ownedProperties.stream().forEach( ownedProperty -> ownedProperty.setOwningClass( this ) );
	}

	public java.lang.Class< ? > getTargetClass()
	{
		return targetClass;
	}

	void setTargetClass( java.lang.Class< ? > targetClass )
	{
		this.targetClass = targetClass;
	}

	public Set< Property > getOwnedProperties()
	{
		return ownedProperties;
	}

	public Set< Association > getOwnedAssociations()
	{
		return ownedAssociations;
	}

	public Class getOwningClass()
	{
		return owningClass;
	}

	void setOwningClass( Class owningClass )
	{
		this.owningClass = owningClass;
	}

	public Set< Class > getOwnedClasses()
	{
		return ownedClasses;
	}

	public Class getSuperClass()
	{
		return superClass;
	}

	void setSuperClass( Class superClass )
	{
		this.superClass = superClass;
	}

	public Set< Class > getSubClasses()
	{
		return subClasses;
	}
	
	public String getName()
	{
		return targetClass.getName();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( targetClass == null ) ? 0 : targetClass.hashCode() );
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
		Class other = (Class)obj;
		if ( targetClass == null )
		{
			if ( other.targetClass != null )
				return false;
		}
		else if ( !targetClass.equals( other.targetClass ) )
			return false;
		return true;
	}
}
