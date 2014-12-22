package ch.zhaw.inflection.operation.basic;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.model.MemberView;
import ch.zhaw.inflection.operation.ClassViewFrame;
import ch.zhaw.inflection.operation.MemberViewFrame;

public class ToTextBasicTypeVisitor extends ToTextAbstractVisitor
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		IdentifiableObject< ?, ? > iObject = frame.getClassViewPair().getLeftObject();
		MemberViewFrame lastMemberViewFrame = getTraverser().getLastMemberViewFrame();
		MemberView lastMemberView = ( lastMemberViewFrame == null ? null : lastMemberViewFrame.getMemberViewPair().getLeftMemberView() );
		String memberViewName = getMemberViewName( lastMemberView );
		println( memberViewName + ": " + iObject.getObject().toString() );
	}
}
