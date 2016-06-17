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
}
