package ch.liquidmind.inflection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * The rationale for hashcodeToIdentifiableObjectSetMap is that it allows us to avoid applying
 * the == operator to every object in the pool. Instead, we only use == to compare objects that
 * share the same hash code (which is rare, but not impossible).
 */
public class IdentifiableObjectPool
{
	public static final ObjectIDProvider< ? > DEFAULT_OBJECT_ID_PROVIDER = new SequentialObjectIDProvider();
	private static ThreadLocal< IdentifiableObjectPool > identifiableObjectPool = new ThreadLocal< IdentifiableObjectPool >();
	
	private Map< Integer, Set< IdentifiableObject< ?, ? > > > hashcodeToIdentifiableObjectSetMap = new HashMap< Integer, Set< IdentifiableObject< ?, ? > > >();
	private Map< Object, Object > objectIdToObjectMap = new HashMap< Object, Object >();
	private ObjectIDProvider< ? > objectIdProvider;

	private IdentifiableObjectPool()
	{
		super();
		this.objectIdProvider = DEFAULT_OBJECT_ID_PROVIDER;
	}
	
	public static IdentifiableObjectPool getIdentifiableObjectPool()
	{
		if ( identifiableObjectPool.get() == null )
			identifiableObjectPool.set( new IdentifiableObjectPool() );
		
		return identifiableObjectPool.get();
	}

	public < ObjectIdType, ObjectType > IdentifiableObject< ObjectIdType, ObjectType > getIdentifiableObject( ObjectType object )
	{
		IdentifiableObject< ObjectIdType, ObjectType > identifiableObject;

		if ( object == null )
			identifiableObject = null;
		else
			identifiableObject = getIdentifiableObjectInternal( object );
		
		return identifiableObject;
	}
	
	@SuppressWarnings( "unchecked" )
	private < ObjectIdType, ObjectType > IdentifiableObject< ObjectIdType, ObjectType > getIdentifiableObjectInternal( ObjectType object )
	{
		IdentifiableObject< ObjectIdType, ObjectType > identifiableObject = null;
		Set< IdentifiableObject< ?, ? > > identifiableObjects = hashcodeToIdentifiableObjectSetMap.get( object.hashCode() );
		
		if ( identifiableObjects == null )
		{
			identifiableObjects = new HashSet< IdentifiableObject< ?, ? > >();
			hashcodeToIdentifiableObjectSetMap.put( object.hashCode(), identifiableObjects );
		}
		
		identifiableObject = getIdentifiableObject( identifiableObjects, object );
		
		if ( identifiableObject == null )
		{
			ObjectIdType objectId = (ObjectIdType)objectIdProvider.createObjectID();
			identifiableObject = new IdentifiableObject< ObjectIdType, ObjectType >( objectId, object );
			identifiableObjects.add( identifiableObject );
			objectIdToObjectMap.put( identifiableObject.getObjectId(), identifiableObject.getObject() );
		}
		
		return identifiableObject;
	}
	
	@SuppressWarnings( "unchecked" )
	private < ObjectIdType, ObjectType > IdentifiableObject< ObjectIdType, ObjectType > getIdentifiableObject( Set< IdentifiableObject< ?, ? > > identifiableObjects, Object object )
	{
		IdentifiableObject< ObjectIdType, ObjectType > foundObject = null;
		
		for ( IdentifiableObject< ?, ? > identifiableObject : identifiableObjects )
		{
			if ( identifiableObject.getObject() == object )
			{
				foundObject = (IdentifiableObject< ObjectIdType, ObjectType >)identifiableObject;
				break;
			}
		}
		
		return foundObject;
	}
	
	@SuppressWarnings( "unchecked" )
	public < ObjectIdType, ObjectType > ObjectType getObject( ObjectIdType objectId )
	{
		return (ObjectType)objectIdToObjectMap.get( objectId );
	}
	
	public void clear()
	{
		hashcodeToIdentifiableObjectSetMap.clear();
		objectIdToObjectMap.clear();
	}

	public ObjectIDProvider< ? > getObjectIdProvider()
	{
		return objectIdProvider;
	}

	public void setObjectIdProvider( ObjectIDProvider< ? > objectIdProvider )
	{
		this.objectIdProvider = objectIdProvider;
	}
}
