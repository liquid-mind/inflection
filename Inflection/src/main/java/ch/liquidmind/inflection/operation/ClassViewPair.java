package ch.liquidmind.inflection.operation;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.ClassView;

public class ClassViewPair extends IdentifiableObjectPair
{
	public ClassViewPair(
			ClassView< ? > leftClassView, ClassView< ? > rightClassView,
			Integer leftPositionMax, Integer rightPositionMax,
			Integer leftPositionCurrent, Integer rightPositionCurrent,
			Object leftIndex, Object rightIndex,
			IdentifiableObject< ?, ? > leftObject, IdentifiableObject< ?, ? > rightObject )
	{
		super( leftClassView, rightClassView, leftPositionMax, rightPositionMax, leftPositionCurrent, rightPositionCurrent, leftIndex, rightIndex, leftObject, rightObject );
	}
	
	public ClassView< ? > getLeftClassView()
	{
		return (ClassView< ? >)getLeftInflectionView();
	}

	public void setLeftClassView( ClassView< ? > classView )
	{
		setLeftInflectionView( classView );
	}
	
	public ClassView< ? > getRightClassView()
	{
		return (ClassView< ? >)getRightInflectionView();
	}

	public void setRightClassView( ClassView< ? > classView )
	{
		setRightInflectionView( classView );
	}
	
	public ClassView< ? > getReferenceClassView()
	{
		return (ClassView< ? >)getReferenceInflectionView();
	}
}
