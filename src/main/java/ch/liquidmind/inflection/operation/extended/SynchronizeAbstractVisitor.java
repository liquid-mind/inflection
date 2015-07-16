package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.IdentifiableObjectPool;
import ch.liquidmind.inflection.operation.AbstractVisitor;
import ch.liquidmind.inflection.operation.ClassViewFrame;
import ch.liquidmind.inflection.operation.ClassViewPair;

public abstract class SynchronizeAbstractVisitor extends AbstractVisitor< SynchronizeTraverser >
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		ClassViewPair classViewPair = (ClassViewPair)frame.getIdentifiableObjectPair();
		IdentifiableObject< ?, ? > leftIdentifiableObject = classViewPair.getLeftObject();
		IdentifiableObject< ?, ? > rightIdentifiableObject = classViewPair.getRightObject();

		if ( leftIdentifiableObject !=null && rightIdentifiableObject == null )
		{
			Object rightObject = createRightObject( classViewPair.getLeftClassView().getJavaClass(), leftIdentifiableObject.getObject() );
			
			rightIdentifiableObject =
				IdentifiableObjectPool.getIdentifiableObjectPool().getIdentifiableObject( rightObject );

			classViewPair.setRightInflectionView( classViewPair.getLeftInflectionView() );
			classViewPair.setRightPositionCurrent( classViewPair.getRightPositionCurrent() );
			classViewPair.setRightPositionMax( classViewPair.getRightPositionMax() );
			classViewPair.setRightObject( rightIdentifiableObject );
		}

		getTraverser().continueTraversal();
	}
	
	protected abstract Object createRightObject( Class< ? > rightClass, Object leftObject );
}
