package ch.zhaw.inflection.operation.extended;

import ch.zhaw.inflection.model.DimensionView;
import ch.zhaw.inflection.operation.AbstractVisitor;
import ch.zhaw.inflection.operation.CallStack;
import ch.zhaw.inflection.operation.ClassViewFrame;
import ch.zhaw.inflection.operation.DimensionViewFrame;
import ch.zhaw.inflection.operation.DimensionViewPair;
import ch.zhaw.inflection.operation.InflectionViewFrame;
import ch.zhaw.inflection.operation.MemberViewFrame;

public class ValidateAbstractVisitor extends AbstractVisitor< ValidateTraverser >
{
	public String getLocation()
	{
		String location = "";
		CallStack callstack = getTraverser().getCallStack();
		
		for ( int i = 0 ; i < callstack.size() ; ++i )
		{
			InflectionViewFrame frame = callstack.get( i );
			
			if ( i == 0 )
			{
				location += ((ClassViewFrame)frame).getClassViewPair().getLeftClassView().getName();
			}
			else if ( frame instanceof MemberViewFrame )
			{
				location += "->" + ((MemberViewFrame)frame).getMemberViewPair().getLeftMemberView().getName();
			}
			else if ( frame instanceof DimensionViewFrame )
			{
				DimensionViewPair pair = ((DimensionViewFrame)frame).getDimensionViewPair();
				DimensionView view = pair.getLeftDimensionView();
				Object index;
				
				if ( view.isOrdered() || view.isMapped() )
					index = pair.getLeftIndex();
				else
					index = "?";
					
				location += "[" + index + "]";
			}
		}
		
		return location;
	}
}
