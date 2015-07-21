package ch.liquidmind.inflection.operation;

import ch.liquidmind.inflection.InflectionResourceLoader;
import ch.liquidmind.inflection.model.ClassView;
import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.MemberView;
import ch.liquidmind.inflection.model.Visitors;
import ch.liquidmind.inflection.model.VisitorsInstance;

public abstract class ConfigurableVisitingTraverser extends VisitingTraverser
{
	public static final String VISITORS_SUFFIX = "Visitors";
	
	private VisitorsInstance visitorsInstance;
	
	public ConfigurableVisitingTraverser( Taxonomy taxonomy, VisitorsInstance visitorsInstance )
	{
		super( taxonomy, null );
		this.visitorsInstance = visitorsInstance;
	}

	@Override
	protected void traverse( ClassViewFrame frame )
	{
		MemberViewFrame lastMemberViewFrame = getLastMemberViewFrame();
		MemberView lastMemberView = ( lastMemberViewFrame == null ? null : lastMemberViewFrame.getMemberViewPair().getReferenceMemberView() );
		setVisitor( lastMemberView, frame.getClassViewPair().getReferenceClassView() );
		super.traverse( frame );
	}

	@Override
	protected void traverse( MemberViewFrame frame )
	{
		setDefaultVisitor();
		super.traverse( frame );
	}

	@Override
	protected void traverse( DimensionViewFrame frame )
	{
		setDefaultVisitor();
		super.traverse( frame );
	}

	@SuppressWarnings( "unchecked" )
	private void setVisitor( MemberView memberView, ClassView< ? > classView )
	{
		InflectionVisitor< VisitingTraverser > visitor = null;
		
		if ( memberView != null )
			visitor = (InflectionVisitor< VisitingTraverser >)visitorsInstance.getVisitor( memberView );
		
		if ( visitor == null )
		{
			ClassView< ? > determiningClassView = ( memberView == null ? classView : memberView.getReferencedClassView() );
			visitor = getVisitor( determiningClassView );
		}
		
		if ( visitor == null )
			visitor = (InflectionVisitor< VisitingTraverser >)visitorsInstance.getDefaultVisitor();
		
		super.setVisitor( visitor );
	}

	@SuppressWarnings( "unchecked" )
	private void setDefaultVisitor()
	{
		super.setVisitor( (InflectionVisitor< VisitingTraverser > )visitorsInstance.getDefaultVisitor() );
	}

	@SuppressWarnings( "unchecked" )
	private InflectionVisitor< VisitingTraverser > getVisitor( ClassView< ? > classView )
	{
		InflectionVisitor< VisitingTraverser > visitor = (InflectionVisitor< VisitingTraverser >)visitorsInstance.getVisitor( classView );
		ClassView< ? > superClassView = classView.getExtendedClassView();
		
		if ( visitor == null && superClassView != null )
			visitor = (InflectionVisitor< VisitingTraverser >)visitorsInstance.getVisitor( superClassView );
		
		return visitor;
	}
	
	protected static VisitorsInstance getVisitors( String visitorsName )
	{
		Visitors visitors = InflectionResourceLoader.getContextInflectionResourceLoader().loadVisitors( visitorsName );
		return visitors.newInstance();
	}
	
	protected VisitorsInstance getVisitorsInstance()
	{
		return visitorsInstance;
	}
}
