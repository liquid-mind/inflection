package ch.liquidmind.inflection.model.compiled;

import java.util.ArrayList;
import java.util.List;

public class ViewCompiled extends AliasableElementCompiled
{
	private static final long serialVersionUID = 1L;
	
	private String usedClass;
	private List< MemberCompiled > membersCompiled = new ArrayList< MemberCompiled >();
	private TaxonomyCompiled parentTaxonomyCompiled;
	
	public ViewCompiled( String name, TaxonomyCompiled parentTaxonomyCompiled )
	{
		super( name );
		this.parentTaxonomyCompiled = parentTaxonomyCompiled;
	}
	
	public String getUsedClass()
	{
		return usedClass;
	}

	public void setUsedClass( String usedClass )
	{
		this.usedClass = usedClass;
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
