package ch.liquidmind.inflection.proxy;

import java.util.Iterator;

public class IteratorProxy< E extends Object > extends Proxy implements Iterator< E >
{
	@Override
	public boolean hasNext()
	{
   		return invokeOnCollection( "hasNext", new Class< ? >[] {}, new Object[] {} );
	}

	@Override
	public E next()
	{
   		return invokeOnCollection( "next", new Class< ? >[] {}, new Object[] {} );
	}
}
