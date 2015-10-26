package ch.liquidmind.inflection.model.linked;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import __java.lang.__ClassLoader;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

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
	
	@SuppressWarnings( "unchecked" )
	@Override
	public List< View > getViews()
	{
		return (List< View >)(Object)getViews( this, new ArrayList< TaxonomyLinked >() );
	}
	
	@SuppressWarnings( "unchecked" )
	private static List< ViewLinked > getViews( TaxonomyLinked taxonomyLinked, List< TaxonomyLinked > taxonomyLinkedSiblings )
	{
		List< ViewLinked > views = new ArrayList< ViewLinked >();
		
		if ( taxonomyLinked != null )
		{
			List< ViewLinked > combinedViews = getCombinedViews( taxonomyLinked.getExtendedTaxonomiesLinked(), taxonomyLinkedSiblings );
			List< ViewLinked > unresolvedViews = (List< ViewLinked >)(Object)taxonomyLinked.getUnresolvedViews();
			views = resolveViews( unresolvedViews, combinedViews );
		}

		return views;
	}
	
	private static List< ViewLinked > getCombinedViews( List< TaxonomyLinked > extendedTaxonomiesLinked, List< TaxonomyLinked > taxonomyLinkedSiblings )
	{
		List< ViewLinked > extendedViewsLinked = getViews( getFirstTaxonomyLinked( extendedTaxonomiesLinked ), getFollowingTaxonomiesLinked( extendedTaxonomiesLinked ) );
		List< ViewLinked > siblingsViewsLinked = getViews( getFirstTaxonomyLinked( taxonomyLinkedSiblings ), getFollowingTaxonomiesLinked( taxonomyLinkedSiblings ) );
		List< ViewLinked > combinedViews = new ArrayList< ViewLinked >();
		
		combinedViews.addAll( siblingsViewsLinked );
		
		for ( ViewLinked extendedViewLinked : extendedViewsLinked )
		{
			if ( containsViewLinked( combinedViews, extendedViewLinked.getName(), extendedViewLinked.getSelectionType() ) )
				combinedViews.set( indexOfViewLinked( combinedViews, extendedViewLinked.getName(), extendedViewLinked.getSelectionType() ), extendedViewLinked );
			else
				combinedViews.add( extendedViewLinked );
		}
		
		return combinedViews;
	}
	
	private static TaxonomyLinked getFirstTaxonomyLinked( List< TaxonomyLinked > taxonomyLinkedSiblings )
	{
		TaxonomyLinked firstTaxonomyLinked;
		
		if ( taxonomyLinkedSiblings.size() == 0 )
			firstTaxonomyLinked = null;
		else
			firstTaxonomyLinked = taxonomyLinkedSiblings.get( 0 );
		
		return firstTaxonomyLinked;
	}
	
	private static List< TaxonomyLinked > getFollowingTaxonomiesLinked( List< TaxonomyLinked > taxonomyLinkedSiblings )
	{
		List< TaxonomyLinked > followingTaxonomiesLinked;
		
		if ( taxonomyLinkedSiblings.size() > 1 )
			followingTaxonomiesLinked = taxonomyLinkedSiblings.subList( 1, taxonomyLinkedSiblings.size() );
		else
			followingTaxonomiesLinked = new ArrayList< TaxonomyLinked >();
		
		return followingTaxonomiesLinked;
	}
	
	@SuppressWarnings( "unchecked" )
	@Override
	public List< View > getDeclaredViews()
	{
		return (List< View >)(Object)resolveViews( viewsLinked, new ArrayList< ViewLinked >() );
	}
	
	private static List< ViewLinked > resolveViews( List< ViewLinked > unresolvedViews, List< ViewLinked > previouslyResolvedViews )
	{
		List< ViewLinked > resolvedViews = new ArrayList< ViewLinked >();
		resolvedViews.addAll( previouslyResolvedViews );
		
		// Pass 1: add includes
		for ( ViewLinked unresolvedView : unresolvedViews )
		{
			if ( unresolvedView.getSelectionType().equals( SelectionType.INCLUDE ) )
			{
				if ( containsViewLinked( resolvedViews, unresolvedView.getName(), SelectionType.INCLUDE ) )
					resolvedViews.set( indexOfViewLinked( resolvedViews, unresolvedView.getName(), unresolvedView.getSelectionType() ), unresolvedView );
				else
					resolvedViews.add( unresolvedView );
			}
		}
		
		// Pass 2: remove excludes
		for ( ViewLinked unresolvedView : unresolvedViews )
		{
			if ( unresolvedView.getSelectionType().equals( SelectionType.EXCLUDE ) && containsViewLinked( resolvedViews, unresolvedView.getName(), SelectionType.INCLUDE ) )
				resolvedViews.remove( indexOfViewLinked( resolvedViews, unresolvedView.getName(), SelectionType.INCLUDE ) );
		}
		
		return resolvedViews;
	}

	private static boolean containsViewLinked( List< ViewLinked > viewsLinked, String name, SelectionType selectionType )
	{
		return indexOfViewLinked( viewsLinked, name, selectionType ) == -1 ? false : true;
	}
	
	@SuppressWarnings( "unchecked" )
	private static int indexOfViewLinked( List< ViewLinked > viewsLinked, String name, SelectionType selectionType )
	{
		return SelectingElementLinked.indexOfSelectingElementLinked( (List< SelectingElementLinked >)(Object)viewsLinked, name, selectionType );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List< View > getUnresolvedViews()
	{
		return ImmutableList.copyOf( (List< View >)(Object)viewsLinked );
	}

	@Override
	public View getView( String name )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getDeclaredView( String name )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getUnresolvedView( String name )
	{
		// TODO Auto-generated method stub
		return null;
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
		
		for ( View view : getViews() )
		{
			if ( view.getViewedClass().equals( viewedClass ) )
			{
				resolvedView = view;
				break;
			}
		}
		
		return resolvedView;
	}

	public TaxonomyLoader getTaxonomyLoader()
	{
		return taxonomyLoader;
	}

	@Override
	public String getPackageName()
	{
		return getPackageName( getName() );
	}

	@Override
	public String getSimpleName()
	{
		return getSimpleName( getName() );
	}
}
