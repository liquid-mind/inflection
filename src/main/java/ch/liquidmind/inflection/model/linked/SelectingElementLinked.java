package ch.liquidmind.inflection.model.linked;

import java.util.List;

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
	
	public static int indexOfSelectingElementLinked( List< SelectingElementLinked > selectingElementsLinked, String name, SelectionType selectionType )
	{
		int foundIndex = -1;
		
		for ( int i = 0 ; i < selectingElementsLinked.size() ; ++i )
		{
			SelectingElementLinked selectingElementLinked = selectingElementsLinked.get( i );
			
			if ( ( name == null || selectingElementLinked.getName().equals( name ) ) && ( selectionType == null || selectingElementLinked.getSelectionType().equals( selectionType ) ) )
			{
				foundIndex = i;
				break;
			}
		}
		
		return foundIndex;
	}
	
}
