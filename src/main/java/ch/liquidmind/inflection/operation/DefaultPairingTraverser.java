package ch.liquidmind.inflection.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.ClassView;
import ch.liquidmind.inflection.model.DimensionView;
import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.InflectionView;
import ch.liquidmind.inflection.model.MemberView;
import ch.liquidmind.inflection.model.Multiplicity;
import ch.liquidmind.inflection.model.VisitorsInstance;

public abstract class DefaultPairingTraverser extends ConfigurableVisitingTraverser
{
	public DefaultPairingTraverser( Taxonomy taxonomy, VisitorsInstance configurationInstance )
	{
		super( taxonomy, configurationInstance );
	}

	@Override
	protected DimensionViewPair createInitialDimensionViewPair( MemberViewPair memberViewPair )
	{
		ClassViewFrame classViewFrame = getFrame( 1 );
		ClassViewPair classViewPair = classViewFrame.getClassViewPair();
		MemberView leftMemberView = memberViewPair.getLeftMemberView();
		MemberView rightMemberView = memberViewPair.getRightMemberView();
		DimensionView leftDimensionView = ( leftMemberView == null ? null : leftMemberView.getInitialDimensionView() );
		DimensionView rightDimensionView = ( rightMemberView == null ? null : rightMemberView.getInitialDimensionView() );
		IdentifiableObject< ?, ? > leftMemberViewObject = getMemberViewObject( classViewPair.getLeftObject(), leftMemberView );
		IdentifiableObject< ?, ? > rightMemberViewObject = getMemberViewObject( classViewPair.getRightObject(), rightMemberView );
		
		DimensionViewPair dimensionViewPair = new DimensionViewPair( leftDimensionView, rightDimensionView, 1, 1, 0, 0, null, null, leftMemberViewObject, rightMemberViewObject );
		
		return dimensionViewPair;
	}
	
	private IdentifiableObject< ?, ? > getMemberViewObject( IdentifiableObject< ?, ? > classViewObject, MemberView memberView )
	{
		Object parentObjectRaw = ( classViewObject == null ? null : classViewObject.getObject() );
		Object objectRaw = ( parentObjectRaw == null ? null : memberView.getMemberInstance( parentObjectRaw ) );
		IdentifiableObject< ?, ? > memberViewObject;
		
		if ( memberView == null )
		{
			memberViewObject = null;
		}
		else if ( memberView.getInitialDimensionView().getMultiplicity().equals( Multiplicity.One ) )
		{
			// TODO Refactor model classes so that in DimensionView points to HashSet in this case
			// rather than the type of the object pointed to by the dimension.
			Set< Object > toOneSet = new HashSet< Object >();
			toOneSet.add( objectRaw );
			memberViewObject = getIdentifiableObjectPool().getIdentifiableObject( toOneSet );
		}
		else
		{
			memberViewObject = getIdentifiableObjectPool().getIdentifiableObject( objectRaw );
		}

		return memberViewObject;
	}

	@Override
	protected List< IdentifiableObjectPair > createIdentifiableObjectPairs( DimensionViewPair dimensionViewPair )
	{
		List< IdentifiableObjectPair > identifiableObjectPairs;

		// TODO Look into what happens when the type of DimensionView (isOrdered, isMapped, etc.)
		// on the left and right are not the same (can happen in the case of dynamic views).
		DimensionView dimensionView = dimensionViewPair.getReferenceDimensionView();
		
		if ( dimensionView == null )
			identifiableObjectPairs = new ArrayList< IdentifiableObjectPair >();
		else if ( dimensionView.isOrdered() )
			identifiableObjectPairs = createIdentifiableObjectPairsFromOrderedDimension( dimensionViewPair );
		else if ( dimensionView.isMapped() )
			identifiableObjectPairs = createIdentifiableObjectPairsFromMappedDimension( dimensionViewPair );
		else
			identifiableObjectPairs = createIdentifiableObjectPairsFromUnorderedDimension( dimensionViewPair );
		
		return identifiableObjectPairs;
	}
	
