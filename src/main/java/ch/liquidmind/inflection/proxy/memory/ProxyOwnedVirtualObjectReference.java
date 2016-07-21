package ch.liquidmind.inflection.proxy.memory;

import ch.liquidmind.inflection.Auxiliary;

public class ProxyOwnedVirtualObjectReference extends VirtualObjectReference
{
	private Object viewedObject;
	private Auxiliary auxiliaryObject;

	public ProxyOwnedVirtualObjectReference( Object viewedObject, Auxiliary auxiliaryObject )
	{
		super();
		this.viewedObject = viewedObject;
		this.auxiliaryObject = auxiliaryObject;
	}

	public Object getViewedObject()
	{
		return viewedObject;
	}

	public void setViewedObject( Object viewedObject )
	{
		this.viewedObject = viewedObject;
	}

	public Auxiliary getAuxiliaryObject()
	{
		return auxiliaryObject;
	}

	public void setAuxiliaryObject( Auxiliary auxiliaryObject )
	{
		this.auxiliaryObject = auxiliaryObject;
	}
}
