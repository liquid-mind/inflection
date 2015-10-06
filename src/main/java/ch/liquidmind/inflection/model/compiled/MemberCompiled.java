package ch.liquidmind.inflection.model.compiled;

import java.util.List;

public class MemberCompiled extends AliasableElementCompiled
{
	private static final long serialVersionUID = 1L;

	private AccessType accessType;
	private ViewCompiled parentViewCompiled;

	public MemberCompiled(
			List< AnnotationCompiled > annotationsCompiled,
			SelectionType selectionType,
			List< NameSelector > nameSelectors,
			String alias,
			AccessType accessType,
			List< NameSelector > memberSelectors,
			ViewCompiled parentViewCompiled )
	{
		super( annotationsCompiled, selectionType, nameSelectors, alias );
		this.accessType = accessType;
		getMemberSelectors().addAll( nameSelectors );
		this.parentViewCompiled = parentViewCompiled;
	}

	public AccessType getAccessType()
	{
		return accessType;
	}

	public void setAccessType( AccessType accessType )
	{
		this.accessType = accessType;
	}

	public List< NameSelector > getMemberSelectors()
	{
		return getNameSelectors();
	}

	public ViewCompiled getParentViewCompiled()
	{
		return parentViewCompiled;
	}

	public void setParentViewCompiled( ViewCompiled parentViewCompiled )
	{
		this.parentViewCompiled = parentViewCompiled;
	}
}
