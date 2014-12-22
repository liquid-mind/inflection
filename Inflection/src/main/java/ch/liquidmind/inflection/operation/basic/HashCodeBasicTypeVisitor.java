package ch.liquidmind.inflection.operation.basic;

import ch.liquidmind.inflection.operation.ClassViewFrame;

// TODO Need to re-term "basic types" to "terminal types".
public class HashCodeBasicTypeVisitor extends HashCodeAbstractVisitor
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		getTraverser().continueTraversal();
		setHashCode( frame.getClassViewPair().getLeftObject().getObject().hashCode() );
	}
}
