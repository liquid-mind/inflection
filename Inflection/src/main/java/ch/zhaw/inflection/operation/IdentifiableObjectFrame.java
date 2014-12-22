package ch.zhaw.inflection.operation;

public abstract class IdentifiableObjectFrame extends InflectionViewFrame
{
	private int visitCount;

	public IdentifiableObjectFrame( InflectionViewPair inflectionViewPair, int positionCurrent, int positionMax, Object userData, int visitCount )
	{
		super( inflectionViewPair, positionCurrent, positionMax, userData );
		this.visitCount = visitCount;
	}

	public IdentifiableObjectPair getIdentifiableObjectPair()
	{
		return (IdentifiableObjectPair)getInflectionViewPair();
	}

	public void setIdentifiableObjectPair( IdentifiableObjectPair identifiableObjectPair )
	{
		setInflectionViewPair( identifiableObjectPair );
	}
	
	public int getVisitCount()
	{
		return visitCount;
	}

	public void setVisitCount( int visitCount )
	{
		this.visitCount = visitCount;
	}
}
