package ch.zhaw.inflection.operation;


public interface InflectionVisitor< TraverserType extends VisitingTraverser >
{
	public void setTraverser( TraverserType traverser );
	public TraverserType getTraverser();
	public void visit( ClassViewFrame frame );
	public void visit( MemberViewFrame frame );
	public void visit( DimensionViewFrame frame );
}
