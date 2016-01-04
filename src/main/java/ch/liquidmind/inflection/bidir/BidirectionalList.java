package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class BidirectionalList< E > extends BidirectionalCollection< E > implements List< E >
{
	public BidirectionalList( Object owner, Field field, List< E > targetList )
	{
		super( owner, field, targetList );
	}
	
	public List< E > getTargetList()
	{
		return (List< E >)getTargetCollection();
	}

	@Override
	public boolean addAll( int index, Collection< ? extends E > c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public E get( int index )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public E set( int index, E element )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void add( int index, E element )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove( int index )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf( Object o )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int lastIndexOf( Object o )
	{
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}
}
