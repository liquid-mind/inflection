package ch.liquidmind.inflection.operation;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.InflectionView;

public abstract class IdentifiableObjectPair extends InflectionViewPair
{
	private Object leftIndex;
	private Object rightIndex;
	private IdentifiableObject< ?, ? > leftObject;
	private IdentifiableObject< ?, ? > rightObject;
	
	public IdentifiableObjectPair(
			InflectionView leftInflectionView, InflectionView rightInflectionView,
			Integer leftPositionMax, Integer rightPositionMax,
			Integer leftPositionCurrent, Integer rightPositionCurrent,
			Object leftIndex, Object rightIndex,
			IdentifiableObject< ?, ? > leftObject, IdentifiableObject< ?, ? > rightObject )
	{
		super( leftInflectionView, rightInflectionView, leftPositionMax, rightPositionMax, leftPositionCurrent, rightPositionCurrent );
		this.leftIndex = leftIndex;
		this.rightIndex = rightIndex;
		this.leftObject = leftObject;
		this.rightObject = rightObject;
	}
	
	public Object getLeftIndex()
	{
		return leftIndex;
	}

	public void setLeftIndex( Object leftIndex )
	{
		this.leftIndex = leftIndex;
	}

	public Object getRightIndex()
	{
		return rightIndex;
	}

	public void setRightIndex( Object rightIndex )
	{
		this.rightIndex = rightIndex;
	}

	public IdentifiableObject< ?, ? > getLeftObject()
	{
		return leftObject;
	}

	public void setLeftObject( IdentifiableObject< ?, ? > leftObject )
	{
		this.leftObject = leftObject;
	}

	public IdentifiableObject< ?, ? > getRightObject()
	{
		return rightObject;
	}

	public void setRightObject( IdentifiableObject< ?, ? > rightObject )
	{
		this.rightObject = rightObject;
	}
	
	public IdentifiableObject< ?, ? > getReferenceObject()
	{
		return leftObject == null ? rightObject : leftObject;
	}
	
	public Object getReferenceIndex()
	{
		return leftIndex == null ? rightIndex : leftIndex;
	}
}
