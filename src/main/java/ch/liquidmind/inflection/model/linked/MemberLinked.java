package ch.liquidmind.inflection.model.linked;

import java.lang.annotation.Annotation;
import java.util.List;

import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.View;

public abstract class MemberLinked extends AliasableElementLinked implements Member
{
	private ViewLinked parentViewLinked;

	public MemberLinked( List< Annotation > annotations, String alias, ViewLinked parentViewLinked )
	{
		super( annotations, alias );
		this.parentViewLinked = parentViewLinked;
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
