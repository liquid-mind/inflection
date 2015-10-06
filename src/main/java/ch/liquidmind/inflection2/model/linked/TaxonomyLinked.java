package ch.liquidmind.inflection2.model.linked;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.liquidmind.inflection2.model.external.Taxonomy;
import ch.liquidmind.inflection2.model.external.View;

public class TaxonomyLinked extends AnnotatableElementLinked implements Taxonomy
{
	private String name;
	private List< TaxonomyLinked > extendedTaxonomiesLinked = new ArrayList< TaxonomyLinked >();
	private List< TaxonomyLinked > extendingTaxonomiesLinked = new ArrayList< TaxonomyLinked >();
	private List< ViewLinked > viewsLinked = new ArrayList< ViewLinked >();
	
	public TaxonomyLinked(
			List< Annotation > annotations,
			String name,
			List< TaxonomyLinked > extendedTaxonomiesLinked,
			List< TaxonomyLinked > extendingTaxonomiesLinked,
			List< ViewLinked > viewsLinked )
	{
		super( annotations );
		this.name = name;
		this.extendedTaxonomiesLinked = extendedTaxonomiesLinked;
		this.extendingTaxonomiesLinked = extendingTaxonomiesLinked;
		this.viewsLinked = viewsLinked;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
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
		return ImmutableList.copyOf( getViewsLinked() );
	}
}
