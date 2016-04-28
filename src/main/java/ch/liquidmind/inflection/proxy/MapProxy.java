package ch.liquidmind.inflection.proxy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapProxy< K extends Object, V extends Object > extends Proxy implements Map< K, V >
{
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
	public boolean containsKey( Object key )
	{
		return invokeOnCollection( "containsKey", new Class< ? >[] { Object.class }, new Object[] { key } );
	}

	@Override
	public boolean containsValue( Object value )
	{
		return invokeOnCollection( "containsValue", new Class< ? >[] { Object.class }, new Object[] { value } );
	}

	@Override
	public V get( Object key )
	{
		return invokeOnCollection( "get", new Class< ? >[] { Object.class }, new Object[] { key } );
	}

	@Override
	public V put( K key, V value )
	{
		return invokeOnCollection( "put", new Class< ? >[] { Object.class, Object.class }, new Object[] { key, value } );
	}

	@Override
	public V remove( Object key )
	{
		return invokeOnCollection( "remove", new Class< ? >[] { Object.class }, new Object[] { key } );
	}

	@Override
	public void putAll( Map< ? extends K, ? extends V > m )
	{
		invokeOnCollection( "putAll", new Class< ? >[] { Object.class }, new Object[] { m } );
	}

	@Override
	public void clear()
	{
		invokeOnCollection( "clear", new Class< ? >[] {}, new Object[] {} );
	}

	@Override
	public Set< K > keySet()
	{
		return invokeOnCollection( "keySet", new Class< ? >[] {}, new Object[] {} );
	}

	@Override
	public Collection< V > values()
	{
		return invokeOnCollection( "values", new Class< ? >[] {}, new Object[] {} );
	}

	@Override
	public Set< java.util.Map.Entry< K, V > > entrySet()
	{
		return invokeOnCollection( "entrySet", new Class< ? >[] {}, new Object[] {} );
	}
}
