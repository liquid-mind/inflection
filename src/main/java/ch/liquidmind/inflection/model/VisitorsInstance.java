package ch.liquidmind.inflection.model;

import java.util.HashMap;
import java.util.Map;

import ch.liquidmind.inflection.operation.InflectionVisitor;

public class VisitorsInstance
{
	private Visitors visitors;
	private Map< InflectionView, InflectionVisitor< ? > > mappings = new HashMap< InflectionView, InflectionVisitor< ? > >();
	private InflectionVisitor< ? > defaultVisitor;

	public VisitorsInstance( Visitors visitors )
	{
		super();
		this.visitors = visitors;
	}

	public Visitors getVisitors()
	{
		return visitors;
	}
	
	public void addViewToVisitorMapping( InflectionView view, InflectionVisitor< ? > visitor )
	{
		mappings.put( view, visitor );
	}
	
	public InflectionVisitor< ? > getVisitor( InflectionView view )
	{
		return mappings.get( view );
	}
	
	public InflectionVisitor< ? > getDefaultVisitor()
	{
		return defaultVisitor;
	}

	public void setDefaultVisitor( InflectionVisitor< ? > defaultVisitor )
	{
		this.defaultVisitor = defaultVisitor;
	}
}
