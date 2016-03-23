package ch.liquidmind.inflection.bidir;

import java.util.Iterator;

public class BidirectionalIterable< E > extends BidirectionalDelegate implements Iterable< E >
{
	public BidirectionalIterable( Object owner, String opposingPropertyName, Object target )
	{
		super( owner, opposingPropertyName, target );
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
		return new BidirectionalIterator< E >( getOwner(), getOpposingPropertyName(), getTarget() );
	}
}
