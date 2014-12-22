package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import __java.io.__Closeable;
import __java.io.__FileInputStream;
import __java.io.__FileOutputStream;
import __java.io.__ObjectInputStream;
import __java.io.__ObjectOutputStream;
import __org.apache.commons.io.__FileUtils;

public abstract class InflectionResourceCompiled implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final String INFLECTION_RESOURCE_COMPILED_SUFFIX = ".ires";
	
	private String name;
	
	public InflectionResourceCompiled( String name )
	{
		super();
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getDefaultFileName()
	{
		return getName().replace( ".", "/" ) + INFLECTION_RESOURCE_COMPILED_SUFFIX;
	}
	
	public static void save( File baseDir, InflectionResourceCompiled inflectionResourceCompiled )
	{
		File classViewFile = new File( baseDir, inflectionResourceCompiled.getDefaultFileName() );
		__FileUtils.forceMkdir( null, new File( classViewFile.getParent() ) );
		FileOutputStream fos = __FileOutputStream.__new( classViewFile );
		
		try
		{
			save( fos, inflectionResourceCompiled );
		}
		finally
		{
			__Closeable.close( fos );
		}
	}
	
	public static void save( OutputStream os, InflectionResourceCompiled inflectionResourceCompiled )
	{
		ObjectOutputStream oos = __ObjectOutputStream.__new( os );
		
		try
		{
			__ObjectOutputStream.writeObject( oos, inflectionResourceCompiled );
		}
		finally
		{
			__Closeable.close( oos );
		}
	}
	
	@SuppressWarnings( "unchecked" )
	public static < T extends InflectionResourceCompiled > T load( File baseDir, String classViewName )
	{
		File classViewFile = new File( baseDir, classViewName.replace( ".", "/" ) );
		FileInputStream fis = __FileInputStream.__new( classViewFile );
		InflectionResourceCompiled inflectionResourceCompiled;
		
		try
		{
			inflectionResourceCompiled = load( fis );
		}
		finally
		{
			__Closeable.close( fis );
		}

		return (T)inflectionResourceCompiled;
	}
	
	@SuppressWarnings( "unchecked" )
	public static < T extends InflectionResourceCompiled > T load( InputStream is )
	{
		ObjectInputStream ois = __ObjectInputStream.__new( is );
		InflectionResourceCompiled inflectionResourceCompiled;
		
		try
		{
			inflectionResourceCompiled = (InflectionResourceCompiled)__ObjectInputStream.readObject( ois );
		}
		finally
		{
			__Closeable.close( ois );
		}

		return (T)inflectionResourceCompiled;
	}
}
