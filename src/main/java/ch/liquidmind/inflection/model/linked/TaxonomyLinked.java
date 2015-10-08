package ch.liquidmind.inflection.model.linked;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

public class TaxonomyLinked extends AnnotatableElementLinked implements Taxonomy
{
	private AccessType defaultAccessType;
	private List< TaxonomyLinked > extendedTaxonomiesLinked = new ArrayList< TaxonomyLinked >();
	private List< TaxonomyLinked > extendingTaxonomiesLinked = new ArrayList< TaxonomyLinked >();
	private List< ViewLinked > viewsLinked = new ArrayList< ViewLinked >();

	public TaxonomyLinked( String name )
	{
		super( name );
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
		return ImmutableList.copyOf( getViewsLinked() );
	}
}
