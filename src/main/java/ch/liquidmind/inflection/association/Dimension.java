package ch.liquidmind.inflection.association;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Range;

public class Dimension
{
	private Type targetType;
	private List< Range< Integer > > ranges = new ArrayList< Range< Integer > >();
	private boolean isOrdered, isUnique;

	public Dimension( Type targetType, boolean isOrdered, boolean isUnique )
	{
		super();
		this.targetType = targetType;
		this.isOrdered = isOrdered;
		this.isUnique = isUnique;
	}

	public boolean isOrdered()
	{
		return isOrdered;
	}

	void setOrdered( boolean isOrdered )
	{
		this.isOrdered = isOrdered;
	}

	public boolean isUnique()
	{
		return isUnique;
	}

	void setUnique( boolean isUnique )
	{
		this.isUnique = isUnique;
	}

	public List< Range< Integer > > getRanges()
	{
		return ranges;
	}

	public Type getTargetType()
	{
		return targetType;
	}

	public void setTargetType( Type targetType )
	{
		this.targetType = targetType;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( isOrdered ? 1231 : 1237 );
		result = prime * result + ( isUnique ? 1231 : 1237 );
		result = prime * result + ( ( ranges == null ) ? 0 : ranges.hashCode() );
		result = prime * result + ( ( targetType == null ) ? 0 : targetType.hashCode() );
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
		Dimension other = (Dimension)obj;
		if ( isOrdered != other.isOrdered )
			return false;
		if ( isUnique != other.isUnique )
			return false;
		if ( ranges == null )
		{
			if ( other.ranges != null )
				return false;
		}
		else if ( !ranges.equals( other.ranges ) )
			return false;
		if ( targetType == null )
		{
			if ( other.targetType != null )
				return false;
		}
		else if ( !targetType.equals( other.targetType ) )
			return false;
		return true;
	}
}
