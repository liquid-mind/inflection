package ch.liquidmind.inflection.proxy.memory;

import ch.liquidmind.inflection.proxy.Proxy;

public class AuxiliaryOwnedVirtualObjectReference extends VirtualObjectReference
{
	private Object viewedObject;
	private Proxy proxyObject;

	public AuxiliaryOwnedVirtualObjectReference( Object viewedObject, Proxy proxyObject )
	{
		super();
		this.viewedObject = viewedObject;
		this.proxyObject = proxyObject;
	}

	public Object getViewedObject()
	{
		return viewedObject;
	}

	public void setViewedObject( Object viewedObject )
	{
		this.viewedObject = viewedObject;
	}

	public Proxy getProxyObject()
	{
		return proxyObject;
	}

	public void setProxyObject( Proxy proxyObject )
	{
		this.proxyObject = proxyObject;
	}
}
