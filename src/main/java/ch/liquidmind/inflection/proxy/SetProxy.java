package ch.liquidmind.inflection.proxy;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class SetProxy< E extends Object > extends Proxy implements Set< E >
{
	protected SetProxy( String taxonomyName )
	{
		super( taxonomyName );
	}

	@Override
	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains( Object o )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator< E > iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public < T > T[] toArray( T[] a )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add( E e )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove( Object o )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll( Collection< ? > c )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll( Collection< ? extends E > c )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll( Collection< ? > c )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll( Collection< ? > c )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
		
	}
}
