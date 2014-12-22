package ch.liquidmind.inflection.operation;

public class ClassViewFrame extends IdentifiableObjectFrame
{
	public ClassViewFrame( InflectionViewPair inflectionViewPair, int positionCurrent, int positionMax, Object userData, int visitCount )
	{
		super( inflectionViewPair, positionCurrent, positionMax, userData, visitCount );
	}
	
	public ClassViewPair getClassViewPair()
	{
		return (ClassViewPair)getIdentifiableObjectPair();
	}

	public void setClassViewPair( ClassViewPair classViewPair )
	{
		setIdentifiableObjectPair( classViewPair );
	}
}
