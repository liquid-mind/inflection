package ch.liquidmind.inflection.proxy;

import java.util.Set;

public class SetProxy< E extends Object > extends CollectionProxy< E > implements Set< E >
{
	protected SetProxy( String taxonomyName )
	{
		super( taxonomyName );
	}
}
