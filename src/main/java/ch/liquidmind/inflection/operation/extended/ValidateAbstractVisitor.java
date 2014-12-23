package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.model.DimensionView;
import ch.liquidmind.inflection.operation.AbstractVisitor;
import ch.liquidmind.inflection.operation.CallStack;
import ch.liquidmind.inflection.operation.ClassViewFrame;
import ch.liquidmind.inflection.operation.DimensionViewFrame;
import ch.liquidmind.inflection.operation.DimensionViewPair;
import ch.liquidmind.inflection.operation.InflectionViewFrame;
import ch.liquidmind.inflection.operation.MemberViewFrame;

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
