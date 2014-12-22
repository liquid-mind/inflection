package ch.zhaw.inflection.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import __java.lang.__Class;
import ch.zhaw.inflection.operation.InflectionVisitor;

// TODO Refactor mappings as List to preserve order of key/value pairs.
// TODO Refactor to reflect the many to many relationship between views
//      and visitors as defined in the grammar (VMapInstance can stay as
//      it is, since there speed is more important than clarity).
public class VMap implements InflectionResource
{
	private String name;
	private VMap extendedVMap;
	private Map< InflectionView, Class< InflectionVisitor< ? > > > mappings = new HashMap< InflectionView, Class< InflectionVisitor< ? > > >();
	private Class< InflectionVisitor< ? > > defaultVisitorClass;
	
	public VMap()
	{
		this( null );
	}

	public VMap( String name )
	{
		this( name, null, null );
	}

	public VMap( String name, VMap extendedVMap, Class< InflectionVisitor< ? > > defaultVisitorClass )
	{
		super();
		this.name = name;
		this.extendedVMap = extendedVMap;
		this.defaultVisitorClass = defaultVisitorClass;
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
	
	public VMap getExtendedVMap()
	{
		return extendedVMap;
	}

	public void setExtendedVMap( VMap extendedVMap )
	{
		this.extendedVMap = extendedVMap;
	}
	
	@SuppressWarnings( "unchecked" )
	public void addViewToVisitorClassMapping( InflectionView view, Class< ? > visitorClass )
	{
		mappings.put( view, (Class< InflectionVisitor< ? > >)visitorClass );
	}
	
	public Class< InflectionVisitor< ? > > getDeclaredVisitorClass( InflectionView view )
	{
		return mappings.get( view );
	}
	
	public Class< InflectionVisitor< ? > > getVisitorClass( InflectionView view )
	{
		Class< InflectionVisitor< ? > > visitorClass = getDeclaredVisitorClass( view );
		
		if ( visitorClass == null && extendedVMap != null)
			visitorClass = extendedVMap.getVisitorClass( view );
		
		return visitorClass;
	}
	
	public Set< InflectionView > getDeclaredViews()
	{
		return mappings.keySet();
	}

	public Set< InflectionView > getViews()
	{
		Set< InflectionView > aggregateViews = new HashSet< InflectionView >();
		
		if ( extendedVMap != null )
			aggregateViews.addAll( extendedVMap.getViews() );
		
		// mappings may override entries from super configuration.
		aggregateViews.addAll( getDeclaredViews() );
		
		return aggregateViews;
	}

	public Class< InflectionVisitor< ? > > getDefaultVisitorClass()
	{
		Class< InflectionVisitor< ? > > resultingClass = defaultVisitorClass;
		
		if ( resultingClass == null && extendedVMap != null )
			resultingClass = extendedVMap.getDefaultVisitorClass();

		return resultingClass;
	}

	public void setDefaultVisitorClass( Class< InflectionVisitor< ? > > defaultVisitorClass )
	{
		this.defaultVisitorClass = defaultVisitorClass;
	}

	public VmapInstance newInstance()
	{
		VmapInstance configInstance = new VmapInstance( this );
		
		for ( InflectionView view : getViews() )
		{
			Class< InflectionVisitor< ? > > visitorClass = getVisitorClass( view );
			InflectionVisitor< ? > visitor = __Class.newInstance( visitorClass );
			configInstance.addViewToVisitorMapping( view, visitor );
		}
		
		InflectionVisitor< ? > visitor = __Class.newInstance( getDefaultVisitorClass() );
		configInstance.setDefaultVisitor( visitor );
		
		return configInstance;
	}

	@Override
	public String toString()
	{
		String vmapName = name;
		String extendsClause = ( extendedVMap == null ? "" : TAB + "extends " + extendedVMap.name + CARRIAGE_RETURN );

		String vmapHeader = "vmap " + vmapName + CARRIAGE_RETURN;
		vmapHeader += extendsClause;
		
		String vmapBody = "{" + CARRIAGE_RETURN;
		vmapBody += vmapBodyToString();
		vmapBody += "}" + CARRIAGE_RETURN;
		
		String fullString = vmapHeader + vmapBody;

		return fullString;
	}

	private String vmapBodyToString()
	{
		String vmapBody = "";
		
		for ( InflectionView view : getViews() )
		{
			String viewName;
			
			if ( view instanceof ClassView )
			{
				viewName = view.getName();
			}
			else if( view instanceof MemberView )
			{
				MemberView memberView = (MemberView)view;
				viewName = memberView.getOwningClassView().getName() + "->" + memberView.getName();
			}
			else
			{
				throw new IllegalStateException( "Unexpected type for view: " + view.getClass().getName() );
			}
			
			String visitorName = getVisitorClass( view ).getName();
			
			vmapBody += TAB + viewName + " " + COLON + " " + visitorName + SEMICOLON + CARRIAGE_RETURN;
		}
		
		return vmapBody;
	}
}
