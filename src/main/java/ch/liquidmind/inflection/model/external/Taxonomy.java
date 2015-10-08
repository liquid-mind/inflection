package ch.liquidmind.inflection.model.external;

import java.util.List;

import ch.liquidmind.inflection.model.AccessType;

public interface Taxonomy extends AnnotatableElement
{
	public String getName();
	public AccessType getDefaultAccessType();
	public List< Taxonomy > getExtendedTaxonomies();
	public List< Taxonomy > getExtendingTaxonomies();
	public List< View > getViews();
}
