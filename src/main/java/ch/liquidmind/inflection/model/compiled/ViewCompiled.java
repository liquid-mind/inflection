package ch.liquidmind.inflection.model.compiled;

import java.util.ArrayList;
import java.util.List;

public class ViewCompiled extends AliasableElementCompiled
{
	private static final long serialVersionUID = 1L;
	
	private List< NameSelector > usedClassesSelectors = new ArrayList< NameSelector >();
	private List< MemberCompiled > membersCompiled = new ArrayList< MemberCompiled >();
	private TaxonomyCompiled parentTaxonomyCompiled;

	public ViewCompiled(
			List< AnnotationCompiled > annotationsCompiled,
			SelectionType selectionType,
			List< NameSelector > nameSelectors,
			String alias,
			List< NameSelector > classesSelectors,
			List< NameSelector > usedClassesSelectors,
			List< MemberCompiled > membersCompiled,
			TaxonomyCompiled parentTaxonomyCompiled )
	{
		super( annotationsCompiled, selectionType, nameSelectors, alias );
		getNameSelectors().addAll( nameSelectors );
		this.usedClassesSelectors = usedClassesSelectors;
		this.membersCompiled = membersCompiled;
		this.parentTaxonomyCompiled = parentTaxonomyCompiled;
	}

	public List< NameSelector > getClassesSelectors()
	{
		return getNameSelectors();
	}

	public List< NameSelector > getUsedClassesSelectors()
	{
		return usedClassesSelectors;
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
