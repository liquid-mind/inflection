package ch.liquidmind.inflection.support;

import java.lang.reflect.Type;

public class UnadornedClassVisitor extends AbstractTypeVisitor
{
	private Class< ? > unadornedClass;

	@Override
	public void visitActualTypeArguments( Type[] actualTypeArguments )
	{
		// Don't walk this path.
	}

	@Override
	public void visitClass( Class< ? > classType )
	{
		unadornedClass = classType;
		super.visitClass( classType );
	}

	public Class< ? > getUnadornedClass()
	{
		return unadornedClass;
	}
}
