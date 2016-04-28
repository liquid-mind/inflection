package ch.liquidmind.inflection.proxy;

import java.util.Iterator;

public class IterableProxy< E > extends Proxy implements Iterable< E >
{
	@Override
	public Iterator< E > iterator()
	{
   		return invokeOnCollection( "iterator", new Class< ? >[] {}, new Object[] {} );
	}
}
