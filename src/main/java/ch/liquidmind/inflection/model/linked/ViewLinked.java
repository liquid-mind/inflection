package ch.liquidmind.inflection.model.linked;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

public class ViewLinked extends AliasableElementLinked implements View
{
	private Class< ? > viewedClass;
	private List< Class< ? > > usedClasses = new ArrayList< Class< ? > >();
	private TaxonomyLinked parentTaxonomyLinked;
	private List< MemberLinked > membersLinked = new ArrayList< MemberLinked >();

	public ViewLinked( String name )
	{
		super( name );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public < T > Class< T > getViewedClass()
	{
		return (Class< T >)viewedClass;
	}

	public void setViewedClass( Class< ? > viewedClass )
	{
		this.viewedClass = viewedClass;
	}

	@Override
	public List< Class< ? > > getUsedClasses()
	{
		return usedClasses;
	}

	public TaxonomyLinked getParentTaxonomyLinked()
	{
		return parentTaxonomyLinked;
	}

	public void setParentTaxonomyLinked( TaxonomyLinked parentTaxonomyLinked )
	{
		this.parentTaxonomyLinked = parentTaxonomyLinked;
	}

	public List< MemberLinked > getMembersLinked()
	{
		return membersLinked;
	}

	@Override
	public Taxonomy getParentTaxonomy()
	{
		return parentTaxonomyLinked;
	}
	
	@SuppressWarnings( { "unchecked" } )
	@Override
	public List< Member > getDeclaredMembers()
	{
		List< MemberLinked > declaredMembers = new ArrayList< MemberLinked >();
		
		// Pass 1: add includes
		for ( MemberLinked memberLinked : membersLinked )
			if ( memberLinked.getSelectionType().equals( SelectionType.INCLUDE ) )
				declaredMembers.add( memberLinked );
		
		// Pass 2: remove excludes
		for ( MemberLinked memberLinked : membersLinked )
			if ( memberLinked.getSelectionType().equals( SelectionType.EXCLUDE ) && containsMemberLinked( declaredMembers, memberLinked.getName(), SelectionType.INCLUDE ) )
				declaredMembers.remove( indexOfMemberLinked( declaredMembers, memberLinked.getName(), SelectionType.INCLUDE ) );		
		
		return (List< Member >)(Object)declaredMembers;
	}

	static boolean containsMemberLinked( List< MemberLinked > membersLinked, String name, SelectionType selectionType )
	{
		return indexOfMemberLinked( membersLinked, name, selectionType ) == -1 ? false : true;
	}
	
	@SuppressWarnings( "unchecked" )
	static int indexOfMemberLinked( List< MemberLinked > membersLinked, String name, SelectionType selectionType )
	{
		return SelectingElementLinked.indexOfSelectingElementLinked( (List< SelectingElementLinked >)(Object)membersLinked, name, selectionType );
	}
	
	@Override
	public List< Member > getUnresolvedMembers()
	{
		return ImmutableList.copyOf( membersLinked );
	}

	@Override
	public View getSuperview()
	{
		Class< ? > superclass = getViewedClass().getSuperclass();
		View superview = getParentTaxonomyLinked().resolveView( superclass );
		
		return superview;
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
	public Member getDeclaredMember( String nameOrAlias )
	{
		Member foundMember = null;
		
		for ( Member declaredMember : getDeclaredMembers() )
		{
			if ( declaredMember.getName().equals( nameOrAlias ) || ( declaredMember.getAlias() != null && declaredMember.getAlias().equals( nameOrAlias ) ) )
			{
				foundMember = declaredMember;
				break;
			}
		}
		
		return foundMember;
	}
}