	protected List< IdentifiableObjectPair > createIdentifiableObjectPairsFromOrderedDimension( DimensionViewPair dimensionViewPair )
	{
		List< ? > leftParentAsList = getAsList( dimensionViewPair.getLeftObject() );
		List< ? > rightParentAsList = getAsList( dimensionViewPair.getRightObject() );
		int leftSize = leftParentAsList.size();
		int rightSize = rightParentAsList.size();
		int maxSize = ( leftSize > rightSize ? leftSize : rightSize );
		
		List< IdentifiableObjectPair > identifiableObjectPairs = new ArrayList< IdentifiableObjectPair >();
		
		for ( int i = 0 ; i < maxSize ; ++i )
		{
			InflectionView leftStaticView = getNextStaticView( getLastMemberViewFrame().getMemberViewPair().getLeftMemberView(), dimensionViewPair.getLeftDimensionView() );
			InflectionView rightStaticView = getNextStaticView( getLastMemberViewFrame().getMemberViewPair().getRightMemberView(), dimensionViewPair.getRightDimensionView() );
			PairInfo leftPairInfo = getOrderedPairInfo( leftStaticView, i, leftSize, leftParentAsList );
			PairInfo rightPairInfo = getOrderedPairInfo( rightStaticView, i, rightSize, rightParentAsList );
			identifiableObjectPairs.add( createIdentifiableObjectPair( leftPairInfo.inflectionView, rightPairInfo.inflectionView,
				leftPairInfo.positionMax, leftPairInfo.positionCurrent, rightPairInfo.positionMax, rightPairInfo.positionCurrent, 
				leftPairInfo.index, rightPairInfo.index, leftPairInfo.object, rightPairInfo.object ) );
		}
		
		return identifiableObjectPairs;
	}
	
	protected InflectionView getNextStaticView( MemberView memberView, DimensionView dimensionView)
	{
		return memberView == null ? null : memberView.getNextView( dimensionView );
	}
	
	public static List< ? > getAsList( IdentifiableObject< ?, ? > identifiableObject )
	{
		List< ? > resultList;
		
		if ( identifiableObject == null )
		{
			resultList = new ArrayList< Object >();
		}
		else
		{
			Object arrayOrList = identifiableObject.getObject();
			
			if ( arrayOrList instanceof List )
				resultList = (List< ? >)arrayOrList;
			else if ( arrayOrList.getClass().isArray() )
				resultList = Arrays.asList( (Object[])arrayOrList );
			else
				throw new IllegalStateException( "Invalid type for arrayOrList: " + arrayOrList.getClass().getName() );
		}
		
		return resultList;
	}

	private static class PairInfo
	{
		public InflectionView inflectionView = null;
		public Integer positionCurrent = null;
		public Integer positionMax = null;
		public Object index = null;
		public IdentifiableObject< ?, ? > object = null;
	}
	
	private PairInfo getOrderedPairInfo( InflectionView staticInflectionView, int i, int size, List< ? > parentAsList )
	{
		PairInfo pairInfo = new PairInfo();
		
		if ( i < size )
		{
			Object object = parentAsList.get( i );
			
			pairInfo.inflectionView = getActualInflectionView( staticInflectionView, object );
			pairInfo.positionCurrent = i;
			pairInfo.positionMax = size;
			pairInfo.index = i;
			pairInfo.object = getIdentifiableObjectPool().getIdentifiableObject( object );
		}
		
		return pairInfo;
	}
	
	protected List< IdentifiableObjectPair > createIdentifiableObjectPairsFromMappedDimension( DimensionViewPair dimensionViewPair )
	{
		IdentifiableObject< ?, ? > leftObject = dimensionViewPair.getLeftObject();
		IdentifiableObject< ?, ? > rightObject = dimensionViewPair.getRightObject();
		Map< ?, ? > leftParentAsMap = ( leftObject == null ? new HashMap< Object, Object >() : (Map< ?, ? >)leftObject.getObject() );
		Map< ?, ? > rightParentAsMap = ( rightObject == null ? new HashMap< Object, Object >() : (Map< ?, ? >)rightObject.getObject() );
		Set< Object > keySet = new HashSet< Object >();
		keySet.addAll( leftParentAsMap.keySet() );
		keySet.addAll( rightParentAsMap.keySet() );
		List< Object > keyList = new ArrayList< Object >( keySet );
		int leftSize = leftParentAsMap.size();
		int rightSize = rightParentAsMap.size();
		int maxSize = keyList.size();

		List< IdentifiableObjectPair > identifiableObjectPairs = new ArrayList< IdentifiableObjectPair >();
		
		for ( int i = 0 ; i < maxSize ; ++i )
		{
			Object key = keyList.get( i );
			InflectionView leftStaticView = getNextStaticView( getLastMemberViewFrame().getMemberViewPair().getLeftMemberView(), dimensionViewPair.getLeftDimensionView() );
			InflectionView rightStaticView = getNextStaticView( getLastMemberViewFrame().getMemberViewPair().getRightMemberView(), dimensionViewPair.getRightDimensionView() );
			PairInfo leftPairInfo = getMappedPairInfo( leftStaticView, key, i, leftSize, leftParentAsMap );
			PairInfo rightPairInfo = getMappedPairInfo( rightStaticView, key, i, rightSize, rightParentAsMap );
			identifiableObjectPairs.add( createIdentifiableObjectPair( leftPairInfo.inflectionView, rightPairInfo.inflectionView, 
				leftPairInfo.positionMax, leftPairInfo.positionCurrent, rightPairInfo.positionMax, rightPairInfo.positionCurrent, 
				leftPairInfo.index, rightPairInfo.index, leftPairInfo.object, rightPairInfo.object ) );
		}
		
		return identifiableObjectPairs;
	}

