package ch.liquidmind.inflection.proxy.memory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import __java.lang.__Class;
import __java.lang.reflect.__Field;
import ch.liquidmind.inflection.Auxiliary;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.proxy.Proxy;

public class GarbageCollectingTaxonomySpecificMemoryManager extends TaxonomySpecificMemoryManager
{
	private static Field proxyOwnedField;
	private static Field auxiliaryOwnedField;
	private static Map< Class< ? >, Field > objectOwnedFields = new HashMap< Class< ? >, Field >();
	
	static
	{
		proxyOwnedField = __Class.getDeclaredField( Proxy.class, "virtualObjectReference" );
		auxiliaryOwnedField = __Class.getDeclaredField( Auxiliary.class, "virtualObjectReference" );
		proxyOwnedField.setAccessible( true );
		auxiliaryOwnedField.setAccessible( true );
	}
	
	public GarbageCollectingTaxonomySpecificMemoryManager( Taxonomy taxonomy )
	{
		super( taxonomy );
	}

	@Override
	protected ObjectsTuple getObjectTuple( Object key )
	{
		return createObjectTuple( key );
	}

	@Override
	protected ObjectsTuple createObjectTuple( Object object )
	{
		ObjectsTuple objectsTuple;
		VirtualObjectReference virtualObjectReference = getVirtualObjectReference( object );
	
		// Set the virtual object references from the tuple.
		if ( virtualObjectReference == null )
		{
			objectsTuple = super.createObjectTuple( object );
			setVirtualObjectReferences( objectsTuple );
		}
		// Set the tuple from the virtual object references.
		else
		{
			objectsTuple = getObjectsTuple( object, virtualObjectReference );
		}
		
		return objectsTuple;
	}
	
	private ObjectsTuple getObjectsTuple( Object object, VirtualObjectReference virtualObjectReference )
	{
		ObjectsTuple objectsTuple;
		
		if ( object instanceof Proxy )
		{
			Proxy proxy = (Proxy)object;
			ProxyOwnedVirtualObjectReference poVirtualObjectReference = (ProxyOwnedVirtualObjectReference)virtualObjectReference;
			objectsTuple = new ObjectsTuple( proxy, poVirtualObjectReference.getViewedObject(), poVirtualObjectReference.getAuxiliaryObject() );
		}
		else if ( object instanceof Auxiliary )
		{
			Auxiliary auxiliary = (Auxiliary)object;
			AuxiliaryOwnedVirtualObjectReference aoVirtualObjectReference = (AuxiliaryOwnedVirtualObjectReference)virtualObjectReference;
			objectsTuple = new ObjectsTuple( aoVirtualObjectReference.getProxyObject(), aoVirtualObjectReference.getViewedObject(), auxiliary );
		}
		else
		{
			ViewedObjectVirtualObjectReference voVirtualObjectReference = (ViewedObjectVirtualObjectReference)virtualObjectReference;
			objectsTuple = new ObjectsTuple( voVirtualObjectReference.getProxyObject(), object, voVirtualObjectReference.getAuxiliaryObject() );
		}
		
		return objectsTuple;
	}
	
	@SuppressWarnings( "unchecked" )
	private < T extends VirtualObjectReference > T getVirtualObjectReference( Object object )
	{
		VirtualObjectReference virtualObjectReference;
		
		if ( object instanceof Proxy )
			virtualObjectReference = (VirtualObjectReference)__Field.get( proxyOwnedField, object );
		else if ( object instanceof Auxiliary )
			virtualObjectReference = (VirtualObjectReference)__Field.get( auxiliaryOwnedField, object );
		else
			virtualObjectReference = getViewedObjectVirtualObjectReferences( object ).getReferences().get( getTaxonomy() );
		
		return (T)virtualObjectReference;
	}
	
	private ViewedObjectVirtualObjectReferences getViewedObjectVirtualObjectReferences( Object object )
	{
		Class< ? > theClass = object.getClass();
		Field objectOwnedField = objectOwnedFields.get( theClass );
		
		if ( objectOwnedField == null )
		{
			objectOwnedField = __Class.getDeclaredField( theClass, "virtualObjectReference" );
			objectOwnedField.setAccessible( true );
		}
		
		ViewedObjectVirtualObjectReferences voVirtualObjectReferences = (ViewedObjectVirtualObjectReferences)__Field.get( objectOwnedField, object );
		
		if ( voVirtualObjectReferences == null )
		{
			voVirtualObjectReferences = new ViewedObjectVirtualObjectReferences();
			__Field.set( objectOwnedField, object, voVirtualObjectReferences );
		}

		return voVirtualObjectReferences;
	}
	
	private void setVirtualObjectReferences( ObjectsTuple objectsTuple )
	{
		Proxy proxy = objectsTuple.getProxy();
		Auxiliary auxiliary = objectsTuple.getAuxiliary();
		Object object = objectsTuple.getObject();
		
		__Field.set( proxyOwnedField, proxy, new ProxyOwnedVirtualObjectReference( object, auxiliary ) );
		
		if ( auxiliary != null )
			__Field.set( auxiliaryOwnedField, proxy, new AuxiliaryOwnedVirtualObjectReference( object, proxy ) );
			
		getViewedObjectVirtualObjectReferences( object ).getReferences().put( getTaxonomy(), new ViewedObjectVirtualObjectReference( proxy, auxiliary ) );
	}
}
