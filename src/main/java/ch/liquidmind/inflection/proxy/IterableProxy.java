package ch.liquidmind.inflection.proxy;

import java.util.Iterator;

public class IterableProxy< E > extends Proxy implements Iterable< E >
{
	protected IterableProxy( String taxonomyName )
	{
		super( taxonomyName );
	}

	@Override
	public Iterator< E > iterator()
	{
   		return invokeOnCollection( "iterator", new Class< ? >[] {}, new Object[] {} );
	}
}
