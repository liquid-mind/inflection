package ch.liquidmind.inflection.model.compiled;

import ch.liquidmind.inflection.model.AccessType;

public class MemberCompiled extends AliasableElementCompiled
{
	private static final long serialVersionUID = 1L;

	private AccessType accessType;
	private ViewCompiled parentViewCompiled;
	
	public MemberCompiled( String name, ViewCompiled parentViewCompiled )
	{
		super( name );
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

	public ViewCompiled getParentViewCompiled()
	{
		return parentViewCompiled;
	}

	public void setParentViewCompiled( ViewCompiled parentViewCompiled )
	{
		this.parentViewCompiled = parentViewCompiled;
	}
}
