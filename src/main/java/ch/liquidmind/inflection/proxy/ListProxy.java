package ch.liquidmind.inflection.proxy;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

// TODO: rename to ProxyList (analogous to ArrayList, LinkedList, etc.)
public class ListProxy< E extends Object > extends CollectionProxy< E > implements List< E >
{
	protected ListProxy( String taxonomyName )
	{
		super( taxonomyName );
	}

	@Override
	public boolean addAll( int index, Collection< ? extends E > c )
	{
		return invokeOnCollection( "addAll", new Class< ? >[] { int.class, Collection.class }, new Object[] { index, c } );
	}

	@Override
	public E get( int index )
	{
   		return invokeOnCollection( "get", new Class< ? >[] { int.class }, new Object[] { index } );
 	}

	@Override
	public E set( int index, E element )
	{
		return invokeOnCollection( "set", new Class< ? >[] { int.class, Object.class }, new Object[] { index, element } );
	}

	@Override
	public void add( int index, E element )
	{
		invokeOnCollection( "add", new Class< ? >[] { int.class, Object.class }, new Object[] { index, element } );
	}

	@Override
	public E remove( int index )
	{
		return invokeOnCollection( "remove", new Class< ? >[] { int.class }, new Object[] { index } );
	}

	@Override
	public int indexOf( Object o )
	{
		return invokeOnCollection( "indexOf", new Class< ? >[] { Object.class }, new Object[] { o } );
	}

	@Override
	public int lastIndexOf( Object o )
	{
		return invokeOnCollection( "lastIndexOf", new Class< ? >[] { Object.class }, new Object[] { o } );
	}

	@Override
	public ListIterator< E > listIterator()
	{
		return invokeOnCollection( "listIterator", new Class< ? >[] {}, new Object[] {} );
	}

	@Override
	public ListIterator< E > listIterator( int index )
	{
		return invokeOnCollection( "listIterator", new Class< ? >[] { int.class }, new Object[] { index } );
	}

	@Override
	public List< E > subList( int fromIndex, int toIndex )
	{
		return invokeOnCollection( "subList", new Class< ? >[] { int.class, int.class }, new Object[] { fromIndex, toIndex } );
	}
}
