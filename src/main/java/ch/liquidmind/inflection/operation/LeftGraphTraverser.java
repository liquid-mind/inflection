package ch.liquidmind.inflection.operation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.InflectionView;
import ch.liquidmind.inflection.model.VisitorsInstance;

public class LeftGraphTraverser extends DefaultPairingTraverser
{
	public LeftGraphTraverser( Taxonomy taxonomy, VisitorsInstance configurationInstance )
	{
		super( taxonomy, configurationInstance );
	}

	@Override
	protected List< IdentifiableObjectPair > createIdentifiableObjectPairsFromUnorderedDimension( DimensionViewPair dimensionViewPair )
	{
		IdentifiableObject< ?, ? > leftObject = dimensionViewPair.getLeftObject();
		Set< ? > leftParentAsSet = ( leftObject == null ? new HashSet< Object >() : (Set< ? >)leftObject.getObject() );
		int leftSize = leftParentAsSet.size();
		Iterator< ? > leftIter = leftParentAsSet.iterator();

		List< IdentifiableObjectPair > identifiableObjectPairs = new ArrayList< IdentifiableObjectPair >();
		
		for ( int i = 0 ; i < leftSize ; ++i )
		{
			IdentifiableObject< ?, ? > leftChildObject = getIdentifiableObjectPool().getIdentifiableObject( leftIter.next() );
			Object leftRawChildObject = ( leftChildObject == null ? null : leftChildObject.getObject() );
			InflectionView staticView = getNextStaticView( getLastMemberViewFrame().getMemberViewPair().getLeftMemberView(), dimensionViewPair.getLeftDimensionView() );
			InflectionView leftInflectionView = getActualInflectionView( staticView, leftRawChildObject );
			identifiableObjectPairs.add( createIdentifiableObjectPair( leftInflectionView, null, leftSize, i, null, null, i, null, leftChildObject, null ) );
		}

		return identifiableObjectPairs;
	}
}
