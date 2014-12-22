package ch.zhaw.inflection.operation;

public class DimensionViewFrame extends IdentifiableObjectFrame
{
	public DimensionViewFrame( InflectionViewPair inflectionViewPair, int positionCurrent, int positionMax, Object userData, int visitCount )
	{
		super( inflectionViewPair, positionCurrent, positionMax, userData, visitCount );
	}
	
	public DimensionViewPair getDimensionViewPair()
	{
		return (DimensionViewPair)getIdentifiableObjectPair();
	}

	public void setDimensionViewPair( DimensionViewPair dimensionViewPair )
	{
		setIdentifiableObjectPair( dimensionViewPair );
	}
}
