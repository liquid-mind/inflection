package ch.liquidmind.inflection.proxy;

import java.util.Iterator;

public class IteratorProxy< E extends Object > extends Proxy implements Iterator< E >
{
	protected IteratorProxy( String taxonomyName )
	{
		super( taxonomyName );
	}

	@Override
	public boolean hasNext()
	{
        try
        {
    		return invokeOnCollection( "hasNext", new Class< ? >[] {}, new Object[] {} );
        }
        catch ( RuntimeException | Error ex )
        {
            throw ex;
        }
        catch ( java.lang.Throwable ex )
        {
            throw new IllegalStateException( "Unexpected exception type: " + ex.getClass().getName(), ex );
        }
	}

	@Override
	public E next()
	{
        try
        {
    		return invokeOnCollection( "next", new Class< ? >[] {}, new Object[] {} );
        }
        catch ( RuntimeException | Error ex )
        {
            throw ex;
        }
        catch ( java.lang.Throwable ex )
        {
            throw new IllegalStateException( "Unexpected exception type: " + ex.getClass().getName(), ex );
        }
	}
}
