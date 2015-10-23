package ch.liquidmind.inflection.model.external;

import ch.liquidmind.inflection.model.SelectionType;

public interface SelectingElement extends AnnotatableElement
{
	public SelectionType getSelectionType();
}
