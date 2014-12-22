package ch.zhaw.inflection.operation.extended;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.model.MemberView;
import ch.zhaw.inflection.operation.ClassViewFrame;
import ch.zhaw.inflection.operation.MemberViewFrame;

public abstract class JsonTerminalVisitor extends JsonAbstractVisitor
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		IdentifiableObject< ?, ? > iObject = frame.getClassViewPair().getLeftObject();
		MemberViewFrame lastMemberViewFrame = getTraverser().getLastMemberViewFrame();
		MemberView lastMemberView = ( lastMemberViewFrame == null ? null : lastMemberViewFrame.getMemberViewPair().getLeftMemberView() );
		String memberViewName = "\"" + getMemberViewName( lastMemberView ) + "\"";
		String memberValueRaw = iObject.getObject().toString();
		String memberValue = "\"" + memberValueRaw + "\"";
		print( memberViewName + " : " + memberValue );
		getTraverser().continueTraversal();
	}
	
	protected abstract String getMemberValue( IdentifiableObject< ?, ? > iObject );
}
