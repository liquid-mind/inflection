package ch.liquidmind.inflection.model.compiled;

import java.util.List;

public class MemberCompiled extends AliasableElementCompiled
{
	private static final long serialVersionUID = 1L;

	private AccessType accessType;
	private String viewedMember;
	private ViewCompiled parentViewCompiled;
	
	public MemberCompiled(
			List< AnnotationCompiled > annotationsCompiled,
			SelectionType selectionType,
			String alias,
			AccessType accessType,
			String viewedMember,
			ViewCompiled parentViewCompiled )
	{
		super( annotationsCompiled, selectionType, alias );
		this.accessType = accessType;
		this.viewedMember = viewedMember;
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

	public String getViewedMember()
	{
		return viewedMember;
	}

	public void setViewedMember( String viewedMember )
	{
		this.viewedMember = viewedMember;
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
