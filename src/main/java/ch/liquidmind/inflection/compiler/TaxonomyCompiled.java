package ch.liquidmind.inflection.compiler;

import java.util.ArrayList;
import java.util.List;

public class TaxonomyCompiled extends InflectionResourceCompiled
{
	private static final long serialVersionUID = 1L;

	private String extendedTaxonomyName;
	private List< String > classViewNames = new ArrayList< String >();
	
	public TaxonomyCompiled( String name )
	{
		super( name );
	}

	public String getExtendedTaxonomyName()
	{
		return extendedTaxonomyName;
	}

	public void setExtendedTaxonomyName( String extendedTaxonomyName )
	{
		this.extendedTaxonomyName = extendedTaxonomyName;
	}

	public List< String > getClassViewNames()
	{
		return classViewNames;
	}
}
