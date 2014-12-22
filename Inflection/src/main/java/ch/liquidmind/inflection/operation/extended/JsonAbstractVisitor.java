package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.model.MemberView;
import ch.liquidmind.inflection.model.Multiplicity;
import ch.liquidmind.inflection.operation.basic.IndentingPrintWriterVisitor;

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
