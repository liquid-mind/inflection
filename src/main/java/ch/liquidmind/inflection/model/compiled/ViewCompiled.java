package ch.liquidmind.inflection.model.compiled;

import java.util.ArrayList;
import java.util.List;

public class ViewCompiled extends AliasableElementCompiled
{
	private static final long serialVersionUID = 1L;
	
	private String viewedClass;
	private List< String > usedClasses = new ArrayList< String >();
	private List< MemberCompiled > membersCompiled = new ArrayList< MemberCompiled >();
	private TaxonomyCompiled parentTaxonomyCompiled;
	
	public ViewCompiled(
			List< AnnotationCompiled > annotationsCompiled,
			SelectionType selectionType,
			String alias,
			String viewedClass,
			List< String > usedClasses,
			List< MemberCompiled > membersCompiled,
			TaxonomyCompiled parentTaxonomyCompiled )
	{
		super( annotationsCompiled, selectionType, alias );
		this.viewedClass = viewedClass;
		this.usedClasses = usedClasses;
		this.membersCompiled = membersCompiled;
		this.parentTaxonomyCompiled = parentTaxonomyCompiled;
	}

	public String getViewedClass()
	{
		return viewedClass;
	}

	public void setViewedClass( String viewedClass )
	{
		this.viewedClass = viewedClass;
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
