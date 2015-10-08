package ch.liquidmind.inflection.model.compiled;

import java.util.ArrayList;
import java.util.List;

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
}
