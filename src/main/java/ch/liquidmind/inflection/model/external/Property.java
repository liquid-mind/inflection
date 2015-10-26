package ch.liquidmind.inflection.model.external;

import java.lang.reflect.Method;

public interface Property extends Member
{
	public Method getReadMethod();
	public Method getWriteMethod();
}
