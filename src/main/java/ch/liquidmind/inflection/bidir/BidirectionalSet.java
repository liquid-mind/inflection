package ch.liquidmind.inflection.bidir;

import java.util.Set;

public class BidirectionalSet< E extends Object > extends BidirectionalCollection< E > implements Set< E >
{
	public BidirectionalSet( Object owner, String opposingPropertyName, Object target )
	{
		super( owner, opposingPropertyName, target );
	}

	@Override
	public Set< E > getTarget()
	{
		return (Set< E >)super.getTarget();
	}
}
