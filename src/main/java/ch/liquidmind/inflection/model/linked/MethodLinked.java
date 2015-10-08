package ch.liquidmind.inflection.model.linked;

import java.lang.reflect.Method;

public class MethodLinked extends MemberLinked
{
	private Method readMethod;
	private Method writeMethod;

	public MethodLinked( String name )
	{
		super( name );
	}

	public Method getReadMethod()
	{
		return readMethod;
	}

	public void setReadMethod( Method readMethod )
	{
		this.readMethod = readMethod;
	}

	public Method getWriteMethod()
	{
		return writeMethod;
	}

	public void setWriteMethod( Method writeMethod )
	{
		this.writeMethod = writeMethod;
	}
}
