package ch.liquidmind.inflection.model.external;

import java.util.List;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
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
	public View getView( String nameOrAlias );
	public View getDeclaredView( String nameOrAlias );
	public View getUnresolvedView( String nameOrAlias );
	public View resolveView( Class< ? > aClass );
	public View resolveView( String className );
	public View getSuperview( View view );
	public List< Member > getMembers( View view );
	public Member getMember( View view, String nameOrAlias );
	public TaxonomyLoader getTaxonomyLoader();
	public String getPackageName();
	public String getSimpleName();
}
