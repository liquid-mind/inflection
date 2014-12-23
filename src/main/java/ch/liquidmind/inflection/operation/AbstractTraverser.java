package ch.liquidmind.inflection.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.IdentifiableObjectPool;
import ch.liquidmind.inflection.model.ClassView;
import ch.liquidmind.inflection.model.DimensionView;
import ch.liquidmind.inflection.model.HGroup;
import ch.liquidmind.inflection.model.InflectionView;
import ch.liquidmind.inflection.model.MemberView;
import ch.liquidmind.inflection.model.Multiplicity;


public abstract class AbstractTraverser implements InflectionTraverser
{
	private Map< VisitedObjectPair, VisitedObjectPair > visitedObjectPairs = new HashMap< VisitedObjectPair, VisitedObjectPair >();
	private CallStack callStack = new CallStack();
	private HGroup hGroup;

	public AbstractTraverser( HGroup hGroup )
	{
		this.hGroup = hGroup;
	}
	
	public ClassViewPair createRootClassViewPair( Object leftRootObject, Object rightRootObject, Class< ? > defaultRootClass )
	{
		ClassView< ? > leftClassView = hGroup.getClassView( getRootClass( leftRootObject, defaultRootClass ) );
		ClassView< ? > rightClassView = hGroup.getClassView( getRootClass( rightRootObject, defaultRootClass ) );
		IdentifiableObject< ?, ? > leftObject = getIdentifiableObjectPool().getIdentifiableObject( leftRootObject );
		IdentifiableObject< ?, ? > rightObject = getIdentifiableObjectPool().getIdentifiableObject( rightRootObject );
		ClassViewPair classViewPair = new ClassViewPair( leftClassView, rightClassView, 1, 1, 0, 0, null, null, leftObject, rightObject );
		
		return classViewPair;
	}
	
	private static Class< ? > getRootClass( Object object, Class< ? > defaultRootClass )
	{
		Class< ? > rootClass;
		
		if ( object != null )
			rootClass = object.getClass();
		else
			rootClass = defaultRootClass;
		
		return rootClass;
	}
	
	@Override
	public void traverse( IdentifiableObjectPair identifiableObjectPair )
	{
		visitedObjectPairs.clear();
		traverse( (InflectionViewPair)identifiableObjectPair, 0, 1 );
	}
	
	private void traverse( InflectionViewPair inflectionViewPair, int positionCurrent, int positionMax )
	{
		InflectionViewFrame frame = createFrame( inflectionViewPair, positionCurrent, positionMax );
		callStack.push( frame );
		
		if ( inflectionViewPair instanceof ClassViewPair )
			traverse( (ClassViewFrame)frame );
		else if ( inflectionViewPair instanceof MemberViewPair )
			traverse( (MemberViewFrame)frame );
		else if ( inflectionViewPair instanceof DimensionViewPair )
			traverse( (DimensionViewFrame)frame );
		else
			throw new IllegalStateException( "Unexpected type for inflectionViewPair: " + inflectionViewPair.getClass().getName() );

		callStack.pop();
	}

	protected abstract void traverse( ClassViewFrame frame );
	protected abstract void traverse( MemberViewFrame frame );
	protected abstract void traverse( DimensionViewFrame frame );
	
	@Override
	public void continueTraversal()
	{
		InflectionViewFrame frame = getCurrentFrame();
		
		if ( frame instanceof ClassViewFrame )
			continueTraversal( (ClassViewFrame)frame );
		else if ( frame instanceof MemberViewFrame )
			continueTraversal( (MemberViewFrame)frame );
		else if ( frame instanceof DimensionViewFrame )
			continueTraversal( (DimensionViewFrame)frame );
		else
			throw new IllegalStateException( "Unexpected type for frame: " + frame.getClass().getName() );
	}

	private void continueTraversal( ClassViewFrame classViewFrame )
	{
		List< MemberViewPair > memberViewPairs = createMemberViewPairs( classViewFrame.getClassViewPair() );
		int positionMax = memberViewPairs.size();
		
		for ( int positionCurrent = 0 ; positionCurrent < positionMax ; ++positionCurrent )
			traverse( memberViewPairs.get( positionCurrent ), positionCurrent, positionMax );
	}
	
