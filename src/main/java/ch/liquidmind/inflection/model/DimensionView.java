package ch.liquidmind.inflection.model;

import java.lang.reflect.Type;

public class DimensionView extends InflectionView
{
	private boolean isOrdered;
	private boolean isMapped;
	private boolean isStatic;
	private Multiplicity multiplicity;
	private Type dimensionType;
	
	public DimensionView(
			boolean isOrdered,
			boolean isMapped,
			boolean isStatic,
			Multiplicity multiplicity,
			Type dimensionType,
			MemberView owningMemberView )
	{
		super();
		this.isOrdered = isOrdered;
		this.isMapped = isMapped;
		this.isStatic = isStatic;
		this.multiplicity = multiplicity;
		this.dimensionType = dimensionType;
		setOwningMemberView( owningMemberView );
	}

	@Override
	public String getName()
	{
		// DimensionViews do not have names.
		return null;
	}

	public boolean isOrdered()
	{
		return isOrdered;
	}

	public void setOrdered( boolean isOrdered )
	{
		this.isOrdered = isOrdered;
	}

	public boolean isMapped()
	{
		return isMapped;
	}

	public void setMapped( boolean isMapped )
	{
		this.isMapped = isMapped;
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	public void setStatic( boolean isStatic )
	{
		this.isStatic = isStatic;
	}

	public Multiplicity getMultiplicity()
	{
		return multiplicity;
	}

	public void setMultiplicity( Multiplicity multiplicity )
	{
		this.multiplicity = multiplicity;
	}

	public Type getDimensionType()
	{
		return dimensionType;
	}

	public void setDimensionType( Type dimensionType )
	{
		this.dimensionType = dimensionType;
	}

	public MemberView getOwningMemberView()
	{
		return (MemberView)getParentView();
	}

	public void setOwningMemberView( MemberView owningMemberView )
	{
		setParentView( owningMemberView );
	}
}
