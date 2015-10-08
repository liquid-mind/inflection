package ch.liquidmind.inflection.model.compiled;

import java.util.ArrayList;
import java.util.List;

public class ViewCompiled extends AliasableElementCompiled
{
	private static final long serialVersionUID = 1L;
	
	private List< String > usedClasses = new ArrayList< String >();
	private List< MemberCompiled > membersCompiled = new ArrayList< MemberCompiled >();
	private TaxonomyCompiled parentTaxonomyCompiled;
	
	public ViewCompiled( String name )
	{
		super( name );
	}

	public List< String > getUsedClasses()
	{
		return usedClasses;
	}

	public List< MemberCompiled > getMembersCompiled()
	{
		return membersCompiled;
	}

	public TaxonomyCompiled getParentTaxonomyCompiled()
	{
		return parentTaxonomyCompiled;
	}

	public void setParentTaxonomyCompiled( TaxonomyCompiled parentTaxonomyCompiled )
	{
		this.parentTaxonomyCompiled = parentTaxonomyCompiled;
	}
}
