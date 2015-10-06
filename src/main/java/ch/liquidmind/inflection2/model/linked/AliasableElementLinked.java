package ch.liquidmind.inflection2.model.linked;

import java.lang.annotation.Annotation;
import java.util.List;

import ch.liquidmind.inflection2.model.external.AliasableElement;

public abstract class AliasableElementLinked extends AnnotatableElementLinked implements AliasableElement
{
	private String alias;

	public AliasableElementLinked( List< Annotation > annotations, String alias )
	{
		super( annotations );
		this.alias = alias;
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias( String alias )
	{
		this.alias = alias;
	}
}
