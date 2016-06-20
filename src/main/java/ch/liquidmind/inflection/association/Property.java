package ch.liquidmind.inflection.association;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Property
{
	private PropertyDescriptor targetProperty;
	private Type relatedType;
	private Aggregation aggregation;
	private List< Dimension > dimensions = new ArrayList< Dimension >();
	private Property redefinedProperty, subsettedProperty, redefiningProperty;
	private Set< Property > subsetttingProperties = new HashSet< Property >();
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
		
		if ( redefinedProperty != null)
			redefinedProperty.setRedefiningProperty( this );
	}

	public Property getSubsettedProperty()
	{
		return subsettedProperty;
	}

	void setSubsettedProperty( Property subsettedProperty )
	{
		this.subsettedProperty = subsettedProperty;
		
		if ( subsettedProperty != null )
			subsettedProperty.getSubsetttingProperties().add( this );
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

	public Property getRedefiningProperty()
	{
		return redefiningProperty;
	}

	public void setRedefiningProperty( Property redefiningProperty )
	{
		this.redefiningProperty = redefiningProperty;
	}

	public Set< Property > getSubsetttingProperties()
	{
		return subsetttingProperties;
	}
	
	public java.lang.Class< ? > getRelatedClass()
	{
		java.lang.Class< ? > relatedClass;
		
		if ( relatedType instanceof ParameterizedType )
			relatedClass = (java.lang.Class< ? >)((ParameterizedType)relatedType).getRawType();
		else if ( relatedType instanceof java.lang.Class )
			relatedClass = (java.lang.Class< ? >)relatedType;
		else
			throw new IllegalStateException( "Unexpected type for relatedType: " + relatedType.getTypeName() );
		
		return relatedClass;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( targetProperty == null ) ? 0 : targetProperty.hashCode() );
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
		Property other = (Property)obj;
		if ( targetProperty == null )
		{
			if ( other.targetProperty != null )
				return false;
		}
		else if ( !targetProperty.equals( other.targetProperty ) )
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		String redefines = ( getRedefinedProperty() == null ? "NA" : getRedefinedProperty().getName() );
		String subsets = ( getSubsettedProperty() == null ? "NA" : getSubsettedProperty().getName() );
		
		return String.format( "Property [name=%s, owningClass=%s, relatedType=%s, aggregation=%s, redefines=%s, subsets=%s, derived=%s, derivedUnion=%s, declared=%s]",
			getName(), owningClass.getName(), relatedType.getTypeName(), aggregation, redefines, subsets, isDerived, isDerivedUnion, isDeclared );
	}
}
