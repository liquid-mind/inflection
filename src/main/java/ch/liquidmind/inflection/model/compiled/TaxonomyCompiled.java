package ch.liquidmind.inflection.model.compiled;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import __java.io.__Closeable;
import __java.io.__FileInputStream;
import __java.io.__FileOutputStream;
import __java.io.__ObjectInputStream;
import __java.io.__ObjectOutputStream;
import __org.apache.commons.io.__FileUtils;
import ch.liquidmind.inflection.model.AccessType;

public class TaxonomyCompiled extends AnnotatableElementCompiled
{
	private static final long serialVersionUID = 1L;
	
	public static final String TAXONOMY_COMPILED_SUFFIX = ".tax";
	
	private List< String > extendedTaxonomies = new ArrayList< String >();
	private AccessType defaultAccessType;
	private List< ViewCompiled > viewsCompiled = new ArrayList< ViewCompiled >();

	public TaxonomyCompiled( String name )
	{
		super( name );
	}

	public List< String > getExtendedTaxonomies()
	{
		return extendedTaxonomies;
	}

	public AccessType getDefaultAccessType()
	{
		return defaultAccessType;
	}

	public void setDefaultAccessType( AccessType defaultAccessType )
	{
		this.defaultAccessType = defaultAccessType;
	}

	public List< ViewCompiled > getViewsCompiled()
	{
		return viewsCompiled;
	}

	public String getFileName()
	{
		return getFileName( getName() );
	}
	
	public static String getFileName( String taxonomyName )
	{
		return taxonomyName.replace( ".", "/" ) + TAXONOMY_COMPILED_SUFFIX;
	}
	
	public void save( File baseDir )
	{
		File taxonomyFile = new File( baseDir, getFileName() );
		__FileUtils.forceMkdir( null, new File( taxonomyFile.getParent() ) );
		FileOutputStream fos = __FileOutputStream.__new( taxonomyFile );
		
		try
		{
			save( fos );
		}
		finally
		{
			__Closeable.close( fos );
		}
	}
	
	public void save( OutputStream os )
	{
		ObjectOutputStream oos = __ObjectOutputStream.__new( os );
		
		try
		{
			__ObjectOutputStream.writeObject( oos, this );
		}
		finally
		{
			__Closeable.close( oos );
		}
	}
	
	@SuppressWarnings( "unchecked" )
	public static < T extends TaxonomyCompiled > T load( File baseDir, String taxonomyName )
	{
		File classViewFile = new File( baseDir, getFileName( taxonomyName ) );
		FileInputStream fis = __FileInputStream.__new( classViewFile );
		TaxonomyCompiled taxonomyCompiled;
		
		try
		{
			taxonomyCompiled = load( fis );
		}
		finally
		{
			__Closeable.close( fis );
		}

		return (T)taxonomyCompiled;
	}
	
	@SuppressWarnings( "unchecked" )
	public static < T extends TaxonomyCompiled > T load( InputStream is )
	{
		ObjectInputStream ois = __ObjectInputStream.__new( is );
		TaxonomyCompiled taxonomyCompiled;
		
		try
		{
			taxonomyCompiled = (TaxonomyCompiled)__ObjectInputStream.readObject( ois );
		}
		finally
		{
			__Closeable.close( ois );
		}

		return (T)taxonomyCompiled;
	}
}
