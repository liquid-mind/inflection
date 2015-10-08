package ch.liquidmind.inflection.model.linked;

import ch.liquidmind.inflection.model.SelectionType;

public class SelectingElementLinked extends AnnotatableElementLinked
{
	private SelectionType selectionType;
	
	public SelectingElementLinked( String name )
	{
		super( name );
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
