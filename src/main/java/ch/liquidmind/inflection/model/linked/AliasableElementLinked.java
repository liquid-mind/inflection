package ch.liquidmind.inflection.model.linked;

import ch.liquidmind.inflection.model.external.AliasableElement;

public abstract class AliasableElementLinked extends AnnotatableElementLinked implements AliasableElement
{
	private String alias;

	public AliasableElementLinked( String name )
	{
		super( name );
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
