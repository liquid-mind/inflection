package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.Collection;

import __java.lang.reflect.__Field;

public abstract class BidirectionalDelegate
{
	private Object owner;
	private Field field;
	private Object target;

	public BidirectionalDelegate( Object owner, Field field, Object target )
	{
		super();
		this.owner = owner;
		this.field = field;
		this.target = target;
	}

	public Object getOwner()
	{
		return owner;
	}

	public Field getField()
	{
		return field;
	}

	public Object getTarget()
	{
		return target;
	}

	@SuppressWarnings( "unchecked" )
	public void setOpposing( Object target, Object oldValue, Object newValue )
	{
		if ( target == null)
			return;
		
		Object currentValue = __Field.get( field, target );
		
		if ( currentValue instanceof Collection )
		{
			Collection< Object > collection = (Collection< Object >)currentValue;

			if ( newValue == null )
				collection.remove( oldValue );
			else
				collection.add( newValue );
		}
		else
		{
			__Field.set( field, target, newValue );
		}
	}
}
