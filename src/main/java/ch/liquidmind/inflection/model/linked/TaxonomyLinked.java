package ch.liquidmind.inflection.model.linked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import __java.lang.__ClassLoader;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.model.external.Member;
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

	@Override
	public AccessType getDefaultAccessType()
	{
		AccessType effectiveDefaultAccessType;
		
		if ( defaultAccessType != null )
			effectiveDefaultAccessType = defaultAccessType;
		else
			effectiveDefaultAccessType = extendedTaxonomiesLinked.get( 0 ).getDefaultAccessType();
		
		return effectiveDefaultAccessType;
	}

	@Override
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
	
	private List< View > viewsCached;
	
	@SuppressWarnings( "unchecked" )
	@Override
	public List< View > getViews()
	{
		if ( viewsCached == null )
			viewsCached = (List< View >)(Object)calculateViews( this, new ArrayList< TaxonomyLinked >() );
		
		return viewsCached;
	}
	
	@SuppressWarnings( "unchecked" )
	private static List< ViewLinked > calculateViews( TaxonomyLinked taxonomyLinked, List< TaxonomyLinked > taxonomyLinkedSiblings )
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
		List< ViewLinked > extendedViewsLinked = calculateViews( getFirstTaxonomyLinked( extendedTaxonomiesLinked ), getFollowingTaxonomiesLinked( extendedTaxonomiesLinked ) );
		List< ViewLinked > siblingsViewsLinked = calculateViews( getFirstTaxonomyLinked( taxonomyLinkedSiblings ), getFollowingTaxonomiesLinked( taxonomyLinkedSiblings ) );
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
	
	private List< View > declaredViewsCached;
	
	@Override
	public List< View > getDeclaredViews()
	{
		if ( declaredViewsCached == null )
			declaredViewsCached = calculateDeclaredViews();
		
		return declaredViewsCached;
	}
	
	@SuppressWarnings( "unchecked" )
	private List< View > calculateDeclaredViews()
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
	
	private Map< String, View > viewsByNameOrAliasCached;

	@Override
	public View getView( String nameOrAlias )
	{
		if ( viewsByNameOrAliasCached == null )
			viewsByNameOrAliasCached = calculateViewsByNameOrAlias();
		
		return viewsByNameOrAliasCached.get( nameOrAlias );
	}

	public Map< String, View > calculateViewsByNameOrAlias()
	{
		Map< String, View > viewsByNameOrAlias = new HashMap< String, View >();
		
		for ( View view : getViews() )
		{
			viewsByNameOrAlias.put( view.getName(), view );
			
			if ( view.getAlias() != null )
				viewsByNameOrAlias.put( view.getAlias(), view );
		}
		
		return viewsByNameOrAlias;
	}

	private Map< String, View > declaredViewsByNameOrAliasCached;
	
	@Override
	public View getDeclaredView( String nameOrAlias )
	{
		if ( declaredViewsByNameOrAliasCached == null )
			declaredViewsByNameOrAliasCached = calculateDeclaredViewsByNameOrAlias();
		
		return declaredViewsByNameOrAliasCached.get( nameOrAlias );
	}

	public Map< String, View > calculateDeclaredViewsByNameOrAlias()
	{
		Map< String, View > declaredViewsByNameOrAlias = new HashMap< String, View >();
		
		for ( View declaredView : getDeclaredViews() )
		{
			declaredViewsByNameOrAlias.put( declaredView.getName(), declaredView );
			
			if ( declaredView.getAlias() != null )
				declaredViewsByNameOrAlias.put( declaredView.getAlias(), declaredView );
		}
		
		return declaredViewsByNameOrAlias;
	}
	
	@Override
	public View getUnresolvedView( String name )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public View resolveView( String viewedClassName )
	{
		return resolveView( __ClassLoader.loadClass( taxonomyLoader.getClassLoader(), viewedClassName ) );
	}
	
	private static Map< Class< ? >, View > resolvedViewsCached;

	@Override
	public View resolveView( Class< ? > viewedClass )
	{
		if ( resolvedViewsCached == null )
			resolvedViewsCached = calculateResolvedViews();
		
		return resolvedViewsCached.get( viewedClass );
	}
	
	private Map< Class< ? >, View > calculateResolvedViews()
	{
		Map< Class< ? >, View > resolvedViews = new HashMap< Class< ? >, View >();
		
		for ( View view : getViews() )
			resolvedViews.put( view.getViewedClass(), view );
		
		return resolvedViews;
	}

	@Override
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

	@Override
	public View getSuperview( View view )
	{
		return resolveView( view.getViewedClass().getSuperclass() );
	}
	
	private Map< View, List< Member > > membersByViewCached = new HashMap< View, List< Member > >();
	
	@Override
	public List< Member > getMembers( View view )
	{
		List< Member > members = membersByViewCached.get( view );
		
		if ( members == null )
		{
			members = calculateMembers( view );
			membersByViewCached.put( view, members );
		}
		
		return members;
	}
	
	@SuppressWarnings( "unchecked" )
	private List< Member > calculateMembers( View view )
	{
		List< Member > members = new ArrayList< Member >();
		
		if ( view != null )
		{
			List< Member > superViewMembers = getMembers( (ViewLinked)getSuperview( view ) );
			List< Member > declaredMembers = view.getDeclaredMembers();
			members.addAll( superViewMembers );
			
			for ( Member declaredMember : declaredMembers )
			{
				if ( ViewLinked.containsMemberLinked( (List< MemberLinked >)(Object)members, declaredMember.getName(), declaredMember.getSelectionType() ) )
					members.set( ViewLinked.indexOfMemberLinked( (List< MemberLinked >)(Object)members, declaredMember.getName(), declaredMember.getSelectionType() ), declaredMember );
				else
					members.add( declaredMember );
			}
		}
		
		return members;
	}
	
	private Map< View, Map< String, Member > > membersByViewAndNameOrAliasCached = new HashMap< View, Map< String, Member > >();
	
	@Override
	public Member getMember( View view, String nameOrAlias )
	{
		Map< String, Member > membersByView = membersByViewAndNameOrAliasCached.get( view );
		
		if ( membersByView == null )
		{
			membersByView = calculateMembersByView( view );
			membersByViewAndNameOrAliasCached.put( view, membersByView );
		}
		
		return membersByView.get( nameOrAlias );
	}
	
	private Map< String, Member > calculateMembersByView( View view )
	{
		Map< String, Member > membersByView = new HashMap< String, Member >();
		
		for ( Member member : getMembers( view ) )
		{
			membersByView.put( member.getName(), member );
			
			if ( member.getAlias() != null )
				membersByView.put( member.getAlias(), member );
		}
		
		return membersByView;
	}
}
