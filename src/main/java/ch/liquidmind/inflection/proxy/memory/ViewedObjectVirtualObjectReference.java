package ch.liquidmind.inflection.proxy.memory;

import ch.liquidmind.inflection.Auxiliary;
import ch.liquidmind.inflection.proxy.Proxy;

public class ViewedObjectVirtualObjectReference extends VirtualObjectReference
{
	private Proxy proxyObject;
	private Auxiliary auxiliaryObject;

	public ViewedObjectVirtualObjectReference( Proxy proxyObject, Auxiliary auxiliaryObject )
	{
		super();
		this.proxyObject = proxyObject;
		this.auxiliaryObject = auxiliaryObject;
	}

	public Proxy getProxyObject()
	{
		return proxyObject;
	}

	public Auxiliary getAuxiliaryObject()
	{
		return auxiliaryObject;
	}
}
