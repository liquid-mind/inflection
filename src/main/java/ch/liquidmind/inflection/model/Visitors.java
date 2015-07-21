package ch.liquidmind.inflection.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import __java.lang.__Class;
import ch.liquidmind.inflection.operation.InflectionVisitor;

// TODO Refactor mappings as List to preserve order of key/value pairs.
// TODO Refactor to reflect the many to many relationship between views
//      and visitors as defined in the grammar (VisitorsInstance can stay as
//      it is, since there speed is more important than clarity).
public class Visitors implements InflectionResource
{
	private String name;
	private Visitors extendedVisitors;
	private Map< InflectionView, Class< InflectionVisitor< ? > > > mappings = new HashMap< InflectionView, Class< InflectionVisitor< ? > > >();
	private Class< InflectionVisitor< ? > > defaultVisitorClass;
	
	public Visitors()
	{
		this( null );
	}

	public Visitors( String name )
	{
		this( name, null, null );
	}

	public Visitors( String name, Visitors extendedVisitors, Class< InflectionVisitor< ? > > defaultVisitorClass )
	{
		super();
		this.name = name;
		this.extendedVisitors = extendedVisitors;
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
	
	public Visitors getExtendedVisitors()
	{
		return extendedVisitors;
	}

	public void setExtendedVisitors( Visitors extendedVisitors )
	{
		this.extendedVisitors = extendedVisitors;
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
		
		if ( visitorClass == null && extendedVisitors != null)
			visitorClass = extendedVisitors.getVisitorClass( view );
		
		return visitorClass;
	}
	
	public Set< InflectionView > getDeclaredViews()
	{
		return mappings.keySet();
	}

	public Set< InflectionView > getViews()
	{
		Set< InflectionView > aggregateViews = new HashSet< InflectionView >();
		
		if ( extendedVisitors != null )
			aggregateViews.addAll( extendedVisitors.getViews() );
		
		// mappings may override entries from super-visitors.
		aggregateViews.addAll( getDeclaredViews() );
		
		return aggregateViews;
	}

	public Class< InflectionVisitor< ? > > getDefaultVisitorClass()
	{
		Class< InflectionVisitor< ? > > resultingClass = defaultVisitorClass;
		
		if ( resultingClass == null && extendedVisitors != null )
			resultingClass = extendedVisitors.getDefaultVisitorClass();

		return resultingClass;
	}

	public void setDefaultVisitorClass( Class< InflectionVisitor< ? > > defaultVisitorClass )
	{
		this.defaultVisitorClass = defaultVisitorClass;
	}

	public VisitorsInstance newInstance()
	{
		VisitorsInstance configInstance = new VisitorsInstance( this );
		
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
		String visitorsName = name;
		String extendsClause = ( extendedVisitors == null ? "" : TAB + "extends " + extendedVisitors.name + CARRIAGE_RETURN );

		String visitorsHeader = "visitors " + visitorsName + CARRIAGE_RETURN;
		visitorsHeader += extendsClause;
		
		String visitorsBody = "{" + CARRIAGE_RETURN;
		visitorsBody += visitorsBodyToString();
		visitorsBody += "}" + CARRIAGE_RETURN;
		
		String fullString = visitorsHeader + visitorsBody;

		return fullString;
	}

	private String visitorsBodyToString()
	{
		String visitorsBody = "";
		
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
			
			visitorsBody += TAB + viewName + " " + COLON + " " + visitorName + SEMICOLON + CARRIAGE_RETURN;
		}
		
		return visitorsBody;
	}
}