	private void continueTraversal( MemberViewFrame memberViewFrame )
	{
		DimensionViewPair firstDimensionViewPair = createInitialDimensionViewPair( memberViewFrame.getMemberViewPair() );
		traverse( firstDimensionViewPair, 0, 1 );
	}
	
	private void continueTraversal( DimensionViewFrame dimensionViewFrame )
	{
		List< IdentifiableObjectPair > identifiableObjectPairs = createIdentifiableObjectPairs( dimensionViewFrame.getDimensionViewPair() );
		int positionMax = identifiableObjectPairs.size(); 
		
		for ( int positionCurrent = 0 ; positionCurrent < positionMax ; ++positionCurrent )
			traverse( identifiableObjectPairs.get( positionCurrent ), positionCurrent, positionMax );
	}

	protected List< MemberViewPair > createMemberViewPairs( ClassViewPair classViewPair )
	{
		List< MemberView > leftMemberViews = getMemberViews( classViewPair.getLeftClassView() );
		List< MemberView > rightMemberViews = getMemberViews( classViewPair.getRightClassView() );
		List< MemberView > allMemberViews = union( leftMemberViews, rightMemberViews );
		List< MemberViewPair > memberViewPairs = new ArrayList< MemberViewPair >();
		
		for ( MemberView memberView : allMemberViews )
		{
			boolean leftMemberViewExists = leftMemberViews.contains( memberView );
			boolean rightMemberViewExists = rightMemberViews.contains( memberView );
			
			MemberView leftMemberView = ( leftMemberViewExists ? memberView : null );
			MemberView rightMemberView = ( rightMemberViewExists ? memberView : null );
			Integer leftPositionMax = ( leftMemberViewExists ? leftMemberViews.size() : null );
			Integer rightPositionMax = ( rightMemberViewExists ? rightMemberViews.size() : null );
			Integer leftPositionCurrent = ( leftMemberViewExists ? leftMemberViews.indexOf( memberView ) : null );
			Integer rightPositionCurrent = ( rightMemberViewExists ? rightMemberViews.indexOf( memberView ) : null );
			
			memberViewPairs.add( new MemberViewPair( leftMemberView, rightMemberView, leftPositionMax, rightPositionMax, leftPositionCurrent, rightPositionCurrent ) );
		}

		return memberViewPairs;
	}

	private < T > List< T > union( List< T > list1, List< T > list2 )
	{
		List< T > list2Difference = new ArrayList< T >( list2 );
		list2Difference.removeAll( list1 );

		List< T > union = new ArrayList< T >();
		union.addAll( list1 );
		union.addAll( list2Difference );
		
		return union;
	}
	
	protected abstract DimensionViewPair createInitialDimensionViewPair( MemberViewPair memberViewPair );
	protected abstract List< IdentifiableObjectPair > createIdentifiableObjectPairs( DimensionViewPair dimensionViewPair );
	
	protected InflectionView getActualInflectionView( InflectionView staticInflectionView, Object object )
	{
		InflectionView actualInflectionView;
		
		if ( staticInflectionView instanceof MemberView || staticInflectionView instanceof DimensionView || object == null )
			actualInflectionView = staticInflectionView;
		else if ( object instanceof List )
			actualInflectionView = new DimensionView( true, false, true, Multiplicity.Many, object.getClass(), null );
		else if ( object instanceof Map )
			actualInflectionView = new DimensionView( false, true, true, Multiplicity.Many, object.getClass(), null );
		else if ( object instanceof Set )
			actualInflectionView = new DimensionView( false, false, true, Multiplicity.Many, object.getClass(), null );
		else if ( object.getClass().isArray() )
			actualInflectionView = new DimensionView( true, false, true, Multiplicity.Many, object.getClass(), null );
		else
			actualInflectionView = getHGroup().getClassView( object.getClass() );
		
		return actualInflectionView;
	}
	
	protected List< MemberView > getMemberViews( ClassView< ? > classView )
	{
		return ( classView == null ? new ArrayList< MemberView >() : classView.getMemberViews() );
	}
	
