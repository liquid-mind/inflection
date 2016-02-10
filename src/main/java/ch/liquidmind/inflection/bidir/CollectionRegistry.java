package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
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
	private Map< Integer, RegisteredCollection > registeredCollections = new HashMap< Integer, RegisteredCollection >();
	
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
		registeredCollections.put( System.identityHashCode( registeredCollection.getCollection() ), registeredCollection );
	}
	
	public synchronized void unregister( Collection< ? > collection )
	{
		registeredCollections.remove( System.identityHashCode( collection ) );
	}
	
	public synchronized RegisteredCollection lookup( Collection< ? > collection )
	{
		return registeredCollections.get( System.identityHashCode( collection ) );
	}
}
