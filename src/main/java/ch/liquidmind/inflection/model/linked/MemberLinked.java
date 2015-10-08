package ch.liquidmind.inflection.model.linked;

import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.View;

public abstract class MemberLinked extends AliasableElementLinked implements Member
{
	private ViewLinked parentViewLinked;

	public MemberLinked( String name )
	{
		super( name );
	}

	public ViewLinked getParentViewLinked()
	{
		return parentViewLinked;
	}

	public void setParentViewLinked( ViewLinked parentViewLinked )
	{
		this.parentViewLinked = parentViewLinked;
	}

	@Override
	public View getParentView()
	{
		return getParentViewLinked();
	}
}
