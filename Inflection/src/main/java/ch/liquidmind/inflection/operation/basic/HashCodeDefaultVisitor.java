package ch.liquidmind.inflection.operation.basic;

import ch.liquidmind.inflection.operation.ClassViewFrame;
import ch.liquidmind.inflection.operation.DimensionViewFrame;
import ch.liquidmind.inflection.operation.MemberViewFrame;
import ch.liquidmind.inflection.operation.basic.HashCodeTraverser.HashCodes;

public class HashCodeDefaultVisitor extends HashCodeAbstractVisitor
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		getTraverser().continueTraversal();
		passThroughOrdered();
	}

	@Override
	public void visit( MemberViewFrame frame )
	{
		getTraverser().continueTraversal();
		passThroughOrdered();
	}

	@Override
	public void visit( DimensionViewFrame frame )
	{
		getTraverser().continueTraversal();

		if ( frame.getDimensionViewPair().getLeftDimensionView().isOrdered() )
			passThroughOrdered();
		else
			passThroughUnordered();
	}
	
	protected void passThroughOrdered()
	{
		setHashCode( getHashCodes().getOrderedSum() );
	}

	protected void passThroughUnordered()
	{
		setHashCode( getHashCodes().getUnorderedSum() );
	}

	private HashCodes getHashCodes()
	{
		return (HashCodes)getTraverser().getCurrentFrame().getUserData();
	}
}
