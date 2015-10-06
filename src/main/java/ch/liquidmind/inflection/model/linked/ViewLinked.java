package ch.liquidmind.inflection.model.linked;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

public class ViewLinked extends AliasableElementLinked implements View
{
	private Class< ? > viewedClass;
	private List< Class< ? > > usedClasses = new ArrayList< Class< ? > >();
	private TaxonomyLinked parentTaxonomyLinked;
	private List< MemberLinked > membersLinked = new ArrayList< MemberLinked >();
	
	public ViewLinked(
			List< Annotation > annotations,
			String alias,
			Class< ? > viewedClass,
			List< Class< ? > > usedClasses,
			TaxonomyLinked parentTaxonomyLinked,
			List< MemberLinked > membersLinked )
	{
		super( annotations, alias );
		this.viewedClass = viewedClass;
		this.usedClasses = usedClasses;
		this.parentTaxonomyLinked = parentTaxonomyLinked;
		this.membersLinked = membersLinked;
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
		return ImmutableList.copyOf( getMembersLinked() );
	}
}
