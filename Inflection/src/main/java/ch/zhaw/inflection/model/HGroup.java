package ch.zhaw.inflection.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO Rename HGroup to VGroup or VContext (or Taxonomy)
public class HGroup implements InflectionResource
{
	private String name;
	private HGroup extendedHGroup;
	private List< ClassView< ? > > classViews = new ArrayList< ClassView< ? > >();
	
	public HGroup()
	{
		this( null );
	}

	public HGroup( String name )
	{
		this( name, null );
	}

	public HGroup( String name, HGroup extendedHGroup )
	{
		super();
		this.name = name;
		this.extendedHGroup = extendedHGroup;
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

	public HGroup getExtendedHGroup()
	{
		return extendedHGroup;
	}

	public void setExtendedHGroup( HGroup extendedHGroup )
	{
		this.extendedHGroup = extendedHGroup;
	}

	public List< ClassView< ? > > getDeclaredClassViews()
	{
		return classViews;
	}

	public List< ClassView< ? > > getClassViews()
	{
		List< ClassView< ? > > allClassViews = new ArrayList< ClassView< ? > >();
		
		if ( extendedHGroup != null )
			allClassViews.addAll( extendedHGroup.getClassViews() );
		
		// mappings may override entries from super configuration.
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
		String hgroupName = name;
		String extendsClause = ( extendedHGroup == null ? "" : TAB + "extends " + extendedHGroup.name + CARRIAGE_RETURN );
		
		String hgroupHeader = "hgroup " + hgroupName + CARRIAGE_RETURN;
		hgroupHeader += extendsClause;
		
		String hgroupBody = "{" + CARRIAGE_RETURN;
		hgroupBody += hgroupBodyToString();
		hgroupBody += "}" + CARRIAGE_RETURN;
		
		String fullString = hgroupHeader + hgroupBody;
		
		return fullString;
	}
	
	private String hgroupBodyToString()
	{
		String hgroupBody = "";
		List< ClassView< ? > > classViews = getClassViews();
		
		for ( int i = 0 ; i < classViews.size() ; ++i )
		{
			hgroupBody += TAB + classViews.get( i ).getName();
			
			if ( i + 1 < classViews.size() )
				hgroupBody += COMMA;
			
			hgroupBody += CARRIAGE_RETURN;
		}
		
		return hgroupBody;
	}
}
