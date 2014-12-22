package ch.liquidmind.inflection.model;

import java.util.ArrayList;
import java.util.List;

public abstract class InflectionView
{
	private String name;
	private InflectionView parentView;
	private List< InflectionView > childViews = new ArrayList< InflectionView >();

	public InflectionView()
	{
		super();
	}

	public InflectionView( String name )
	{
		this( name, null );
	}

	public InflectionView( String name, InflectionView parentView )
	{
		super();
		this.parentView = parentView;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}

	public InflectionView getParentView()
	{
		return parentView;
	}

	public void setParentView( InflectionView parentView )
	{
		this.parentView = parentView;
	}

	public List< InflectionView > getChildViews()
	{
		return childViews;
	}
}
