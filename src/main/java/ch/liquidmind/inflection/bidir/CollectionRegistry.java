package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionRegistry
{
	public static class RegisteredCollection
	{
		private Object owner;
		private Collection< ? > collection;
		private Field field;
		
		public RegisteredCollection( Object owner, Collection< ? > collection, Field field )
		{
			super();
			this.owner = owner;
			this.collection = collection;
			this.field = field;
		}

		public Object getOwner()
		{
			return owner;
		}

		public void setOwner( Object owner )
		{
			this.owner = owner;
		}

		public Collection< ? > getCollection()
		{
			return collection;
		}

		public void setCollection( Collection< ? > collection )
		{
			this.collection = collection;
		}

		public Field getField()
		{
			return field;
		}

		public void setField( Field field )
		{
			this.field = field;
		}
	}
	
	private static CollectionRegistry collectionRegistry;
	private Map< Integer, List< RegisteredCollection > > registeredCollections = new HashMap< Integer, List< RegisteredCollection > >();
	
	// In general, most collections will be managed by single threads for the entirety of
	// their life cycle, however there may be cases in which, e.g., a collection is set
	// by one thread and then unset by another; these synchronized methods are necessary to
	// guard against such cases. Unfortunately, this incurs the usual costs associated with
	// thread contingency, however I suspect that it would be possible to introduce optimizations
	// if this ever became a serious issue. Finally, I should note that I am assuming that this
	// object will be created exactly once (associated with a single class loader) but have
	// not built in any guarantees in this regard.
	public synchronized static CollectionRegistry getRegistry()
	{
		if ( collectionRegistry == null )
			collectionRegistry = new CollectionRegistry();
		
		return collectionRegistry;
	}
	
	public synchronized void register( RegisteredCollection registeredCollection )
	{
		int hashcode = System.identityHashCode( registeredCollection.getCollection() );
		List< RegisteredCollection > registeredCollectionsByHashcode = registeredCollections.get( hashcode );
		
		if ( registeredCollectionsByHashcode == null )
		{
			registeredCollectionsByHashcode = new ArrayList< RegisteredCollection >();
			registeredCollections.put( hashcode, registeredCollectionsByHashcode );
		}
		
		registeredCollectionsByHashcode.add( registeredCollection );
	}
	
	public synchronized void unregister( Collection< ? > collection )
	{
		List< RegisteredCollection > registeredCollectionsByHashcode = registeredCollections.get( System.identityHashCode( collection ) );
		
		if ( registeredCollectionsByHashcode != null )
		{
			for ( int i = 0 ; i < registeredCollectionsByHashcode.size() ; ++i )
			{
				if ( registeredCollectionsByHashcode.get( i ).getCollection() == collection )
				{
					registeredCollectionsByHashcode.remove( i );
					
					if ( registeredCollectionsByHashcode.isEmpty() )
						registeredCollections.remove( System.identityHashCode( collection ) );
					
					break;
				}
			}
		}
	}
	
	public synchronized RegisteredCollection lookup( Collection< ? > collection )
	{
		RegisteredCollection foundRegisteredCollection = null;
		
		List< RegisteredCollection > registeredCollectionsByHashcode = registeredCollections.get( System.identityHashCode( collection ) );
		
		if ( registeredCollectionsByHashcode != null )
		{
			for ( RegisteredCollection registeredCollectionByHashcode : registeredCollectionsByHashcode )
			{
				if ( registeredCollectionByHashcode.getCollection() == collection )
				{
					foundRegisteredCollection = registeredCollectionByHashcode;
					break;
				}
			}
		}

		return foundRegisteredCollection;
	}
}
