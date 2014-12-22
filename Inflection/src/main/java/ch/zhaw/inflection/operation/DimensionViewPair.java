package ch.zhaw.inflection.operation;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.model.DimensionView;

public class DimensionViewPair extends IdentifiableObjectPair
{
	public DimensionViewPair(
			DimensionView leftDimensionView, DimensionView rightDimensionView,
			Integer leftPositionMax, Integer rightPositionMax,
			Integer leftPositionCurrent, Integer rightPositionCurrent,
			Object leftIndex, Object rightIndex,
			IdentifiableObject< ?, ? > leftObject, IdentifiableObject< ?, ? > rightObject )
	{
		super( leftDimensionView, rightDimensionView, leftPositionMax, rightPositionMax, leftPositionCurrent, rightPositionCurrent, leftIndex, rightIndex, leftObject, rightObject );
	}
	
	public DimensionView getLeftDimensionView()
	{
		return (DimensionView)getLeftInflectionView();
	}

	public void setLeftDimensionView( DimensionView dimensionView )
	{
		setLeftInflectionView( dimensionView );
	}
	
	public DimensionView getRightDimensionView()
	{
		return (DimensionView)getRightInflectionView();
	}

	public void setRightDimensionView( DimensionView dimensionView )
	{
		setRightInflectionView( dimensionView );
	}
	
	public DimensionView getReferenceDimensionView()
	{
		return (DimensionView)getReferenceInflectionView();
	}
}
