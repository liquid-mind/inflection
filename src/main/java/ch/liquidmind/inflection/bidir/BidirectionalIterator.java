package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.Iterator;

public class BidirectionalIterator< E extends Object > extends BidirectionalDelegate implements Iterator< E >
{
	public BidirectionalIterator( Object owner, Field field, Object target )
	{
		super( owner, field, target );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Iterator< E > getTarget()
	{
		return (Iterator< E >)super.getTarget();
	}

	@Override
	public boolean hasNext()
	{
   		return getTarget().hasNext();
	}

	@Override
	public E next()
	{
   		return getTarget().next();
	}
}
