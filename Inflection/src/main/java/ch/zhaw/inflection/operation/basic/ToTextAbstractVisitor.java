package ch.zhaw.inflection.operation.basic;

import ch.zhaw.inflection.model.MemberView;
import ch.zhaw.inflection.model.Multiplicity;

public abstract class ToTextAbstractVisitor extends IndentingPrintWriterVisitor< ToTextTraverser >
{
	protected String getMemberViewName( MemberView memberView )
	{
		String memberViewName;
		
		if ( memberView == null )
		{
			memberViewName = "root";
		}
		else
		{
			if ( memberView.getDimensionViews().get( 0 ).getMultiplicity().equals( Multiplicity.Many ) )
				memberViewName = "";
			else
				memberViewName = memberView.getName();
		}
		
		return memberViewName;
	}
}
