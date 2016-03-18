package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.Iterator;

public class BidirectionalIterable< E > extends BidirectionalDelegate implements Iterable< E >
{
	public BidirectionalIterable( Object owner, Field field, Object target )
	{
		super( owner, field, target );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Iterable< E > getTarget()
	{
		return (Iterable< E >)super.getTarget();
	}

	@Override
	public Iterator< E > iterator()
	{
		return new BidirectionalIterator< E >( getOwner(), getField(), getTarget() );
	}
}
