package ch.liquidmind.inflection.model.linked;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.model.external.ViewRaw;

public class ViewLinked extends AliasableElementLinked implements ViewRaw
{
	private Class< ? > viewedClass;
	private List< Class< ? > > usedClasses = new ArrayList< Class< ? > >();
	private TaxonomyLinked parentTaxonomyLinked;
	private List< MemberLinked > membersLinked = new ArrayList< MemberLinked >();

	public ViewLinked( String name )
	{
		super( name );
	}

	public Class< ? > getViewedClass()
	{
		return viewedClass;
	}

	public void setViewedClass( Class< ? > viewedClass )
	{
		this.viewedClass = viewedClass;
	}

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
		return getParentTaxonomyLinked();
	}

	@Override
	public List< Member > getMembers()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List< Member > getDeclaredMembers()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List< Member > getMembersRaw()
	{
		return ImmutableList.copyOf( getMembersRaw( this ) );
	}
	
	public List< MemberLinked > getMembersRaw( ViewLinked viewLinked )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List< Member > getDeclaredMembersRaw()
	{
		return ImmutableList.copyOf( getMembersLinked() );
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( getSelectionType() == null ) ? 0 : getSelectionType().hashCode() );
		result = prime * result + ( ( viewedClass == null ) ? 0 : viewedClass.hashCode() );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		ViewLinked other = (ViewLinked)obj;
		if ( getSelectionType() != other.getSelectionType() )
			return false;
		if ( viewedClass == null )
		{
			if ( other.viewedClass != null )
				return false;
		}
		else if ( !viewedClass.equals( other.viewedClass ) )
			return false;
		return true;
	}

	@Override
	public View getSuperview()
	{
		Class< ? > superclass = getViewedClass().getSuperclass();
		View superview = getParentTaxonomyLinked().resolveView( superclass );
		
		return superview;
	}
}
