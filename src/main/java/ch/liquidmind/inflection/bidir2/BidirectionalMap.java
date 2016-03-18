package ch.liquidmind.inflection.bidir2;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class BidirectionalMap< K extends Object, V extends Object > extends BidirectionalDelegate implements Map< K, V >
{
	public BidirectionalMap( Object owner, Field field, Object target )
	{
		super( owner, field, target );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Map< K, V > getTarget()
	{
		return (Map< K, V >)super.getTarget();
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
	public boolean containsKey( Object key )
	{
		return getTarget().containsKey( key );
	}

	@Override
	public boolean containsValue( Object value )
	{
		return getTarget().containsValue( value );
	}

	@Override
	public V get( Object key )
	{
		return getTarget().get( key );
	}

	@Override
	public V put( K key, V value )
	{
		setOpposing( getTarget().get( key ), getOwner(), null );
		setOpposing( value, null, getOwner() );
		
		return getTarget().put( key, value );
	}

	@Override
	public V remove( Object key )
	{
		setOpposing( getTarget().get( key ), getOwner(), null );
		
		return getTarget().remove( key );
	}

	@Override
	public void putAll( Map< ? extends K, ? extends V > m )
	{
		for ( Map.Entry< ?, ? > entry : m.entrySet() )
		{
			setOpposing( getTarget().get( entry.getKey() ), getOwner(), null );
			setOpposing( entry.getValue(), null, getOwner() );
		}
		
		getTarget().putAll( m );
	}

	@Override
	public void clear()
	{
		for ( Object value : getTarget().values() )
			setOpposing( value, getOwner(), null );
		
		getTarget().clear();
	}

	@Override
	public Set< K > keySet()
	{
		return getTarget().keySet();
	}

	@Override
	public Collection< V > values()
	{
		return getTarget().values();
	}

	@Override
	public Set< java.util.Map.Entry< K, V > > entrySet()
	{
		return getTarget().entrySet();
	}
}
