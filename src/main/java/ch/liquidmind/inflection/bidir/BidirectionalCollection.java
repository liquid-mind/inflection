package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BidirectionalCollection< E > extends BidirectionalIterable< E > implements Collection< E >
{
	public BidirectionalCollection( Object owner, Field field, Collection< E > targetCollection )
	{
		super( owner, field, targetCollection );
	}

	public Collection< E > getTargetCollection()
	{
		return (Collection< E >)getTargetIterable();
	}

	@Override
	public int size()
	{
		return getTargetCollection().size();
	}

	@Override
	public boolean isEmpty()
	{
		return getTargetCollection().isEmpty();
	}

	@Override
	public boolean contains( Object o )
	{
		return getTargetCollection().contains( o );
	}

	@Override
	public Iterator< E > iterator()
	{
		return getTargetCollection().iterator();
	}

	@Override
	public Object[] toArray()
	{
		return getTargetCollection().toArray();
	}

	@Override
	public < T > T[] toArray( T[] a )
	{
		return getTargetCollection().toArray( a );
	}

	@Override
	public boolean add( E objectB )
	{
		return handleAdd( getOwner(), getField(), objectB );
	}

	@Override
	public boolean remove( Object objectB )
	{
		return handleRemove( getOwner(), getField(), objectB );
	}

	@Override
	public boolean containsAll( Collection< ? > c )
	{
		return getTargetCollection().containsAll( c );
	}

	@Override
	public boolean addAll( Collection< ? extends E > objectsB )
	{
		Set< Boolean > wasChanged = new HashSet< Boolean >();
		
		for ( E objectB : objectsB )
			wasChanged.add( add( objectB ) );
		
		return wasChanged.contains( true );
	}

	@Override
	public boolean removeAll( Collection< ? > objectsB )
	{
		Set< Boolean > wasChanged = new HashSet< Boolean >();
		
		// Necessary to guard against cases in which this collection == objectsB.
		List< ? > copiedObjectsB = new ArrayList< Object >( objectsB );
		
		for ( Object objectB : copiedObjectsB )
			wasChanged.add( remove( objectB ) );
		
		return wasChanged.contains( true );
	}

	@Override
	public boolean retainAll( Collection< ? > objectsB )
	{
		List< Object > removableObjects = new ArrayList< Object >();
		
		

		for ( Object objectB : getTargetCollection() )
			if ( !objectsB.contains( objectB ) )
				removableObjects.add( objectB );
		
		removeAll( removableObjects );

		return !removableObjects.isEmpty();
	}

	@Override
	public void clear()
	{
		removeAll( getTargetCollection() );
	}
	
	public boolean handleAdd( Object objectA, Field fieldA, Object objectB )
	{
		Set< Link > removableLinks = getRemovableLinks( objectA, fieldA, objectB );
		Set< Link > addableLinks = ( objectB == null ? new HashSet< Link >() : Link.getLinks( objectA, fieldA, objectB ) );
		
		int sizeBefore = getTargetCollection().size();
		Link.removeLinks( removableLinks );
		Link.addLinks( addableLinks );
		int sizeAfter = getTargetCollection().size();
		
		return sizeBefore != sizeAfter;
	}
	
	private static Set< Link > getRemovableLinks( Object objectA, Field fieldA, Object objectB )
	{
		Class< ? > classB = BidirectionalAspect.getOpposingClass( fieldA );
		Field fieldB = BidirectionalAspect.getOpposingField( fieldA, classB );
		Set< Link > removableLinks = new HashSet< Link >();
		
		// All links from all new objectB's current state are removable IF the multiplicity of ClassA from ClassB is one (1).
		if ( !Collection.class.isAssignableFrom( fieldB.getType() ) )
			removableLinks.addAll( Link.getLinks( objectB, fieldB ) );

		return removableLinks;
	}

	public boolean handleRemove( Object objectA, Field fieldA, Object objectB )
	{
		Set< Link > removableLinks = ( objectB == null ? new HashSet< Link >() : Link.getLinks( objectA, fieldA, objectB ) );
		
		int sizeBefore = getTargetCollection().size();
		Link.removeLinks( removableLinks );
		int sizeAfter = getTargetCollection().size();
		
		return sizeBefore != sizeAfter;
	}
}