	private PairInfo getMappedPairInfo( InflectionView staticInflectionView, Object key, int i, int size, Map< ?, ? > parentAsMap )
	{
		PairInfo pairInfo = new PairInfo();
		
		if ( parentAsMap.keySet().contains( key ) )
		{
			Object object = parentAsMap.get( key );
			
			pairInfo.inflectionView = getActualInflectionView( staticInflectionView, object );
			pairInfo.positionCurrent = i;
			pairInfo.positionMax = size;
			pairInfo.index = key;
			pairInfo.object = getIdentifiableObjectPool().getIdentifiableObject( object );
		}
		
		return pairInfo;
	}
	
	protected IdentifiableObjectPair createIdentifiableObjectPair( InflectionView leftInflectionView, InflectionView rightInflectionView, Integer leftPositionMax, Integer leftPositionCurrent, Integer rightPositionMax, Integer rightPositionCurrent, Object leftIndex, Object rightIndex, IdentifiableObject< ?, ? > leftObject, IdentifiableObject< ?, ? > rightObject )
	{
		IdentifiableObjectPair identifiableObjectPair;
		
		// TODO Look into what happens when the leftInflectionView and rightInflectionView
		// are not of the same subclass (can happen due to dynamic typing).
		
		InflectionView referenceInflectionView = ( leftInflectionView == null ? rightInflectionView : leftInflectionView );
		
		if ( referenceInflectionView instanceof ClassView )
			identifiableObjectPair = new ClassViewPair( (ClassView< ? >)leftInflectionView, (ClassView< ? >)rightInflectionView, leftPositionMax, leftPositionCurrent, rightPositionMax, rightPositionCurrent, leftIndex, rightIndex, leftObject, rightObject );
		else if ( referenceInflectionView instanceof DimensionView )
			identifiableObjectPair = new DimensionViewPair( (DimensionView)leftInflectionView, (DimensionView)rightInflectionView, leftPositionMax, leftPositionCurrent, rightPositionMax, rightPositionCurrent, leftIndex, rightIndex, leftObject, rightObject );
		else
			throw new IllegalStateException( "Invalid type for inflectionView: " + referenceInflectionView.getClass().getName() );
			
		return identifiableObjectPair;
	}
	
	protected List< IdentifiableObjectPair > createIdentifiableObjectPairsFromUnorderedDimension( DimensionViewPair dimensionViewPair )
	{
		IdentifiableObject< ?, ? > leftObject = dimensionViewPair.getLeftObject();
		IdentifiableObject< ?, ? > rightObject = dimensionViewPair.getRightObject();
		Set< ? > leftParentAsSet = ( leftObject == null ? new HashSet< Object >() : (Set< ? >)leftObject.getObject() );
		Set< ? > rightParentAsSet = ( rightObject == null ? new HashSet< Object >() : (Set< ? >)rightObject.getObject() );
		int leftSize = leftParentAsSet.size();
		int rightSize = rightParentAsSet.size();
		int maxSize = leftSize * rightSize;
		Iterator< ? > leftIter = leftParentAsSet.iterator();

		List< IdentifiableObjectPair > identifiableObjectPairs = new ArrayList< IdentifiableObjectPair >();
		
		for ( int i = 0 ; i < leftSize ; ++i )
		{
			Object leftRawObject = leftIter.next();
			IdentifiableObject< ?, ? > leftChildObject = getIdentifiableObjectPool().getIdentifiableObject( leftRawObject );
			InflectionView leftStaticView = getNextStaticView( getLastMemberViewFrame().getMemberViewPair().getLeftMemberView(), dimensionViewPair.getLeftDimensionView() );
			InflectionView leftInflectionView = getActualInflectionView( leftStaticView, leftRawObject );
			Iterator< ? > rightIter = rightParentAsSet.iterator();
			
			for  ( int j = 0 ; j < rightSize ; ++j )
			{
				Object rightRawObject = rightIter.next();
				IdentifiableObject< ?, ? > rightChildObject = getIdentifiableObjectPool().getIdentifiableObject( rightRawObject );
				InflectionView rightStaticView = getNextStaticView( getLastMemberViewFrame().getMemberViewPair().getRightMemberView(), dimensionViewPair.getRightDimensionView() );
				InflectionView rightInflectionView = getActualInflectionView( rightStaticView, rightRawObject );
				int positionCurrent = i * rightSize + j;
				identifiableObjectPairs.add( createIdentifiableObjectPair( leftInflectionView, rightInflectionView, maxSize, positionCurrent, maxSize, positionCurrent, null, null, leftChildObject, rightChildObject ) );
			}
		}

		return identifiableObjectPairs;
	}
}
