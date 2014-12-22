package ch.zhaw.inflection.operation.basic;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.operation.ClassViewFrame;
import ch.zhaw.inflection.operation.DimensionViewFrame;
import ch.zhaw.inflection.operation.IdentifiableObjectFrame;
import ch.zhaw.inflection.operation.IdentifiableObjectPair;
import ch.zhaw.inflection.operation.InflectionViewFrame;
import ch.zhaw.inflection.operation.InflectionViewPair;
import ch.zhaw.inflection.operation.MemberViewFrame;

// TODO Might refactor this to work with left AND right sides simultaneously.
public class MetaModelToTextVisitor extends IndentingPrintWriterVisitor< MetaModelToTextTraverser >
{
	@Override
	public void visit( ClassViewFrame frame )
	{
		print( "ClassViewFrame: " );
		print( frame );
		println();
		continueTraversal();
	}

	@Override
	public void visit( MemberViewFrame frame )
	{
		print( "MemberViewFrame: " );
		print( frame );
		println();
		continueTraversal();
	}

	@Override
	public void visit( DimensionViewFrame frame )
	{
		print( "DimensionViewFrame: " );
		print( frame );
		println();
		continueTraversal();
	}
	
	private void print( IdentifiableObjectFrame frame )
	{
		IdentifiableObjectPair pair = frame.getIdentifiableObjectPair();
		IdentifiableObject< ?, ? > leftObject = pair.getLeftObject();
		
		print( (InflectionViewFrame)frame );
		print(
			", " + 
			"leftIndex=" + pair.getLeftIndex() + ", " +
			"leftObjectId=" + ( leftObject == null ? "NA" : leftObject.getObjectId() ) + ", " +
			"leftClass=" + ( leftObject == null ? "NA" : leftObject.getObject().getClass().getName() ) );
	}
	
	private void print( InflectionViewFrame frame )
	{
		InflectionViewPair pair = frame.getInflectionViewPair();
		
		print(
			"framePosCurrent=" + frame.getPositionCurrent() + ", " +
			"framePosMax=" + frame.getPositionMax() + ", " +
			"inflectionView=" + pair.getLeftInflectionView().getName() + ", " +
			"leftPosCurrent=" + pair.getLeftPositionCurrent() + ", " +
			"leftPosMax=" + pair.getLeftPositionMax() );
	}

	private void continueTraversal()
	{
		increaseIndent();
		getTraverser().continueTraversal();
		decreaseIndent();
	}
}
