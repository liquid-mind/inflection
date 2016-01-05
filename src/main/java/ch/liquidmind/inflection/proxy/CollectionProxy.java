package ch.liquidmind.inflection.proxy;

import java.util.Collection;

public class CollectionProxy< E > extends IterableProxy< E > implements Collection< E >
{
	protected CollectionProxy( String taxonomyName )
	{
		super( taxonomyName );
	}

	@Override
	public int size()
	{
		return invokeOnCollection( "size", new Class< ? >[] {}, new Object[] {} );
	}

	@Override
	public boolean isEmpty()
	{
		return invokeOnCollection( "isEmpty", new Class< ? >[] {}, new Object[] {} );
	}

	@Override
	public boolean contains( Object o )
	{
		return invokeOnCollection( "contains", new Class< ? >[] { Object.class }, new Object[] { o } );
	}

	@Override
	public Object[] toArray()
	{
		return invokeOnCollection( "toArray", new Class< ? >[] {}, new Object[] {} );
	}

	@Override
	public < T > T[] toArray( T[] a )
	{
		return invokeOnCollection( "toArray", new Class< ? >[] { Object.class }, new Object[] { a } );
	}

	@Override
	public boolean add( E e )
	{
		return invokeOnCollection( "add", new Class< ? >[] { Object.class }, new Object[] { e } );
	}

	@Override
	public boolean remove( Object o )
	{
		return invokeOnCollection( "remove", new Class< ? >[] { Object.class }, new Object[] { o } );
	}

	@Override
	public boolean containsAll( Collection< ? > c )
	{
		return invokeOnCollection( "containsAll", new Class< ? >[] { Collection.class }, new Object[] { c } );
	}

	@Override
	public boolean addAll( Collection< ? extends E > c )
	{
		return invokeOnCollection( "addAll", new Class< ? >[] { Collection.class }, new Object[] { c } );
	}

	@Override
	public boolean removeAll( Collection< ? > c )
	{
		return invokeOnCollection( "removeAll", new Class< ? >[] { Collection.class }, new Object[] { c } );
	}

	@Override
	public boolean retainAll( Collection< ? > c )
	{
		return invokeOnCollection( "retainAll", new Class< ? >[] { Collection.class }, new Object[] { c } );
	}

	@Override
	public void clear()
	{
		invokeOnCollection( "clear", new Class< ? >[] {}, new Object[] {} );
	}
}
