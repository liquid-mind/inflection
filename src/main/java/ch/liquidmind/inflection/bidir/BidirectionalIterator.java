package ch.liquidmind.inflection.bidir;

import java.util.Iterator;

public class BidirectionalIterator< E extends Object > extends BidirectionalDelegate implements Iterator< E >
{
	public BidirectionalIterator( Object owner, String opposingPropertyName, Object target )
	{
		super( owner, opposingPropertyName, target );
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
