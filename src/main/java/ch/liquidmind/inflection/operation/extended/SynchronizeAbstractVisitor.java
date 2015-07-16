package ch.liquidmind.inflection.operation.extended;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import __java.lang.__Class;
import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.IdentifiableObjectPool;
import ch.liquidmind.inflection.model.DimensionView;
import ch.liquidmind.inflection.model.MemberView;
import ch.liquidmind.inflection.model.Multiplicity;
import ch.liquidmind.inflection.operation.AbstractVisitor;
import ch.liquidmind.inflection.operation.ClassViewFrame;
import ch.liquidmind.inflection.operation.ClassViewPair;
import ch.liquidmind.inflection.operation.DimensionViewFrame;
import ch.liquidmind.inflection.operation.DimensionViewPair;
import ch.liquidmind.inflection.operation.IdentifiableObjectFrame;
import ch.liquidmind.inflection.operation.InflectionViewFrame;

public abstract class SynchronizeAbstractVisitor extends AbstractVisitor< SynchronizeTraverser >
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		ClassViewPair classViewPair = frame.getClassViewPair();
		IdentifiableObject< ?, ? > leftIdentifiableObject = classViewPair.getLeftObject();
		Object leftObject = ( leftIdentifiableObject == null ? null : leftIdentifiableObject.getObject() );
		IdentifiableObject< ?, ? > rightIdentifiableObject = classViewPair.getRightObject();
		Object rightObject = ( rightIdentifiableObject == null ? null : rightIdentifiableObject.getObject() );

		if ( leftObject != null )
		{
			Object rightObjectSynchronized = synchronizeRightObject( classViewPair.getLeftClassView().getJavaClass(), leftObject, rightObject );
			
			IdentifiableObject< ?, ? > rightIdentifiableObjectSynchronized =
				IdentifiableObjectPool.getIdentifiableObjectPool().getIdentifiableObject( rightObjectSynchronized );

			classViewPair.setRightClassView( classViewPair.getLeftClassView() );
			classViewPair.setRightPositionCurrent( classViewPair.getRightPositionCurrent() );
			classViewPair.setRightPositionMax( classViewPair.getRightPositionMax() );
			classViewPair.setRightObject( rightIdentifiableObjectSynchronized );
			classViewPair.setRightIndex( classViewPair.getLeftIndex() );
		}
		
		setJavaReference();
		getTraverser().continueTraversal();
	}
	
	@Override
	public void visit( DimensionViewFrame frame )
	{
		DimensionViewPair dimensionViewPair = frame.getDimensionViewPair();
		Class< ? > leftType = (Class< ? >)dimensionViewPair.getLeftDimensionView().getDimensionType();
		IdentifiableObject< ?, ? > leftIdentifiableObject = dimensionViewPair.getLeftObject();
		Object leftObject = ( leftIdentifiableObject == null ? null : leftIdentifiableObject.getObject() );
		IdentifiableObject< ?, ? > rightIdentifiableObject = dimensionViewPair.getRightObject();
		Object rightObject = ( rightIdentifiableObject == null ? null : rightIdentifiableObject.getObject() );
		
		if ( leftObject != null && rightObject == null )
		{
			Object rightObjectSynchronized;
			
			if ( Collection.class.isAssignableFrom( leftObject.getClass() ) )
			{
				rightObjectSynchronized = __Class.newInstance( leftType );
			}
			else
			{
				throw new IllegalStateException( "Unexpected type for leftType: " + leftType.getName() );
			}

			IdentifiableObject< ?, ? > rightIdentifiableObjectSynchronized =
					IdentifiableObjectPool.getIdentifiableObjectPool().getIdentifiableObject( rightObjectSynchronized );
			
			dimensionViewPair.setRightDimensionView( dimensionViewPair.getLeftDimensionView() );
			dimensionViewPair.setRightIndex( dimensionViewPair.getLeftIndex() );
			dimensionViewPair.setRightObject( rightIdentifiableObjectSynchronized );
			dimensionViewPair.setRightPositionCurrent( dimensionViewPair.getLeftPositionCurrent() );
			dimensionViewPair.setRightPositionMax( dimensionViewPair.getLeftPositionMax() );	
		}
		
		setJavaReference();
		getTraverser().continueTraversal();
	}

	@SuppressWarnings( "unchecked" )
	private void setJavaReference()
	{
		InflectionViewFrame previousFrame = getTraverser().getPreviousFrame();
		
		if ( previousFrame == null || !( previousFrame instanceof DimensionViewFrame ) )
			return;
		
		IdentifiableObjectFrame currentFrame = getTraverser().getCurrentFrame();
		DimensionViewFrame dimensionViewFrame = (DimensionViewFrame)previousFrame;
		DimensionView rightDimensionView = dimensionViewFrame.getDimensionViewPair().getRightDimensionView();
		
		Object rightObject = currentFrame.getIdentifiableObjectPair().getRightObject().getObject();
		
		if ( rightDimensionView.getMultiplicity().equals( Multiplicity.One ) )
		{
			ClassViewFrame classViewFrame = getLastClassViewFrame();
			Object rightContainingObject = classViewFrame.getIdentifiableObjectPair().getRightObject().getObject();
			MemberView rightMemberView = (MemberView)rightDimensionView.getParentView();
			rightMemberView.setMemberInstance( rightContainingObject, rightObject );
		}
		else if ( rightDimensionView.getMultiplicity().equals( Multiplicity.Many ) )
		{
			Class< ? > dimensionType = (Class< ? >)rightDimensionView.getDimensionType();
			
			if ( List.class.isAssignableFrom( dimensionType ) )
			{
				List< Object > listDimension = (List< Object >)dimensionViewFrame.getDimensionViewPair().getRightObject().getObject();
				int rightIndex = (Integer)currentFrame.getIdentifiableObjectPair().getRightIndex();
				ensureCapacity( listDimension, rightIndex + 1 );
				listDimension.set( rightIndex, rightObject );
			}
			else if ( Set.class.isAssignableFrom( dimensionType ) )
			{
				// TODO: implement missing collection/array types.
				throw new UnsupportedOperationException();
			}
			else if ( Map.class.isAssignableFrom( dimensionType ) )
			{
				throw new UnsupportedOperationException();
			}
			else if ( dimensionType.isArray() )
			{
				throw new UnsupportedOperationException();
			}
			else
			{
				throw new IllegalStateException( "Unexpected type for dimensionType: " + dimensionType.getName() );
			}
			
		}
		else
		{
			throw new IllegalStateException( "Unexpected value for rightDimensionView.getMultiplicity(): " + rightDimensionView.getMultiplicity() );
		}
	}
	
	private void ensureCapacity( List< ? > list, int newSize )
	{
		if ( list.size() >= newSize )
			return;
		
		int difference = newSize - list.size();
		
		for ( int i = 0 ; i < difference ; ++i )
			list.add( null );
	}
	
	private ClassViewFrame getLastClassViewFrame()
	{
		int offset = 0;
		
		if ( getTraverser().getCurrentFrame() instanceof ClassViewFrame )
			offset = 1;
		
		ClassViewFrame classViewFrame = getTraverser().getLastFrameOfType( ClassViewFrame.class, offset );
		
		return classViewFrame;
	}
	
	protected abstract Object synchronizeRightObject( Class< ? > rightClass, Object leftObject, Object rightObject );
}
