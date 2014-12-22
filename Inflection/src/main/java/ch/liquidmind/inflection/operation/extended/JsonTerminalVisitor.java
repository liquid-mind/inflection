package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.MemberView;
import ch.liquidmind.inflection.operation.ClassViewFrame;
import ch.liquidmind.inflection.operation.MemberViewFrame;

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
