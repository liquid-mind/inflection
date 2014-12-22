package ch.liquidmind.inflection.operation;

public class MemberViewFrame extends InflectionViewFrame
{
	public MemberViewFrame( MemberViewPair memberViewPair, int positionCurrent, int positionMax, Object userData )
	{
		super( memberViewPair, positionCurrent, positionMax, userData );
	}
	
	public MemberViewPair getMemberViewPair()
	{
		return (MemberViewPair)getInflectionViewPair();
	}

	public void setMemberViewPair( MemberViewPair memberViewPair )
	{
		setInflectionViewPair( memberViewPair );
	}
}
