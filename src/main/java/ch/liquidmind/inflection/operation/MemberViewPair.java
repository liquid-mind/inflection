package ch.liquidmind.inflection.operation;

import ch.liquidmind.inflection.model.MemberView;

public class MemberViewPair extends InflectionViewPair
{
	public MemberViewPair(
			MemberView leftMemberView, MemberView rightMemberView,
			Integer leftPositionMax, Integer rightPositionMax,
			Integer leftPositionCurrent, Integer rightPositionCurrent )
	{
		super( leftMemberView, rightMemberView, leftPositionMax, rightPositionMax, leftPositionCurrent, rightPositionCurrent );
	}
	
	public MemberView getLeftMemberView()
	{
		return (MemberView)getLeftInflectionView();
	}

	public void setLeftMemberView( MemberView memberView )
	{
		setLeftInflectionView( memberView );
	}
	
	public MemberView getRightMemberView()
	{
		return (MemberView)getRightInflectionView();
	}

	public void setRightMemberView( MemberView memberView )
	{
		setRightInflectionView( memberView );
	}
	
	public MemberView getReferenceMemberView()
	{
		return (MemberView)getReferenceInflectionView();
	}
}
