package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class BidirectionalList< E extends Object > extends BidirectionalCollection< E > implements List< E >
{
	public BidirectionalList( Object owner, Field field, Object target )
	{
		super( owner, field, target );
	}

	@Override
	public List< E > getTarget()
	{
		return (List< E >)super.getTarget();
	}

	@Override
	public boolean addAll( int index, Collection< ? extends E > c )
	{
		for ( Object e : c )
			setOpposing( e, null, getOwner() );

		return getTarget().addAll( index, c );
	}

	@Override
	public E get( int index )
	{
   		return getTarget().get( index );
 	}

	@Override
	public E set( int index, E element )
	{
		setOpposing( get( index ), getOwner(), null );
		setOpposing( element, null, getOwner() );
		
		return getTarget().set( index, element );
	}

	@Override
	public void add( int index, E element )
	{
		setOpposing( element, null, getOwner() );
		
		getTarget().add( index, element );
	}

	@Override
	public E remove( int index )
	{
		setOpposing( get( index ), getOwner(), null );

		return getTarget().remove( index );
	}

	@Override
	public int indexOf( Object o )
	{
		return getTarget().indexOf( o );
	}

	@Override
	public int lastIndexOf( Object o )
	{
		return getTarget().lastIndexOf( o );
	}

	@Override
	public ListIterator< E > listIterator()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator< E > listIterator( int index )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List< E > subList( int fromIndex, int toIndex )
	{
		return getTarget().subList( fromIndex, toIndex );
	}
}
