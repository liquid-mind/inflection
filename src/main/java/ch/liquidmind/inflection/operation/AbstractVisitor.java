package ch.liquidmind.inflection.operation;


public class AbstractVisitor< TraverserType extends VisitingTraverser > implements InflectionVisitor< TraverserType >
{
	private TraverserType traverser;

	@Override
	public void setTraverser( TraverserType traverser )
	{
		this.traverser = traverser;
	}

	@Override
	public TraverserType getTraverser()
	{
		return traverser;
	}

	@Override
	public void visit( ClassViewFrame frame )
	{
		traverser.continueTraversal();
	}

	@Override
	public void visit( MemberViewFrame frame )
	{
		traverser.continueTraversal();
	}

	@Override
	public void visit( DimensionViewFrame frame )
	{
		traverser.continueTraversal();
	}
}
