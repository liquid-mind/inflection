package ch.zhaw.inflection.model;

import java.util.HashMap;
import java.util.Map;

import ch.zhaw.inflection.operation.InflectionVisitor;

public class VmapInstance
{
	private VMap vmap;
	private Map< InflectionView, InflectionVisitor< ? > > mappings = new HashMap< InflectionView, InflectionVisitor< ? > >();
	private InflectionVisitor< ? > defaultVisitor;

	public VmapInstance( VMap vmap )
	{
		super();
		this.vmap = vmap;
	}

	public VMap getVmap()
	{
		return vmap;
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
