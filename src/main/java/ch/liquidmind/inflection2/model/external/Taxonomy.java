package ch.liquidmind.inflection2.model.external;

import java.util.List;

public interface Taxonomy extends AnnotatableElement
{
	public String getName();
	public List< Taxonomy > getExtendedTaxonomies();
	public List< Taxonomy > getExtendingTaxonomies();
	public List< View > getViews();
}
