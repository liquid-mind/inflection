package ch.liquidmind.inflection.association;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Range;

public class Dimension
{
	private java.lang.Class< ? > targetCollectionOrMap;
	private List< Range< Integer > > ranges = new ArrayList< Range< Integer > >();
	private boolean isOrdered, isUnique;

	public boolean isOrdered()
	{
		return isOrdered;
	}

	void setOrdered( boolean isOrdered )
	{
		this.isOrdered = isOrdered;
	}

	public boolean isUnique()
	{
		return isUnique;
	}

	void setUnique( boolean isUnique )
	{
		this.isUnique = isUnique;
	}

	public List< Range< Integer > > getRanges()
	{
		return ranges;
	}

	public java.lang.Class< ? > getTargetCollectionOrMap()
	{
		return targetCollectionOrMap;
	}

	public void setTargetCollectionOrMap( java.lang.Class< ? > targetCollectionOrMap )
	{
		this.targetCollectionOrMap = targetCollectionOrMap;
	}
}
