package ch.zhaw.inflection.operation.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.model.Aggregation;
import ch.zhaw.inflection.model.DimensionView;
import ch.zhaw.inflection.model.MemberView;
import ch.zhaw.inflection.operation.ClassViewFrame;
import ch.zhaw.inflection.operation.ClassViewPair;
import ch.zhaw.inflection.operation.DefaultPairingTraverser;
import ch.zhaw.inflection.operation.DimensionViewFrame;
import ch.zhaw.inflection.operation.DimensionViewPair;
import ch.zhaw.inflection.operation.IdentifiableObjectPair;
import ch.zhaw.inflection.operation.MemberViewFrame;
import ch.zhaw.inflection.operation.MemberViewPair;

public class EqualsDefaultVisitor extends EqualsAbstractVisitor
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		ClassViewPair pair = frame.getClassViewPair();
		IdentifiableObject< ?, ? > leftObject = pair.getLeftObject();
		IdentifiableObject< ?, ? > rightObject = pair.getRightObject();
		Boolean isEqual;

		if ( !areBothNullOrNotNull( leftObject, rightObject ) )
			isEqual = false;
		else if ( leftObject == null && rightObject == null )
			isEqual = true;
		else if ( getAggregation().equals( Aggregation.Composite ) )
			isEqual = compositeObjectIsEqual( frame );
		else
			isEqual = discreteObjectIsEqual( frame );
		
		returnEquality( pair, isEqual );
	}

	private Aggregation getAggregation()
	{
		Aggregation aggregation;
		
		MemberViewFrame frame = getTraverser().getLastMemberViewFrame();
		MemberViewPair pair = ( frame == null ? null : frame.getMemberViewPair() );
		MemberView memberView = ( pair == null ? null : pair.getReferenceMemberView() );
		
		if ( memberView == null )
			aggregation = Aggregation.Composite;
		else
			aggregation = memberView.getAggregation();
		
		return aggregation;
	}
	
	private Boolean compositeObjectIsEqual( ClassViewFrame frame )
	{
		ClassViewPair pair = frame.getClassViewPair();
		Boolean isEqual = null;

		if ( pair.getLeftObject().getObjectId().equals( pair.getRightObject().getObjectId() ) )
			isEqual = true;
		else if ( !pair.getLeftClassView().equals( pair.getRightClassView() ) )
			isEqual = false;
		else if ( pair.getLeftClassView().getMemberViews().size() != pair.getRightClassView().getMemberViews().size() )
			isEqual = false;
		else
		{
			getTraverser().continueTraversal();
			
			if ( getTraverser().getCurrentData().getUnequalPairs().size() > 0 )
				isEqual = false;
			else
				isEqual = true;
		}
		
		return isEqual;
	}

	private Boolean discreteObjectIsEqual( ClassViewFrame frame )
	{
		ClassViewPair pair = frame.getClassViewPair();
		Object leftObjectId = pair.getLeftObject().getObjectId();
		Object rightObjectId = pair.getRightObject().getObjectId();
		Boolean isEqual;

		if ( leftObjectId.equals( rightObjectId ) )
			isEqual = true;
		else
			isEqual = false;
		
		return isEqual;
	}
	
	@Override
	public void visit( MemberViewFrame frame )
	{
		Boolean isEqual = null;
		MemberViewPair pair = frame.getMemberViewPair();
		
		getTraverser().continueTraversal();
		
		if ( getTraverser().getCurrentData().getEqualPairs().size() != 1 )
			isEqual = false;
		else
			isEqual = true;
		
		returnEquality( pair, isEqual );
	}

	@Override
	public void visit( DimensionViewFrame frame )
	{
		DimensionViewPair pair = frame.getDimensionViewPair();
		IdentifiableObject< ?, ? > leftObject = pair.getLeftObject();
		IdentifiableObject< ?, ? > rightObject = pair.getRightObject();
		Boolean isEqual = null;

		if ( !areBothNullOrNotNull( leftObject, rightObject ) )
			isEqual = false;
		else if ( leftObject == null && rightObject == null )
			isEqual = true;
		else if ( leftObject != null && ( getSize( leftObject.getObject() ) != getSize( rightObject.getObject() ) ) )
			isEqual = false;
		
		if ( isEqual == null )
		{
			getTraverser().continueTraversal();
			
			DimensionView referenceDimensionView = pair.getReferenceDimensionView();
			
			if ( referenceDimensionView.isOrdered() )
				isEqual = areOrderingDimensionViewsEqual( leftObject, rightObject );
			else if ( referenceDimensionView.isMapped() )
				isEqual = areMappingDimensionViewsEqual( leftObject, rightObject );
			else
				isEqual = areUnqualifiedDimensionViewsEqual( leftObject, rightObject );
		}

		returnEquality( pair, isEqual );
	}

	@SuppressWarnings( "unchecked" )
	private boolean areOrderingDimensionViewsEqual( IdentifiableObject< ?, ? > leftObject, IdentifiableObject< ?, ? > rightObject )
	{
		List< Object > leftOriginalList = (List< Object >)DefaultPairingTraverser.getAsList( leftObject );
		List< Object > rightOriginalList = (List< Object >)DefaultPairingTraverser.getAsList( rightObject );
		List< Object > leftListOfEqualObjects = new ArrayList< Object >();
		List< Object > rightListOfEqualObjects = new ArrayList< Object >();
		
		for ( int i = 0 ; i < leftOriginalList.size() ; ++i )
		{
			// TODO optimize this algorithm by changing EqualsData to store the equal pairs in a Map
			// where the key is the index and the value is the object. This would avoid needing to
			// call getIdentifiableObjectPair() to locate an object with a given index. Another option
			// would be to store the equal pairs in either a List, Set or Map (dependent on the original
			// container). In this case, the three areXXXDimensionViewsEqual() would no longer be
			// necessary at all, since the the "equals" collection could be compared against the original
			// collection directly, without the need for further processing.
			Set< IdentifiableObjectPair > equalPairs = (Set< IdentifiableObjectPair >)(Object)getTraverser().getCurrentData().getEqualPairs();
			IdentifiableObjectPair equalPair = getIdentifiableObjectPair( equalPairs, i );
			
			if ( equalPair == null )
				break;

			leftListOfEqualObjects.add( (Object)equalPair.getLeftObject().getObject() );
			rightListOfEqualObjects.add( (Object)equalPair.getRightObject().getObject() );
		}
		
		return leftOriginalList.equals( leftListOfEqualObjects ) && rightOriginalList.equals( rightListOfEqualObjects );
	}

	@SuppressWarnings( "unchecked" )
	private boolean areMappingDimensionViewsEqual( IdentifiableObject< ?, ? > leftObject, IdentifiableObject< ?, ? > rightObject )
	{
		Map< Object, Object > leftOriginalMap = (Map< Object, Object >)leftObject.getObject();
		Map< Object, Object > rightOriginalMap = (Map< Object, Object >)rightObject.getObject();
		Map< Object, Object > leftMapOfEqualObjects = new HashMap< Object, Object >();
		Map< Object, Object > rightMapOfEqualObjects = new HashMap< Object, Object >();
		
		for ( Object key : leftOriginalMap.keySet() )
		{
			Set< IdentifiableObjectPair > equalPairs = (Set< IdentifiableObjectPair >)(Object)getTraverser().getCurrentData().getEqualPairs();
			IdentifiableObjectPair equalPair = getIdentifiableObjectPair( equalPairs, key );
			
			if ( equalPair == null )
				break;

			leftMapOfEqualObjects.put( key, equalPair.getLeftObject().getObject() );
			rightMapOfEqualObjects.put( key, equalPair.getRightObject().getObject() );
		}
		
		return leftOriginalMap.equals( leftMapOfEqualObjects ) && rightOriginalMap.equals( rightMapOfEqualObjects );
	}
	
	@SuppressWarnings( "unchecked" )
	private boolean areUnqualifiedDimensionViewsEqual( IdentifiableObject< ?, ? > leftObject, IdentifiableObject< ?, ? > rightObject )
	{
		Set< Object > leftOriginalSet = (Set< Object >)leftObject.getObject();
		Set< Object > rightOriginalSet = (Set< Object >)rightObject.getObject();
		Set< Object > leftSetOfEqualObjects = new HashSet< Object >();
		Set< Object > rightSetOfEqualObjects = new HashSet< Object >();
		Set< IdentifiableObjectPair > equalPairs = (Set< IdentifiableObjectPair >)(Object)getTraverser().getCurrentData().getEqualPairs();
		
		for ( IdentifiableObjectPair equalPair : equalPairs )
		{
			Object leftRawObject = ( equalPair.getLeftObject() == null ? null : equalPair.getLeftObject().getObject() );
			Object rightRawObject = ( equalPair.getRightObject() == null ? null : equalPair.getRightObject().getObject() );
			leftSetOfEqualObjects.add( leftRawObject );
			rightSetOfEqualObjects.add( rightRawObject );
		}
		
		return leftOriginalSet.equals( leftSetOfEqualObjects ) && rightOriginalSet.equals( rightSetOfEqualObjects );
	}

	private IdentifiableObjectPair getIdentifiableObjectPair( Set< IdentifiableObjectPair > pairs, Object index )
	{
		IdentifiableObjectPair foundPair = null;
		
		for ( IdentifiableObjectPair pair : pairs )
		{
			if ( pair.getReferenceIndex().equals( index ) )
			{
				foundPair = pair;
				break;
			}
		}
		
		return foundPair;
	}
	
	private int getSize( Object collectionMapOrArray )
	{
		int size;
		
		if ( collectionMapOrArray instanceof Collection )
			size = ((Collection< ? >)collectionMapOrArray).size();
		else if ( collectionMapOrArray instanceof Map )
			size = ((Map< ?, ? >)collectionMapOrArray).size();
		else if ( collectionMapOrArray.getClass().isArray() )
			size = ((Object[])collectionMapOrArray).length;
		else
			throw new IllegalStateException( "Unexpected type for collectionMapOrArray: " + collectionMapOrArray.getClass().getName() );
		
		return size;
	}
}
