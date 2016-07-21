package ch.liquidmind.inflection.proxy.memory;

import java.net.URLClassLoader;

public class AgentClassLoader extends URLClassLoader
{
	public AgentClassLoader()
	{
		super( getSystemClassLoader().getURLs(), getExtensionsClassLoader() );
	}
	
	private static URLClassLoader getExtensionsClassLoader()
	{
		return (URLClassLoader)ClassLoader.getSystemClassLoader().getParent();
	}
	
	public static URLClassLoader getSystemClassLoader()
	{
		return (URLClassLoader)ClassLoader.getSystemClassLoader();
	}
}

