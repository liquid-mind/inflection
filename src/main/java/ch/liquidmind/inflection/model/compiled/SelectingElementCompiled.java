package ch.liquidmind.inflection.model.compiled;

public abstract class SelectingElementCompiled extends AnnotatableElementCompiled
{
	private static final long serialVersionUID = 1L;

	private SelectionType selectionType;

	public SelectingElementCompiled( String name )
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
