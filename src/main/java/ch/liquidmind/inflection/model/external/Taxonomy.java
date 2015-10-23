package ch.liquidmind.inflection.model.external;

import java.util.List;

import ch.liquidmind.inflection.model.AccessType;

public interface Taxonomy extends AnnotatableElement
{
	public AccessType getDefaultAccessType();
	public AccessType getDeclaredDefaultAccessType();
	public List< Taxonomy > getExtendedTaxonomies();
	public List< Taxonomy > getExtendingTaxonomies();
	public List< View > getViews();
	public List< View > getDeclaredViews();
	public List< View > getUnresolvedViews();
	public View getView( String name );
	public View getDeclaredView( String name );
	public View getUnresolvedView( String name );
	public View resolveView( Class< ? > aClass );
	public View resolveView( String className );
}