	protected Object createUserData()
	{
		return null;
	}
	
	private int getVisitCount( IdentifiableObjectPair identifiableObjectPair2 )
	{
		VisitedObjectPair visitedPair = new VisitedObjectPair( identifiableObjectPair2 );
		
		if ( visitedObjectPairs.containsKey( visitedPair ) )
		{
			visitedPair = visitedObjectPairs.get( visitedPair );
			visitedPair.incrementVisitCount();
		}
		else
		{
			visitedObjectPairs.put( visitedPair, visitedPair );
		}
		
		return visitedPair.getVisitCount();
	}

	public CallStack getCallStack()
	{
		return callStack;
	}
	
	@SuppressWarnings( "unchecked" )
	public < T extends InflectionViewFrame > T getFrame( int offsetFromCurrent )
	{
		return (T)callStack.peek( offsetFromCurrent );
	}
	
	@SuppressWarnings( "unchecked" )
	public < T extends InflectionViewFrame > T getCurrentFrame()
	{
		return (T)getFrame( 0 );
	}
	
	@SuppressWarnings( "unchecked" )
	public < T extends InflectionViewFrame > T getPreviousFrame()
	{
		return (T)getFrame( 1 );
	}
	
	public MemberViewFrame getLastMemberViewFrame()
	{
		MemberViewFrame memberViewFrame = null;
		
		for ( int i = 0 ; i < callStack.size() ; ++i )
		{
			InflectionViewFrame frame = callStack.get( callStack.size() - i - 1 );
			
			if ( frame instanceof MemberViewFrame )
			{
				memberViewFrame = (MemberViewFrame)frame;
				break;
			}
		}
		
		return memberViewFrame;
	}
	
	private InflectionViewFrame createFrame( InflectionViewPair inflectionViewPair, int positionCurrent, int positionMax )
	{
		InflectionViewFrame frame;
		
		if ( inflectionViewPair instanceof IdentifiableObjectPair )
			frame = createFrame( (IdentifiableObjectPair)inflectionViewPair, positionCurrent, positionMax );
		else if ( inflectionViewPair instanceof MemberViewPair )
			frame = createFrame( (MemberViewPair)inflectionViewPair, positionCurrent, positionMax );
		else
			throw new IllegalStateException( "Unexpected type for inflectionViewPair: " + inflectionViewPair.getClass().getName() );
		
		return frame;
	}

	private IdentifiableObjectFrame createFrame( IdentifiableObjectPair identifiableObjectPair, int positionCurrent, int positionMax )
	{
		IdentifiableObjectFrame frame;
		
		if ( identifiableObjectPair instanceof ClassViewPair )
			frame = createFrame( (ClassViewPair)identifiableObjectPair, positionCurrent, positionMax );
		else if ( identifiableObjectPair instanceof DimensionViewPair )
			frame = createFrame( (DimensionViewPair)identifiableObjectPair, positionCurrent, positionMax );
		else
			throw new IllegalStateException( "Unexpected type for identifiableObjectPair: " + identifiableObjectPair.getClass().getName() );
		
		return frame;
	}
	
	private ClassViewFrame createFrame( ClassViewPair classViewPair, int positionCurrent, int positionMax )
	{
		return new ClassViewFrame( classViewPair, positionCurrent, positionMax, createUserData(), getVisitCount( classViewPair ) );
	}

	private DimensionViewFrame createFrame( DimensionViewPair dimensionViewPair, int positionCurrent, int positionMax )
	{
		return new DimensionViewFrame( dimensionViewPair, positionCurrent, positionMax, createUserData(), getVisitCount( dimensionViewPair ) );
	}
		
	private MemberViewFrame createFrame( MemberViewPair memberViewPair, int positionCurrent, int positionMax )
	{
		return new MemberViewFrame( memberViewPair, positionCurrent, positionMax, createUserData() );
	}

	protected IdentifiableObjectPool getIdentifiableObjectPool()
	{
		return IdentifiableObjectPool.getIdentifiableObjectPool();
	}

	public HGroup getHGroup()
	{
		return hGroup;
	}
}
