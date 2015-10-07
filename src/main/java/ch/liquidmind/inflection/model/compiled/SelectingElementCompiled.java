package ch.liquidmind.inflection.model.compiled;

import java.util.List;

public abstract class SelectingElementCompiled extends AnnotatableElementCompiled
{
	private static final long serialVersionUID = 1L;

	private SelectionType selectionType;

	public SelectingElementCompiled( List< AnnotationCompiled > annotationsCompiled, SelectionType selectionType )
	{
		super( annotationsCompiled );
		this.selectionType = selectionType;
	}

	public SelectionType getSelectionType()
	{
		return selectionType;
	}

	public void setSelectionType( SelectionType selectionType )
	{
		this.selectionType = selectionType;
	}
}
