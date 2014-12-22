package ch.zhaw.inflection.operation.extended;

import ch.zhaw.inflection.model.MemberView;
import ch.zhaw.inflection.model.Multiplicity;
import ch.zhaw.inflection.operation.basic.IndentingPrintWriterVisitor;

public abstract class JsonAbstractVisitor extends IndentingPrintWriterVisitor< JsonTraverser >
{
	protected String getMemberViewName( MemberView memberView )
	{
		String memberViewName;
		
		if ( memberView == null )
		{
			memberViewName = "";
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
