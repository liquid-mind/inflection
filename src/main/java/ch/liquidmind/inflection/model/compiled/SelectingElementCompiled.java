package ch.liquidmind.inflection.model.compiled;

import java.util.ArrayList;
import java.util.List;

public abstract class SelectingElementCompiled extends AnnotatableElementCompiled
{
	private static final long serialVersionUID = 1L;

	private SelectionType selectionType;
	private List< NameSelector > nameSelectors = new ArrayList< NameSelector >();
	
	public SelectingElementCompiled(
			List< AnnotationCompiled > annotationsCompiled,
			SelectionType selectionType,
			List< NameSelector > nameSelectors )
	{
		super( annotationsCompiled );
		this.selectionType = selectionType;
		this.nameSelectors = nameSelectors;
	}

	public SelectionType getSelectionType()
	{
		return selectionType;
	}

	public void setSelectionType( SelectionType selectionType )
	{
		this.selectionType = selectionType;
	}

	public List< NameSelector > getNameSelectors()
	{
		return nameSelectors;
	}
}
