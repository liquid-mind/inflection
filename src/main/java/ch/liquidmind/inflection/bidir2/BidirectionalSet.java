package ch.liquidmind.inflection.bidir2;

import java.lang.reflect.Field;
import java.util.Set;

public class BidirectionalSet< E extends Object > extends BidirectionalCollection< E > implements Set< E >
{
	public BidirectionalSet( Object owner, Field field, Object target )
	{
		super( owner, field, target );
	}

	@Override
	public Set< E > getTarget()
	{
		return (Set< E >)super.getTarget();
	}
}
