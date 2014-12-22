package ch.liquidmind.inflection.operation.basic;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.MemberView;
import ch.liquidmind.inflection.operation.ClassViewFrame;
import ch.liquidmind.inflection.operation.MemberViewFrame;

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
