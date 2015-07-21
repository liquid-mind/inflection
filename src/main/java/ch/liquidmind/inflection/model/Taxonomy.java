package ch.liquidmind.inflection.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO Rename Taxonomy to VGroup or VContext (or Taxonomy)
public class Taxonomy implements InflectionResource
{
	private String name;
	private Taxonomy extendedTaxonomy;
	private List< ClassView< ? > > classViews = new ArrayList< ClassView< ? > >();
	
	public Taxonomy()
	{
		this( null );
	}

	public Taxonomy( String name )
	{
		this( name, null );
	}

	public Taxonomy( String name, Taxonomy extendedTaxonomy )
	{
		super();
		this.name = name;
		this.extendedTaxonomy = extendedTaxonomy;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName( String name )
	{
		this.name = name;
	}

	public Taxonomy getExtendedTaxonomy()
	{
		return extendedTaxonomy;
	}

	public void setExtendedTaxonomy( Taxonomy extendedTaxonomy )
	{
		this.extendedTaxonomy = extendedTaxonomy;
	}

	public List< ClassView< ? > > getDeclaredClassViews()
	{
		return classViews;
	}

	public List< ClassView< ? > > getClassViews()
	{
		List< ClassView< ? > > allClassViews = new ArrayList< ClassView< ? > >();
		
		if ( extendedTaxonomy != null )
			allClassViews.addAll( extendedTaxonomy.getClassViews() );
		
		// mappings may override entries from super-taxonomy.
		allClassViews.removeAll( getDeclaredClassViews() );
		allClassViews.addAll( getDeclaredClassViews() );
		
		return allClassViews;
	}
	
	// TODO Optimize by setting up the map once rather than each time
	// this method is called.
	public ClassView< ? > getClassView( Class< ? > viewableClass )
	{
		Map< Class< ? >, ClassView< ? > > classViewMap = new HashMap< Class< ? >, ClassView< ? > >();
		
		for ( ClassView< ? > currentClassView : getClassViews() )
			classViewMap.put( currentClassView.getJavaClass(), currentClassView );
		
		ClassView< ? > foundClassView = null;
		Class< ? > currentViewableClass = viewableClass;
		
		while ( currentViewableClass != null )
		{
			foundClassView = classViewMap.get( currentViewableClass );
			
			if ( foundClassView != null )
				break;
			
			currentViewableClass = currentViewableClass.getSuperclass();
		}

		return foundClassView;
	}
	
	@Override
	public String toString()
	{
		String taxonomyName = name;
		String extendsClause = ( extendedTaxonomy == null ? "" : TAB + "extends " + extendedTaxonomy.name + CARRIAGE_RETURN );
		
		String taxonomyHeader = "taxonomy " + taxonomyName + CARRIAGE_RETURN;
		taxonomyHeader += extendsClause;
		
		String taxonomyBody = "{" + CARRIAGE_RETURN;
		taxonomyBody += taxonomyBodyToString();
		taxonomyBody += "}" + CARRIAGE_RETURN;
		
		String fullString = taxonomyHeader + taxonomyBody;
		
		return fullString;
	}
	
	private String taxonomyBodyToString()
	{
		String taxonomyBody = "";
		List< ClassView< ? > > classViews = getClassViews();
		
		for ( int i = 0 ; i < classViews.size() ; ++i )
		{
			taxonomyBody += TAB + classViews.get( i ).getName();
			
			if ( i + 1 < classViews.size() )
				taxonomyBody += COMMA;
			
			taxonomyBody += CARRIAGE_RETURN;
		}
		
		return taxonomyBody;
	}
}
