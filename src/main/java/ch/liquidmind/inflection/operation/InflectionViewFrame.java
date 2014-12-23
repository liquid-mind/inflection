package ch.liquidmind.inflection.operation;

public abstract class InflectionViewFrame
{
	private InflectionViewPair inflectionViewPair;
	private int positionCurrent;
	private int positionMax;
	private Object userData;
	
	public InflectionViewFrame( InflectionViewPair inflectionViewPair, int positionCurrent, int positionMax, Object userData )
	{
		super();
		this.inflectionViewPair = inflectionViewPair;
		this.positionCurrent = positionCurrent;
		this.positionMax = positionMax;
		this.userData = userData;
	}

	public InflectionViewPair getInflectionViewPair()
	{
		return inflectionViewPair;
	}

	public void setInflectionViewPair( InflectionViewPair inflectionViewPair )
	{
		this.inflectionViewPair = inflectionViewPair;
	}

	public int getPositionCurrent()
	{
		return positionCurrent;
	}

	public void setPositionCurrent( int positionCurrent )
	{
		this.positionCurrent = positionCurrent;
	}

	public int getPositionMax()
	{
		return positionMax;
	}

	public void setPositionMax( int positionMax )
	{
		this.positionMax = positionMax;
	}

	public Object getUserData()
	{
		return userData;
	}

	public void setUserData( Object userData )
	{
		this.userData = userData;
	}
}
