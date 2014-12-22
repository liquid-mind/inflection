package ch.zhaw.inflection.operation.basic;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.model.MemberView;
import ch.zhaw.inflection.model.Multiplicity;
import ch.zhaw.inflection.operation.ClassViewFrame;
import ch.zhaw.inflection.operation.DimensionViewFrame;
import ch.zhaw.inflection.operation.MemberViewFrame;

public class ToTextDefaultVisitor extends ToTextAbstractVisitor
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		IdentifiableObject< ?, ? > iObject = frame.getClassViewPair().getLeftObject();
		Object objectId = iObject.getObjectId();
		MemberViewFrame lastMemberViewFrame = getTraverser().getLastMemberViewFrame();
		MemberView lastMemberView = ( lastMemberViewFrame == null ? null : lastMemberViewFrame.getMemberViewPair().getLeftMemberView() );
		String memberViewName = getMemberViewName( lastMemberView );
		memberViewName = ( memberViewName.isEmpty() ? "" : memberViewName + " " );

		if ( frame.getVisitCount() == 0 )
		{
			if ( getTraverser().getMultiplyTraversedObjects().contains( iObject ) )
			{
				openBraces();
				println( memberViewName + "@id=" + objectId + "" );
				getTraverser().continueTraversal();
				closeBraces();
			}
			else
			{
				if ( !memberViewName.isEmpty() )
					println( memberViewName );
				
				openBraces();
				getTraverser().continueTraversal();
				closeBraces();
			}
		}
		else
		{
			println( memberViewName + "@ref=" + objectId + "" );
		}
	}

	@Override
	public void visit( MemberViewFrame frame )
	{
		MemberView memberView = frame.getMemberViewPair().getLeftMemberView();

		if ( memberView.getDimensionViews().get( 0 ).getMultiplicity().equals( Multiplicity.Many ) )
			println( memberView.getName() );

		getTraverser().continueTraversal();
	}

	@Override
	public void visit( DimensionViewFrame frame )
	{
		if ( frame.getDimensionViewPair().getLeftDimensionView().getMultiplicity().equals( Multiplicity.Many ) )
		{
			openBraces();
			getTraverser().continueTraversal();
			closeBraces();
		}
		else
		{
			getTraverser().continueTraversal();
		}
	}

	protected void openBraces()
	{
		println( "{" );
		increaseIndent();
	}
	
	protected void closeBraces()
	{
		decreaseIndent();
		println( "}" );
	}
}
