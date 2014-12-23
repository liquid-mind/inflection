package ch.liquidmind.inflection;

import java.io.InputStream;

import ch.liquidmind.inflection.compiler.InflectionResourceCompiled;
import ch.liquidmind.inflection.model.InflectionResource;

public class DelegatingInflectionResourceLoader extends InflectionResourceLoader
{
	private ClassLoader delegateClassLoader;
	
	// Constructor for System InflectionResourceLoader.
	@SuppressWarnings( "unused" )
	private DelegatingInflectionResourceLoader( Void unused )
	{
		super( unused );
		this.delegateClassLoader = ClassLoader.getSystemClassLoader();
	}
	
	protected DelegatingInflectionResourceLoader( InflectionResourceLoader parentInflectionResourceLoader, ClassLoader delegateClassLoader )
	{
		super( parentInflectionResourceLoader );
		this.delegateClassLoader = delegateClassLoader;
	}

	@Override
	public InflectionResource findInflectionResource( String name )
	{
		InflectionResourceCompiled resourceCompiled = getInflectionResourceCompiled( name );
		InflectionResource resource = defineInflectionResource( resourceCompiled );
		
		return resource;
	}

	@Override
	public Class< ? > findClass( String name ) throws ClassNotFoundException
	{
		return delegateClassLoader.loadClass( name );
	}
	
	private InflectionResourceCompiled getInflectionResourceCompiled( String name )
	{
		String resourceName = name.replace( ".", "/" ) + InflectionResourceCompiled.INFLECTION_RESOURCE_COMPILED_SUFFIX;
		InputStream inputStream = delegateClassLoader.getResourceAsStream( resourceName );
		InflectionResourceCompiled inflectionResourceCompiled;

		try
		{
			inflectionResourceCompiled = InflectionResourceCompiled.load( inputStream );
		}
		catch ( Throwable t )
		{
			throw new ClassViewNotFoundException( "Unable to load class view: " + name, t );
		}
		
		return inflectionResourceCompiled;
	}
}
