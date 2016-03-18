package ch.liquidmind.inflection.bidir2;

import java.lang.reflect.Field;
import java.util.Collection;

public class BidirectionalCollection< E > extends BidirectionalIterable< E > implements Collection< E >
{
	public BidirectionalCollection( Object owner, Field field, Object target )
	{
		super( owner, field, target );
	}

	@Override
	public Collection< E > getTarget()
	{
		return (Collection< E >)super.getTarget();
	}

	@Override
	public int size()
	{
		return getTarget().size();
	}

	@Override
	public boolean isEmpty()
	{
		return getTarget().isEmpty();
	}

	@Override
	public boolean contains( Object o )
	{
		return getTarget().contains( o );
	}

	@Override
	public Object[] toArray()
	{
		return getTarget().toArray();
	}

	@Override
	public < T > T[] toArray( T[] a )
	{
		return getTarget().toArray( a );
	}

	@Override
	public boolean add( E e )
	{
		setOpposing( e, null, getOwner() );
		
		return getTarget().add( e );
	}

	@Override
	public boolean remove( Object o )
	{
		setOpposing( o, getOwner(), null );

		return getTarget().remove( o );
	}

	@Override
	public boolean containsAll( Collection< ? > c )
	{
		return getTarget().contains( c );
	}

	@Override
	public boolean addAll( Collection< ? extends E > c )
	{
		for ( Object e : c )
			setOpposing( e, null, getOwner() );
		
		return getTarget().addAll( c );
	}

	@Override
	public boolean removeAll( Collection< ? > c )
	{
		for ( Object e : c )
			setOpposing( e, getOwner(), null );
		
		return getTarget().removeAll( c );
	}

	@Override
	public boolean retainAll( Collection< ? > c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		for ( Object e : getTarget() )
			setOpposing( e, getOwner(), null );
		
		getTarget().clear();
	}
}
