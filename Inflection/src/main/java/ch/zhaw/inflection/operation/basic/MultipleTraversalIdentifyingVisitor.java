package ch.zhaw.inflection.operation.basic;

import ch.zhaw.inflection.operation.AbstractVisitor;
import ch.zhaw.inflection.operation.ClassViewFrame;

public class MultipleTraversalIdentifyingVisitor extends AbstractVisitor< MultipleTraversalIdentifyingTraverser >
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		if ( frame.getVisitCount() == 1 )
		{
			MultipleTraversalIdentifyingTraverser traverser = getTraverser();
			traverser.getMultiplyTraversedObjects().add( frame.getClassViewPair().getLeftObject() );
		}
		
		getTraverser().continueTraversal();
	}
}
