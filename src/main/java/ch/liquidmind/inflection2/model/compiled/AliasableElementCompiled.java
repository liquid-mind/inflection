package ch.liquidmind.inflection2.model.compiled;

import java.util.List;

public abstract class AliasableElementCompiled extends SelectingElementCompiled
{
	private static final long serialVersionUID = 1L;

	private String alias;

	public AliasableElementCompiled(
			List< AnnotationCompiled > annotationsCompiled,
			SelectionType selectionType,
			List< NameSelector > nameSelectors,
			String alias )
	{
		super( annotationsCompiled, selectionType, nameSelectors );
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
