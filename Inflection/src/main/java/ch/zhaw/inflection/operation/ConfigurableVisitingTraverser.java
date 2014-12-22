package ch.zhaw.inflection.operation;

import ch.zhaw.inflection.InflectionResourceLoader;
import ch.zhaw.inflection.model.ClassView;
import ch.zhaw.inflection.model.HGroup;
import ch.zhaw.inflection.model.MemberView;
import ch.zhaw.inflection.model.VMap;
import ch.zhaw.inflection.model.VmapInstance;

public abstract class ConfigurableVisitingTraverser extends VisitingTraverser
{
	public static final String CONFIGURATION_SUFFIX = "Configuration";
	
	// TODO Vmap and VmapInstance are being refered to in many places as "configuration"; fix naming.
	private VmapInstance configurationInstance;
	
	public ConfigurableVisitingTraverser( HGroup hGroup, VmapInstance configurationInstance )
	{
		super( hGroup, null );
		this.configurationInstance = configurationInstance;
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
			visitor = (InflectionVisitor< VisitingTraverser >)configurationInstance.getVisitor( memberView );
		
		if ( visitor == null )
		{
			ClassView< ? > determiningClassView = ( memberView == null ? classView : memberView.getReferencedClassView() );
			visitor = getVisitor( determiningClassView );
		}
		
		if ( visitor == null )
			visitor = (InflectionVisitor< VisitingTraverser >)configurationInstance.getDefaultVisitor();
		
		super.setVisitor( visitor );
	}

	@SuppressWarnings( "unchecked" )
	private void setDefaultVisitor()
	{
		super.setVisitor( (InflectionVisitor< VisitingTraverser > )configurationInstance.getDefaultVisitor() );
	}

	@SuppressWarnings( "unchecked" )
	private InflectionVisitor< VisitingTraverser > getVisitor( ClassView< ? > classView )
	{
		InflectionVisitor< VisitingTraverser > visitor = (InflectionVisitor< VisitingTraverser >)configurationInstance.getVisitor( classView );
		ClassView< ? > superClassView = classView.getExtendedClassView();
		
		if ( visitor == null && superClassView != null )
			visitor = (InflectionVisitor< VisitingTraverser >)configurationInstance.getVisitor( superClassView );
		
		return visitor;
	}
	
	protected static VmapInstance getConfiguration( String configurationName )
	{
		VMap vmap = InflectionResourceLoader.getContextInflectionResourceLoader().loadVmap( configurationName );
		return vmap.newInstance();
	}
	
	protected VmapInstance getVmapInstance()
	{
		return configurationInstance;
	}
}
