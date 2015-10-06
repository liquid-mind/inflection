package ch.liquidmind.inflection2.model.linked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class MethodLinked extends MemberLinked
{
	private Method readMethod;
	private Method writeMethod;

	public MethodLinked(
			List< Annotation > annotations,
			String alias,
			ViewLinked parentViewLinked,
			Method readMethod,
			Method writeMethod )
	{
		super( annotations, alias, parentViewLinked );
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
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
