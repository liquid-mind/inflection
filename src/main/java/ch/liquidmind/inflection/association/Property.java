package ch.liquidmind.inflection.association;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Property
{
	private PropertyDescriptor targetProperty;
	private Type relatedType;
	private Aggregation aggregation;
	private List< Dimension > dimensions = new ArrayList< Dimension >();
	private Property redefinedProperty, subsettedProperty;
	private boolean isDerived, isDerivedUnion, isDeclared;
	private Class owningClass;

	public Property( PropertyDescriptor targetProperty )
	{
		super();
		this.targetProperty = targetProperty;
	}

	public PropertyDescriptor getTargetProperty()
	{
		return targetProperty;
	}

	void setTargetProperty( PropertyDescriptor targetProperty )
	{
		this.targetProperty = targetProperty;
	}

	public Type getRelatedType()
	{
		return relatedType;
	}

	void setRelatedType( Type relatedType )
	{
		this.relatedType = relatedType;
	}

	public Aggregation getAggregation()
	{
		return aggregation;
	}

	void setAggregation( Aggregation aggregation )
	{
		this.aggregation = aggregation;
	}

	public Property getRedefinedProperty()
	{
		return redefinedProperty;
	}

	void setRedefinedProperty( Property redefinedProperty )
	{
		this.redefinedProperty = redefinedProperty;
	}

	public Property getSubsettedProperty()
	{
		return subsettedProperty;
	}

	void setSubsettedProperty( Property subsettedProperty )
	{
		this.subsettedProperty = subsettedProperty;
	}

	public boolean isDerived()
	{
		return isDerived;
	}

	void setDerived( boolean isDerived )
	{
		this.isDerived = isDerived;
	}

	public boolean isDerivedUnion()
	{
		return isDerivedUnion;
	}

	void setDerivedUnion( boolean isDerivedUnion )
	{
		this.isDerivedUnion = isDerivedUnion;
	}

	public List< Dimension > getDimensions()
	{
		return dimensions;
	}

	void setDimensions( List< Dimension > dimensions )
	{
		this.dimensions = dimensions;
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
	}

	public String getName()
	{
		return targetProperty.getName();
	}
}
