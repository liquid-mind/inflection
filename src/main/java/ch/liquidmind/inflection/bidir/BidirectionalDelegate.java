package ch.liquidmind.inflection.bidir;

import ch.liquidmind.inflection.exception.ExceptionWrapper;

public abstract class BidirectionalDelegate
{
	private Object owner;
	private String opposingPropertyName;
	private Object target;

	public BidirectionalDelegate( Object owner, String opposingPropertyName, Object target )
	{
		super();
		this.owner = owner;
		this.opposingPropertyName = opposingPropertyName;
		this.target = target;
	}

	public Object getOwner()
	{
		return owner;
	}

	public String getOpposingPropertyName()
	{
		return opposingPropertyName;
	}

	public Object getTarget()
	{
		return target;
	}

	public void setOpposing( Object opposingOwner, Object oldValue, Object newValue )
	{
		if ( opposingOwner == null)
			return;
		
		// TODO: remove opposingOwner from oldValue in case where oldValue != null.
		
		ExceptionWrapper.PropertyUtils_setProperty( opposingOwner, opposingPropertyName, newValue );
	}
}
