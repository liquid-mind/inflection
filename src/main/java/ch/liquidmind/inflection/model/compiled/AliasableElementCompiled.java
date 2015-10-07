package ch.liquidmind.inflection.model.compiled;

import java.util.List;

public abstract class AliasableElementCompiled extends SelectingElementCompiled
{
	private static final long serialVersionUID = 1L;

	private String alias;

	public AliasableElementCompiled(
			List< AnnotationCompiled > annotationsCompiled,
			SelectionType selectionType,
			String alias )
	{
		super( annotationsCompiled, selectionType );
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
