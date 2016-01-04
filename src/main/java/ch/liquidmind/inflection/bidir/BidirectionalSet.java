package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.Set;

public class BidirectionalSet< E > extends BidirectionalCollection< E > implements Set< E >
{
	public BidirectionalSet( Object owner, Field field, Set< E > targetSet )
	{
		super( owner, field, targetSet );
	}
	
	public Set< E > getTargetSet()
	{
		return (Set< E >)getTargetCollection();
	}
}
