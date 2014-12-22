package ch.zhaw.inflection.operation;

import ch.zhaw.inflection.model.HGroup;

public abstract class VisitingTraverser extends AbstractTraverser
{
	private InflectionVisitor< VisitingTraverser > visitor;
	
	public VisitingTraverser( HGroup hGroup, InflectionVisitor< VisitingTraverser > visitor )
	{
		super( hGroup );
		this.visitor = visitor;
	}

	@Override
	protected void traverse( ClassViewFrame frame )
	{
		visitor.visit( frame );
	}

	@Override
	protected void traverse( MemberViewFrame frame )
	{
		visitor.visit( frame );
	}

	@Override
	protected void traverse( DimensionViewFrame frame )
	{
		visitor.visit( frame );
	}

	public InflectionVisitor< VisitingTraverser > getVisitor()
	{
		return visitor;
	}

	public void setVisitor( InflectionVisitor< VisitingTraverser > visitor )
	{
		this.visitor = visitor;
		visitor.setTraverser( this );
	}
}
