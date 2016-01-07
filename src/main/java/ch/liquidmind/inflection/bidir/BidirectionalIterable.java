package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.Iterator;

public class BidirectionalIterable< E > implements Iterable< E >
{
	private Object owner;
	private Field field;
	private Iterable< E > targetIterable;

	public BidirectionalIterable( Object owner, Field field, Iterable< E > targetIterable )
	{
		super();
		this.owner = owner;
		this.field = field;
		this.targetIterable = targetIterable;
	}

	public Object getOwner()
	{
		return owner;
	}

	public Field getField()
	{
		return field;
	}

	public Iterable< E > getTargetIterable()
	{
		return targetIterable;
	}

	@Override
	public Iterator< E > iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
