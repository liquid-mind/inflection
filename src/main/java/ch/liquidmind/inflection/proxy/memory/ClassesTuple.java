package ch.liquidmind.inflection.proxy.memory;

public class ClassesTuple
{
	private Class< ? > proxyClass;
	private Class< ? > objectClass;
	private Class< ? > auxiliaryClass;
	
	public ClassesTuple( Class< ? > proxyClass, Class< ? > objectClass, Class< ? > auxiliaryClass )
	{
		super();
		this.proxyClass = proxyClass;
		this.objectClass = objectClass;
		this.auxiliaryClass = auxiliaryClass;
	}

	public Class< ? > getProxyClass()
	{
		return proxyClass;
	}

	public Class< ? > getObjectClass()
	{
		return objectClass;
	}

	public Class< ? > getAuxiliaryClass()
	{
		return auxiliaryClass;
	}
}