package ch.liquidmind.inflection.model.linked;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import __java.lang.__ClassLoader;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.model.external.ViewRaw;

public class TaxonomyLinked extends AnnotatableElementLinked implements Taxonomy
{
	private TaxonomyLoader taxonomyLoader;
	private AccessType defaultAccessType;
	private List< TaxonomyLinked > extendedTaxonomiesLinked = new ArrayList< TaxonomyLinked >();
	private List< TaxonomyLinked > extendingTaxonomiesLinked = new ArrayList< TaxonomyLinked >();
	private List< ViewLinked > viewsLinked = new ArrayList< ViewLinked >();

	public TaxonomyLinked( String name, TaxonomyLoader taxonomyLoader )
	{
		super( name );
		this.taxonomyLoader = taxonomyLoader;
	}

	public AccessType getDefaultAccessType()
	{
		AccessType effectiveDefaultAccessType;
		
		if ( defaultAccessType != null )
			effectiveDefaultAccessType = defaultAccessType;
		else
			effectiveDefaultAccessType = extendedTaxonomiesLinked.get( 0 ).getDefaultAccessType();
		
		return effectiveDefaultAccessType;
	}

	public AccessType getDeclaredDefaultAccessType()
	{
		return defaultAccessType;
	}

	public void setDefaultAccessType( AccessType defaultAccessType )
	{
		this.defaultAccessType = defaultAccessType;
	}

	public List< TaxonomyLinked > getExtendedTaxonomiesLinked()
	{
		return extendedTaxonomiesLinked;
	}

	public List< TaxonomyLinked > getExtendingTaxonomiesLinked()
	{
		return extendingTaxonomiesLinked;
	}

	public List< ViewLinked > getViewsLinked()
	{
		return viewsLinked;
	}

	@Override
	public List< Taxonomy > getExtendedTaxonomies()
	{
		return ImmutableList.copyOf( getExtendedTaxonomiesLinked() );
	}

	@Override
	public List< Taxonomy > getExtendingTaxonomies()
	{
		return ImmutableList.copyOf( getExtendingTaxonomiesLinked() );
	}

	@Override
	public List< View > getViews()
	{
		return getViews( getViewsRaw() );
	}

	@Override
	public List< View > getDeclaredViews()
	{
		return getViews( getDeclaredViewsRaw() );
	}
	
	private List< View > getViews( List< ViewRaw > viewsRaw )
	{
		List< View > views = new ArrayList< View >();
		
		// Pass 1: add includes
		for ( ViewRaw viewRaw : viewsRaw )
			if ( viewRaw.getSelectionType().equals( SelectionType.INCLUDE ) )
				views.add( viewRaw );
		
		// Pass 2: remove excludes
		for ( ViewRaw viewRaw : viewsRaw )
		{
			if ( viewRaw.getSelectionType().equals( SelectionType.EXCLUDE ) )
			{
				Iterator< View > iter = views.iterator();
				
				while( iter.hasNext() )
				{
					View declaredView = iter.next();
					if ( declaredView.getName().equals( viewRaw.getName() ) )
					{
						views.remove( declaredView );
						break;
					}
				}
			}
		}
		
		return views;
	}

	@Override
	public View getView( String name )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public View getDeclaredView( String name )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public View resolveView( String viewedClassName )
	{
		return resolveView( __ClassLoader.loadClass( taxonomyLoader.getClassLoader(), viewedClassName ) );
	}

	@Override
	public View resolveView( Class< ? > viewedClass )
	{
		View resolvedView = null;
		
		for ( ViewLinked viewLinked : viewsLinked )
		{
			if ( viewLinked.getViewedClass().equals( viewedClass ) )
			{
				resolvedView = viewLinked;
				break;
			}
		}
		
		return resolvedView;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List< ViewRaw > getViewsRaw()
	{
		return (List< ViewRaw >)(Object)getViewsRaw( this );
	}
	
	private static List< ViewLinked > getViewsRaw( TaxonomyLinked taxonomyLinked )
	{
		List< ViewLinked > viewsRaw = new ArrayList< ViewLinked >();
		List< TaxonomyLinked > extendedTaxonomiesLinked = taxonomyLinked.getExtendedTaxonomiesLinked();
		
		for ( int i = extendedTaxonomiesLinked.size() - 1 ; i >= 0 ; --i )
		{
			TaxonomyLinked extendedTaxonomyLinked = extendedTaxonomiesLinked.get( i );
			List< ViewLinked > viewsRawDelta = getViewsRaw( extendedTaxonomyLinked );
			addViewsRaw( viewsRaw, viewsRawDelta );
		}

		List< ViewLinked > viewsRawDelta = taxonomyLinked.getViewsLinked();
		addViewsRaw( viewsRaw, viewsRawDelta );
		
		return viewsRaw;
	}
	
	private static void addViewsRaw( List< ViewLinked > viewsRaw, List< ViewLinked > viewsRawDelta )
	{
		for ( ViewLinked viewRawDelta : viewsRawDelta )
		{
			boolean foundMatch = false;
			
			for ( int i = 0 ; i < viewsRaw.size() ; ++i )
			{
				ViewLinked viewRaw = viewsRaw.get( i );
				
				if ( viewRaw.equals( viewRawDelta ) )
				{
					viewsRaw.set( i, viewRawDelta );
					foundMatch = true;
					break;
				}
			}
			
			if ( !foundMatch )
				viewsRaw.add( viewRawDelta );
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List< ViewRaw > getDeclaredViewsRaw()
	{
		return (List< ViewRaw >)(Object)ImmutableList.copyOf( getViewsLinked() );
	}

	@Override
	public ViewRaw getViewRaw( String name )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ViewRaw getDeclaredViewRaw( String name )
	{
		throw new UnsupportedOperationException();
	}

	public TaxonomyLoader getTaxonomyLoader()
	{
		return taxonomyLoader;
	}
}
